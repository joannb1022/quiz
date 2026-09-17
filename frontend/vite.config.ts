import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// proxy '/api' -> http://localhost:8080
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      }
    }
  },
  build: {
    outDir: 'build' // ustawiamy 'build' żeby pasowało do przykładów Gradle
  }
})