import axios from 'axios'

// Same origin (Vite proxy in development): the session cookie is sent automatically
const axiosClient = axios.create()

// The backend needs a CSRF token on POST/PUT/PATCH/DELETE (cookie sessions); fetched once, then reused
let csrf
axiosClient.interceptors.request.use(async (config) => {
  if (config.method && !['get', 'head', 'options'].includes(config.method.toLowerCase())) {
    csrf ??= (await axios.get('/auth/csrf')).data
    config.headers[csrf.headerName] = csrf.token
  }
  return config
})

export default axiosClient
