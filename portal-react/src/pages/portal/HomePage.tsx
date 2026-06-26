import { useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import AppIcon from '@/components/AppIcon';
import { useAppStore } from '@/store/app';
import { openApplication } from '@/utils/openApplication';
import type { ApplicationItem } from '@/types';
import designTitle from '@/assets/images/sjyf.png';
import productionTitle from '@/assets/images/sczc.png';
import managementTitle from '@/assets/images/yygl.png';
import computingTitle from '@/assets/images/slzyV2.png';

const sections = [
  { key: '设计研发', title: designTitle, side: 'left' },
  { key: '生产制造', title: productionTitle, side: 'left' },
  { key: '运营管理', title: managementTitle, side: 'right' },
  { key: '算力资源', title: computingTitle, side: 'right' },
  { key: '办公管理', label: '办公管理', side: 'right' },
];

function FeatureSection({
  section,
  items,
  onOpen,
}: {
  section: (typeof sections)[number];
  items: ApplicationItem[];
  onOpen: (item: ApplicationItem) => void;
}) {
  if (!items.length) {
    return null;
  }

  return (
    <section className={`feature-section ${section.side === 'right' ? 'right-align' : ''}`}>
      {section.title ? <img className="feature-title-image" src={section.title} alt={section.key} /> : <h2>{section.label}</h2>}
      <div className="feature-card">
        {items.map((item) => (
          <button
            key={`${item.id || item.name}-${item.url}`}
            type="button"
            className={`feature-card-item ${Number(item.status) !== 1 ? 'disabled' : ''}`}
            onClick={() => onOpen(item)}
          >
            <span className="feature-icon-wrap">
              <AppIcon app={item} />
            </span>
            <span className="feature-name">{item.name}</span>
          </button>
        ))}
      </div>
    </section>
  );
}

export default function HomePage() {
  const navigate = useNavigate();
  const applications = useAppStore((state) => state.applications);
  const renderedSections = useMemo(
    () =>
      sections.map((section) => ({
        ...section,
        items: applications[section.key] || [],
      })),
    [applications],
  );

  const handleOpen = (item: ApplicationItem) => {
    openApplication(item, navigate);
  };

  return (
    <div className="portal-home">
      <div className="feature-cards-container">
        <div className="feature-column left-column">
          {renderedSections
            .filter((section) => section.side === 'left')
            .map((section) => (
              <FeatureSection key={section.key} section={section} items={section.items} onOpen={handleOpen} />
            ))}
        </div>
        <div className="feature-column right-column">
          {renderedSections
            .filter((section) => section.side === 'right')
            .map((section) => (
              <FeatureSection key={section.key} section={section} items={section.items} onOpen={handleOpen} />
            ))}
        </div>
      </div>
    </div>
  );
}
