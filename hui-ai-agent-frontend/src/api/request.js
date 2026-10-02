import axios from 'axios'

/**
 * axios 实例：用于普通（非流式）请求
 * baseURL 默认 /api，开发环境由 Vite 代理转发到 http://localhost:8123
 */
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 60000
})

export default request
