import axios from "axios";

// Ajuste para a porta configurada no backend (application-dev.properties): 6000
// O frontend usará VITE_API_URL quando definido em .env, caso contrário usa padrão local.
const baseUrl = import.meta.env.VITE_API_URL || "http://localhost:6000/api";

export const api = axios.create({
  baseURL: baseUrl,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
