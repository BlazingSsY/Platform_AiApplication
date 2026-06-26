import { useEffect, useMemo, useState } from 'react';
import { Button, Col, Row, Statistic, message } from 'antd';
import { ArrowRightOutlined } from '@ant-design/icons';
import ReactECharts from 'echarts-for-react';
import { useNavigate } from 'react-router-dom';
import { fetchReviewHome } from '@/api/review';
import { useReviewModule } from '@/pages/review/useReviewModule';

export default function ReviewHome() {
  const module = useReviewModule();
  const navigate = useNavigate();
  const [data, setData] = useState<Record<string, any>>({});

  useEffect(() => {
    fetchReviewHome(module)
      .then((result) => {
        if (result.succ !== false) {
          setData(result.content || {});
        }
      })
      .catch(() => message.error('首页统计加载失败'));
  }, [module]);

  const metrics = useMemo(() => {
    const entries = Object.entries(data).filter(([, value]) => typeof value === 'number').slice(0, 4);
    if (entries.length) {
      return entries.map(([key, value]) => ({ key, label: key, value }));
    }
    return [
      { key: 'total', label: '审查总数', value: 0 },
      { key: 'passed', label: '通过数量', value: 0 },
      { key: 'failed', label: '问题数量', value: 0 },
      { key: 'rules', label: '规则数量', value: 0 },
    ];
  }, [data]);

  const chartOption = {
    tooltip: {},
    grid: { left: 32, right: 20, top: 30, bottom: 28 },
    xAxis: { type: 'category', data: metrics.map((item) => item.label) },
    yAxis: { type: 'value' },
    series: [
      {
        type: 'bar',
        data: metrics.map((item) => item.value),
        itemStyle: { color: '#1683d8' },
        barWidth: 28,
      },
    ],
  };

  return (
    <div className="review-home">
      <section className="review-banner" style={{ backgroundImage: `url(${module.banner})` }}>
        <div>
          <img src={module.logo} alt={module.title} />
          <h1>{module.title}</h1>
          <p>围绕设计文件、规则库、审查记录和问题复核提供统一工作台。</p>
          <Button type="primary" size="large" onClick={() => navigate(module.uploadPath)}>
            开始审查 <ArrowRightOutlined />
          </Button>
        </div>
      </section>
      <Row gutter={16} className="review-stat-row">
        {metrics.map((item) => (
          <Col span={6} key={item.key}>
            <div className="review-stat-block">
              <Statistic title={item.label} value={Number(item.value) || 0} />
            </div>
          </Col>
        ))}
      </Row>
      <section className="review-panel">
        <h2>审查趋势</h2>
        <ReactECharts option={chartOption} style={{ height: 280 }} />
      </section>
    </div>
  );
}
