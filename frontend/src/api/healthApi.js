import axiosClient from './axiosClient'

export const getHealthStatus = () => {
  return axiosClient.get('/api/health')
}