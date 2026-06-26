import { useEffect, useMemo, useState } from 'react';
import { Col, Row, Statistic, message } from 'antd';
import ReactECharts from 'echarts-for-react';
import { fetchReviewHome } from '@/api/review';
import { useReviewModule } from '@/pages/review/useReviewModule';

export default function StatisticsPage() {
  const module = useReviewModule();
  const [data, setData] = useState<Record<string, any>>({});

  useEffect(() => {
    fetchReviewHome(module)
      .then((result) => {
        if (result.succ !== false) {
          setData(result.content || {});
        }
      })
      .catch(() => message.error('统计数据加载失败'));
  }, [module]);

  const metrics = useMemo(() => {
    const values = Object.entries(data).filter(([, value]) => typeof value === 'number');
    return values.length ? values : [['审查总数', 0], ['通过数', 0], ['问题数', 0], ['规则数', 0]];
  }, [data]);

  return (
    <section className="review-page">
      <div className="page-toolbar">
        <div>
          <h1>统计分析</h1>
          <p>{module.title}运行指标统计。</p>
        </div>
      </div>
      <Row gutter={16} className="review-stat-row">
        {metrics.slice(0, 4).map(([key, value]) => (
          <Col span={6} key={key}>
            <div className="review-stat-block">
              <Statistic title={key} value={Number(value) || 0} />
            </div>
          </Col>
        ))}
      </Row>
      <div className="review-panel">
        <ReactECharts
          style={{ height: 360 }}
          option={{
            tooltip: {},
            legend: { bottom: 0 },
            series: [
              {
                type: 'pie',
                radius: ['45%', '70%'],
                data: metrics.map(([name, value]) => ({ name, value })),
              },
            ],
          }}
        />
      </div>
    </section>
  );
}
