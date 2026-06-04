import axios from "axios";
import { loadSession } from "../auth/authStorage";

const baseUrl =
  import.meta.env.VITE_API_BASE_URL ||
  import.meta.env.VITE_API_URL ||
  "http://localhost:8080/api";

export const api = axios.create({
  baseURL: baseUrl,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const session = loadSession();

  if (session?.token && config.headers) {
    config.headers.Authorization = `Bearer ${session.token}`;
  }
  return config;
});

export default api;
