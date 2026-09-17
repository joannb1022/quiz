import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api',
  withCredentials: true,
})

apiClient.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      window.location.href = '/oauth2/authorization/google'
    }
    return Promise.reject(error)
  }
)

export default apiClient
