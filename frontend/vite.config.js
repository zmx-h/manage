import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

// Vite 配置：开发期通过 proxy 把 /api 转发到后端，避免跨域问题。
// 后端 context-path 为 /api（见 backend/src/main/resources/application.yml）。
export default defineConfig(async ({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  return {
    plugins: [vue(), ...(mode === 'mock' ? [(await import('./mock/plugin.js')).mockApi()] : [])],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      host: '0.0.0.0',
      port: 5173,
      proxy: mode === 'mock' ? {} : {
        '/api': {
          target: env.VITE_API_TARGET || 'http://localhost:8080',
          changeOrigin: true
        }
      }
    },
    build: {
      outDir: 'dist',
      sourcemap: false,
      rollupOptions: { output: { manualChunks: { framework: ["vue", "vue-router", "pinia"], ui: ["element-plus"], transport: ["axios"] } } }
    }
  }
})
