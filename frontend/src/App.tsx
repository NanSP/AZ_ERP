import { BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./auth/AuthContext";
import ProtectedRoute from "./auth/ProtectedRoute";
import PasswordChangeGuard from "./auth/PasswordChangeGuard";
import Home from "./pages/Home";
import ModulePage from "./pages/ModulePage";
import MasterLoginPage from "./pages/MasterLoginPage";
import TenantLoginPage from "./pages/TenantLoginPage";
import ChangePasswordPage from "./pages/ChangePasswordPage";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<MasterLoginPage />} />
          <Route path="/tenant-login" element={<TenantLoginPage />} />
          <Route path="/change-password" element={<ChangePasswordPage />} />

          <Route element={<ProtectedRoute />}>
            <Route element={<PasswordChangeGuard />}>
              <Route path="/" element={<Home />} />
              <Route path="/module/:schema/:entity" element={<ModulePage />} />
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
