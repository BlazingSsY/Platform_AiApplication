import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { resolve } from 'node:path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': resolve(__dirname, './src')
    }
  },
  base: './',
  server: {
    host: '0.0.0.0',
    port: 3200,
    open: false,
    proxy: {
      '/portal': 'http://localhost:18080',
      '/circuitreview': 'http://localhost:18080',
      '/sourcecodereview': 'http://localhost:18080',
      '/logicreview': 'http://localhost:18080',
      '/sso': 'http://localhost:18080',
      '/ai-document-review': 'http://localhost:18080'
    }
  },
  build: {
    sourcemap: false,
    chunkSizeWarningLimit: 1200
  }
});
