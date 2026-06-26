import { useOutletContext } from 'react-router-dom';
import type { ReviewModuleConfig } from '@/types';

export function useReviewModule() {
  return useOutletContext<ReviewModuleConfig>();
}
