import { InboxOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Select, Upload, message } from 'antd';
import type { UploadProps } from 'antd';
import { useReviewModule } from '@/pages/review/useReviewModule';

const uploadProps: UploadProps = {
  name: 'file',
  multiple: true,
  beforeUpload: () => false,
};

export default function UploadReviewPage() {
  const module = useReviewModule();

  return (
    <section className="review-page">
      <div className="page-toolbar">
        <div>
          <h1>文档审查</h1>
          <p>{module.title}任务创建与文件上传。</p>
        </div>
      </div>
      <Card className="review-form-card">
        <Form
          layout="vertical"
          onFinish={() => message.info('React 版本已保留任务创建入口，提交接口可按当前后端任务协议继续补齐。')}
        >
          <Form.Item name="taskName" label="任务名称" rules={[{ required: true, message: '请输入任务名称' }]}>
            <Input placeholder="请输入任务名称" />
          </Form.Item>
          <Form.Item name="reviewType" label="审查类型">
            <Select
              placeholder="请选择审查类型"
              options={[
                { label: '标准审查', value: 'standard' },
                { label: '快速审查', value: 'quick' },
              ]}
            />
          </Form.Item>
          <Form.Item name="files" label="审查文件" rules={[{ required: true, message: '请选择审查文件' }]}>
            <Upload.Dragger {...uploadProps}>
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击或拖拽文件到此区域上传</p>
              <p className="ant-upload-hint">支持一次上传多个审查文件。</p>
            </Upload.Dragger>
          </Form.Item>
          <Button type="primary" htmlType="submit">
            提交审查
          </Button>
        </Form>
      </Card>
    </section>
  );
}
