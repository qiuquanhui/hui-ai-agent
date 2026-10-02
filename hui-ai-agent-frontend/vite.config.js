import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      // 后端 context-path 为 /api，代理时保留前缀、不做 rewrite
      '/api': {
        target: 'http://localhost:8123',
        changeOrigin: true
      }
    }
  }
})
