import fallbackIcon from '@/assets/images/bglr.png';
import circuitHover from '@/assets/images/circuit-hover.png';
import codeHover from '@/assets/images/code-review-hover.png';
import elgHover from '@/assets/images/elg-hover.png';
import elg from '@/assets/images/elg.png';
import erds from '@/assets/images/erds.png';
import pcb from '@/assets/images/pcb.png';
import pcba from '@/assets/images/pcba.png';
import dsr from '@/assets/images/dsr.png';
import crr from '@/assets/images/crr.png';
import asc from '@/assets/images/asc.png';
import pdkb from '@/assets/images/pdkb.png';
import cn from '@/assets/images/cn.png';
import icn from '@/assets/images/icn.png';
import type { ApplicationItem } from '@/types';

const localIconMap: Record<string, string> = {
  circuitHover,
  codeHover,
  elgHover,
  elg,
  erds,
  pcb,
  pcba,
  dsr,
  crr,
  asc,
  pdkb,
  cn,
  icn,
};

interface AppIconProps {
  app: ApplicationItem;
}

export function resolveIcon(app: ApplicationItem) {
  const icon = app.icon || '';
  if (!icon) {
    return fallbackIcon;
  }
  if (localIconMap[icon]) {
    return localIconMap[icon];
  }
  if (icon.endsWith('.png') || icon.includes('-')) {
    return `/portal/common/v1/storage/download/${icon}?fileId=${icon}`;
  }
  return fallbackIcon;
}

export default function AppIcon({ app }: AppIconProps) {
  return <img src={resolveIcon(app)} alt={app.name} />;
}
