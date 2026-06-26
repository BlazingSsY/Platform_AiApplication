import { create } from 'zustand';
import type { ApplicationGroups } from '@/types';

interface AppState {
  applications: ApplicationGroups;
  setApplications: (applications: ApplicationGroups) => void;
}

export const useAppStore = create<AppState>((set) => ({
  applications: {},
  setApplications: (applications) => set({ applications }),
}));
