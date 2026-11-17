import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

const BACKEND = 'http://127.0.0.1:8080'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Open http://127.0.0.1:5173 (not localhost): AuthReflector redirects back to this exact address
    host: '127.0.0.1',
    port: 5173,
    strictPort: true,
    // Same origin for the browser: the backend's session cookie (BFF) works without CORS
    // changeOrigin: false keeps the browser's Host (127.0.0.1:5173); otherwise Spring would build the
    // sign-in redirect URL with the backend's own address and AuthReflector would refuse it
    proxy: Object.fromEntries(
      ['/api', '/auth', '/oauth2', '/login/oauth2'].map((path) => [path, { target: BACKEND, changeOrigin: false }]),
    ),
  },
})
