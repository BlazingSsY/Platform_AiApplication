import { useCallback, useEffect, useState } from 'react';
import { Button, Table, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { extractRows } from '@/api/http';
import { fetchLogs } from '@/api/review';
import { useReviewModule } from '@/pages/review/useReviewModule';

export default function LogPage() {
  const module = useReviewModule();
  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState<Record<string, any>[]>([]);
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const result = await fetchLogs(module, { pageNo: page, pageNum: page, pageSize: 10 });
      const data = extractRows(result);
      setRows(data.rows.map((row, index) => ({ key: row.id || index, ...row })));
      setTotal(data.total);
    } catch {
      message.error('日志加载失败');
    } finally {
      setLoading(false);
    }
  }, [module, page]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const columns: ColumnsType<Record<string, any>> = [
    { title: '操作人', dataIndex: 'operator', render: (_, row) => row.operator || row.createBy || row.userName || '-' },
    { title: '操作内容', dataIndex: 'content', render: (_, row) => row.content || row.operation || row.description || '-' },
    { title: 'IP', dataIndex: 'ip', width: 150, render: (value) => value || '-' },
    { title: '时间', dataIndex: 'createTime', width: 190, render: (value) => value || '-' },
  ];

  return (
    <section className="review-page">
      <div className="page-toolbar">
        <div>
          <h1>日志查看</h1>
          <p>{module.title}操作日志。</p>
        </div>
        <Button onClick={loadData}>刷新</Button>
      </div>
      <Table
        loading={loading}
        columns={columns}
        dataSource={rows}
        pagination={{ current: page, total, pageSize: 10, showSizeChanger: false, onChange: setPage }}
      />
    </section>
  );
}
