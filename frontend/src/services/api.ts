import axios from "axios";

const baseUrl =
  import.meta.env.VITE_API_BASE_URL ||
  import.meta.env.VITE_API_URL ||
  "http://localhost:8080/api";

export const api = axios.create({
  baseURL: baseUrl,
  headers: {
    "Content-Type": "application/json",
  },
  // Crucial para enviar cookies HttpOnly e receber Set-Cookie
  withCredentials: true,
});

export default api;
