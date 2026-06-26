import { useCallback, useEffect, useState } from 'react';
import { Button, Input, Space, Table, Tag, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { extractRows } from '@/api/http';
import { fetchRules } from '@/api/review';
import { useReviewModule } from '@/pages/review/useReviewModule';

export default function RuleListPage() {
  const module = useReviewModule();
  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState<Record<string, any>[]>([]);
  const [page, setPage] = useState(1);
  const [total, setTotal] = useState(0);

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const result = await fetchRules(module, { pageNo: page, pageNum: page, pageSize: 10 });
      const data = extractRows(result);
      setRows(data.rows.map((row, index) => ({ key: row.id || index, ...row })));
      setTotal(data.total);
    } catch {
      message.error('规则列表加载失败');
    } finally {
      setLoading(false);
    }
  }, [module, page]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const columns: ColumnsType<Record<string, any>> = [
    { title: '规则名称', dataIndex: 'name', render: (_, row) => row.name || row.ruleName || '-' },
    { title: '规则编号', dataIndex: 'code', render: (_, row) => row.code || row.ruleCode || '-' },
    { title: '分类', dataIndex: 'category', render: (_, row) => row.category || row.type || '-' },
    {
      title: '状态',
      dataIndex: 'status',
      width: 120,
      render: (value) => <Tag color={Number(value) === 1 ? 'green' : 'default'}>{Number(value) === 1 ? '启用' : value ?? '-'}</Tag>,
    },
  ];

  return (
    <section className="review-page">
      <div className="page-toolbar">
        <div>
          <h1>规则列表</h1>
          <p>{module.title}规则库查询。</p>
        </div>
        <Space>
          <Input.Search placeholder="规则名称/编号" enterButton="查询" allowClear />
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
