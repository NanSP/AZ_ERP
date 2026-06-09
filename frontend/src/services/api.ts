import axios from "axios";

const baseUrl =
  import.meta.env.VITE_API_BASE_URL ||
  import.meta.env.VITE_API_URL ||
  "http://localhost:8080/api";

let authToken: string | null = null;

export function setAuthToken(token: string | null) {
  authToken = token;
}

export const api = axios.create({
  baseURL: baseUrl,
  headers: {
    "Content-Type": "application/json",
  },
  // Crucial para enviar cookies HttpOnly e receber Set-Cookie
  withCredentials: true,
});

// Se um token em memória for fornecido, adicionamos o header Authorization.
api.interceptors.request.use((config) => {
  if (authToken && config.headers) {
    config.headers.Authorization = `Bearer ${authToken}`;
  }
  return config;
});

export default api;
