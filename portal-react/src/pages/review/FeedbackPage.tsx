import { Button, Form, Input, Select, message } from 'antd';
import { submitFeedback } from '@/api/review';
import { useReviewModule } from '@/pages/review/useReviewModule';

export default function FeedbackPage() {
  const module = useReviewModule();
  const [form] = Form.useForm();

  const handleSubmit = async (values: Record<string, any>) => {
    const result = await submitFeedback(module, values);
    if (result.succ !== false) {
      message.success('反馈已提交');
      form.resetFields();
    }
  };

  return (
    <section className="review-page">
      <div className="page-toolbar">
        <div>
          <h1>意见反馈</h1>
          <p>{module.title}问题反馈与改进建议。</p>
        </div>
      </div>
      <div className="review-form-card">
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item name="type" label="反馈类型" rules={[{ required: true, message: '请选择反馈类型' }]}>
            <Select
              options={[
                { label: '功能问题', value: '功能问题' },
                { label: '审查规则建议', value: '审查规则建议' },
                { label: '使用体验', value: '使用体验' },
              ]}
            />
          </Form.Item>
          <Form.Item name="title" label="标题" rules={[{ required: true, message: '请输入标题' }]}>
            <Input placeholder="请输入标题" />
          </Form.Item>
          <Form.Item name="content" label="反馈内容" rules={[{ required: true, message: '请输入反馈内容' }]}>
            <Input.TextArea rows={6} placeholder="请描述具体问题或建议" />
          </Form.Item>
          <Button type="primary" htmlType="submit">
            提交反馈
          </Button>
        </Form>
      </div>
    </section>
  );
}
