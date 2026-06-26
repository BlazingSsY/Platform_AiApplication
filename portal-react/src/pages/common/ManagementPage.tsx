import { useCallback, useEffect, useMemo, useState } from 'react';
import { Button, Input, Space, Table, Tag, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { extractRows } from '@/api/http';
import {
  fetchApplicationPages,
  fetchDepartments,
  fetchInformation,
  fetchLogs,
  fetchMenus,
  fetchRoles,
  fetchUsers,
} from '@/api/portal';

const pageConfig: Record<
  string,
  {
    title: string;
    description: string;
    fetcher: (params: Record<string, any>) => Promise<any>;
  }
> = {
  userList: { title: '用户管理', description: '维护平台用户、账号状态和组织关系。', fetcher: fetchUsers },
  depList: { title: '组织管理', description: '维护部门树、组织节点和人员归属。', fetcher: fetchDepartments },
  applicationManage: { title: '应用管理', description: '维护门户首页卡片、跳转地址和启停状态。', fetcher: fetchApplicationPages },
  informationManage: { title: '资讯管理', description: '维护平台资讯与应用说明。', fetcher: fetchInformation },
  applicationList: { title: '资讯列表', description: '查看已发布的资讯内容。', fetcher: fetchInformation },
  menu: { title: '菜单管理', description: '维护系统权限菜单。', fetcher: fetchMenus },
  role: { title: '角色管理', description: '维护角色和权限配置。', fetcher: fetchRoles },
  log: { title: '日志管理', description: '查看系统操作日志。', fetcher: fetchLogs },
};

function normalizeRecord(record: Record<string, any>, index: number) {
  return {
    key: record.id || record.userID || record.code || index,
    ...record,
  };
}

export default function ManagementPage({ type }: { type: string }) {
  const config = pageConfig[type] || {
    title: '系统页面',
    description: '该页面已在 React 版本中保留入口。',
    fetcher: fetchApplicationPages,
  };
  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState<Record<string, any>[]>([]);
  const [total, setTotal] = useState(0);
  const [keyword, setKeyword] = useState('');
  const [page, setPage] = useState(1);

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const result = await config.fetcher({ pageNo: page, pageNum: page, pageSize: 10, keyword });
      const data = extractRows(result);
      setRows(data.rows.map(normalizeRecord));
      setTotal(data.total);
    } catch {
      message.error(`${config.title}数据加载失败`);
    } finally {
      setLoading(false);
    }
  }, [config, keyword, page]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const columns: ColumnsType<Record<string, any>> = useMemo(
    () => [
      {
        title: '名称',
        dataIndex: 'name',
        ellipsis: true,
        render: (_, record) => record.name || record.loginName || record.title || record.module || record.id || '-',
      },
      {
        title: '编码/账号',
        dataIndex: 'code',
        ellipsis: true,
        render: (_, record) => record.code || record.loginName || record.username || record.url || '-',
      },
      {
        title: '状态',
        dataIndex: 'status',
        width: 110,
        render: (value) =>
          value === undefined || value === null ? (
            '-'
          ) : (
            <Tag color={Number(value) === 1 || value === true ? 'green' : 'default'}>
              {Number(value) === 1 || value === true ? '启用' : '停用'}
            </Tag>
          ),
      },
      {
        title: '更新时间',
        dataIndex: 'updateTime',
        width: 190,
        render: (_, record) => record.updateTime || record.createTime || '-',
      },
    ],
    [],
  );

  return (
    <section className="management-page">
      <div className="page-toolbar">
        <div>
          <h1>{config.title}</h1>
          <p>{config.description}</p>
        </div>
        <Space>
          <Input.Search
            placeholder="请输入关键字"
            allowClear
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
        pagination={{
          current: page,
          total,
          pageSize: 10,
          showSizeChanger: false,
          onChange: setPage,
        }}
      />
    </section>
  );
}
