import axios from 'axios';

const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api', timeout: 12000, headers: { 'Content-Type': 'application/json' } });
api.interceptors.request.use((config) => {
  const credentials = localStorage.getItem('monica.auth');
  if (credentials) config.headers.Authorization = `Basic ${credentials}`;
  return config;
});
api.interceptors.response.use((response) => response, (error) => {
  if (error.response?.status === 401 && !error.config?.url?.includes('/auth/me')) {
    window.dispatchEvent(new CustomEvent('monica:unauthorized'));
  }
  return Promise.reject(error);
});
export const errorMessage = (error) => error.response?.data?.mensagem || error.response?.data?.message || error.response?.data?.erro || error.message || 'Não foi possível concluir a operação.';
export default api;
