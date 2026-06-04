import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "./useAuth";

export default function PasswordChangeGuard() {
  const { session } = useAuth();

  if (session?.passwordChangeRequired) {
    return <Navigate to="/change-password" replace />;
  }
  return <Outlet />;
}
