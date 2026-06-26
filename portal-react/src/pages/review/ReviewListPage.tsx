import { useCallback, useEffect, useState } from 'react';
import { Button, Input, Space, Table, Tag, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { fetchReviewPage } from '@/api/review';
import { extractRows } from '@/api/http';
import { useReviewModule } from '@/pages/review/useReviewModule';

export default function ReviewListPage() {
  const module = useReviewModule();
  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState<Record<string, any>[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [keyword, setKeyword] = useState('');

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const result = await fetchReviewPage(module, { pageNo: page, pageNum: page, pageSize: 10, keyword });
      const data = extractRows(result);
      setRows(data.rows.map((row, index) => ({ key: row.id || index, ...row })));
      setTotal(data.total);
    } catch {
      message.error('复核列表加载失败');
    } finally {
      setLoading(false);
    }
  }, [keyword, module, page]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const columns: ColumnsType<Record<string, any>> = [
    { title: '任务名称', dataIndex: 'name', render: (_, row) => row.name || row.taskName || row.fileName || '-' },
    { title: '文件名称', dataIndex: 'fileName', render: (_, row) => row.fileName || row.documentName || '-' },
    {
      title: '状态',
      dataIndex: 'status',
      width: 130,
      render: (value) => <Tag color={Number(value) === 1 ? 'blue' : 'default'}>{value ?? '-'}</Tag>,
    },
    { title: '创建时间', dataIndex: 'createTime', width: 190, render: (value) => value || '-' },
  ];

  return (
    <section className="review-page">
      <div className="page-toolbar">
        <div>
          <h1>问题复核</h1>
          <p>{module.title}审查任务与问题复核记录。</p>
        </div>
        <Space>
          <Input.Search
            allowClear
            placeholder="任务名称/文件名称"
            enterButton="查询"
            onSearch={(value) => {
              setKeyword(value);
              setPage(1);
            }}
          />
          <Button onClick={loadData}>刷新</Button>
        </Space>
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
