import { useCallback, useEffect, useState } from 'react';
import { Button, List, message } from 'antd';
import { DownloadOutlined } from '@ant-design/icons';
import { extractRows } from '@/api/http';
import { fetchTools } from '@/api/review';
import { useReviewModule } from '@/pages/review/useReviewModule';

export default function ToolDownloadPage() {
  const module = useReviewModule();
  const [loading, setLoading] = useState(false);
  const [rows, setRows] = useState<Record<string, any>[]>([]);

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const result = await fetchTools(module, { pageNo: 1, pageNum: 1, pageSize: 50 });
      setRows(extractRows(result).rows);
    } catch {
      message.error('工具列表加载失败');
    } finally {
      setLoading(false);
    }
  }, [module]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  return (
    <section className="review-page">
      <div className="page-toolbar">
        <div>
          <h1>工具下载</h1>
          <p>{module.title}客户端工具、模板和说明文件。</p>
        </div>
        <Button onClick={loadData}>刷新</Button>
      </div>
      <List
        loading={loading}
        bordered
        dataSource={rows}
        locale={{ emptyText: '暂无工具文件' }}
        renderItem={(item) => (
          <List.Item
            actions={[
              <Button key="download" icon={<DownloadOutlined />} type="link">
                下载
              </Button>,
            ]}
          >
            <List.Item.Meta
              title={item.name || item.fileName || item.title || '未命名工具'}
              description={item.remark || item.description || item.createTime || '-'}
            />
          </List.Item>
        )}
      />
    </section>
  );
}
