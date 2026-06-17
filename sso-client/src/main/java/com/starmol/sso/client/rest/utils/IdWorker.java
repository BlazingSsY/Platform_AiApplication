package com.starmol.sso.client.rest.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author huguojun
 */
@Slf4j
public class IdWorker {

	private static final IdWorker INSTANCE = new IdWorker();
	private static final String AT = "@";
	/**
	 * 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
	 */
	private final long twepoch = 1288834974657L;
	/**
	 * 机器标识位数
	 */
	private final long workerIdBits = 3L;
	private final long datacenterIdBits = 3L;
	private final long maxWorkerId = ~(-1L << workerIdBits);
	private final long maxDatacenterId = ~(-1L << datacenterIdBits);
	/**
	 * 毫秒内自增位
	 */
	private final long sequenceBits = 3L;
	private final long workerIdShift = sequenceBits;
	private final long datacenterIdShift = sequenceBits + workerIdBits;
	/**
	 * 时间戳左移动位
	 */
	private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
	private final long sequenceMask = ~(-1L << sequenceBits);
	private final AtomicLong now = new AtomicLong(System.currentTimeMillis());
	private long workerId = 0;
	/**
	 * 数据标识 ID 部分
	 */
	private long datacenterId = 0;
	/**
	 * 并发控制
	 */
	private long sequence = 0L;
	/**
	 * 上次生产 ID 时间戳
	 */
	private long lastTimestamp = -1L;

	public IdWorker() {
		this.datacenterId = getDatacenterId(maxDatacenterId);
		this.workerId = getMaxWorkerId(datacenterId, maxWorkerId);
	}

	/**
	 * 获取 maxWorkerId
	 */
	protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
		StringBuilder mpid = new StringBuilder();
		mpid.append(datacenterId);
		String name = ManagementFactory.getRuntimeMXBean().getName();
		if (StringUtils.isNotBlank(name)) {
			/*
			 * GET jvmPid
			 */
			mpid.append(name.split(AT)[0]);
		}
		/*
		 * MAC + PID 的 hashcode 获取16个低位
		 */
		return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
	}

	/**
	 * 数据标识id部分
	 */
	protected static long getDatacenterId(long maxDatacenterId) {
		long id = 0L;
		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			if (network == null) {
				id = 1L;
			} else {
				byte[] mac = network.getHardwareAddress();
				if (null != mac) {
					id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
					id = id % (maxDatacenterId + 1);
				}
			}
		} catch (Exception e) {
			log.warn(" getDatacenterId: " + e.getMessage());
		}
		return id;
	}

	public static Long getId() {
		return INSTANCE._next();
	}

	public static IdWorker getInstance() {
		return INSTANCE;
	}

	/**
	 * 获取下一个 ID
	 *
	 * @return 下一个 ID
	 */
	public synchronized Long _next() {
		long timestamp = timeGen();
		//闰秒
		if (timestamp < lastTimestamp) {
			long offset = lastTimestamp - timestamp;
			if (offset <= 5) {
				try {
					wait(offset << 1);
					timestamp = timeGen();
					if (timestamp < lastTimestamp) {
						throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
			}
		}

		if (lastTimestamp == timestamp) {
			// 相同毫秒内，序列号自增
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) {
				// 同一毫秒的序列数已经达到最大
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			// 不同毫秒内，序列号置为 1 - 3 随机数
			sequence = ThreadLocalRandom.current().nextLong(1, 3);
		}

		lastTimestamp = timestamp;

		// 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
		return ((timestamp - twepoch) << timestampLeftShift)
				| (datacenterId << datacenterIdShift)
				| (workerId << workerIdShift)
				| sequence;
	}

	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	protected long timeGen() {
		return SystemClock.now();
	}

	public Number nextId() {
		return getId();
	}

	public String nextUUID() {
		return String.valueOf(getId());
	}

	public static class SystemClock {

		private final long period;
		private final AtomicLong now;

		private SystemClock(long period) {
			this.period = period;
			this.now = new AtomicLong(System.currentTimeMillis());
			scheduleClockUpdating();
		}

		private static SystemClock instance() {
			return InstanceHolder.INSTANCE;
		}

		public static long now() {
			return instance().currentTimeMillis();
		}

		public static String nowDate() {
			return new Timestamp(instance().currentTimeMillis()).toString();
		}

		private void scheduleClockUpdating() {
			ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
				Thread thread = new Thread(runnable, "System Clock");
				thread.setDaemon(true);
				return thread;
			});
			scheduler.scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);
		}

		private long currentTimeMillis() {
			return now.get();
		}

		private static class InstanceHolder {
			public static final SystemClock INSTANCE = new SystemClock(1);
		}
	}
}
