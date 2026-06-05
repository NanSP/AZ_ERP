import { BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./auth/AuthContext";
import ProtectedRoute from "./auth/ProtectedRoute";
import PasswordChangeGuard from "./auth/PasswordChangeGuard";
import SiteLayout from "./layouts/SiteLayout";
import AppShell from "./layouts/AppShell/AppShell";
import EntryPage from "./pages/EntryPage/EntryPage";
import MasterLoginPage from "./pages/Login/MasterLoginPage";
import TenantLoginPage from "./pages/Login/TenantLoginPage";
import ChangePasswordPage from "./pages/Login/ChangePasswordPage";
import DashboardPage from "./pages/Dashboard/DashboardPage";
import ModuleWorkspacePage from "./pages/Module/ModuleWorkspacePage";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route element={<SiteLayout />}>
            <Route path="/" element={<EntryPage />} />
            <Route path="/login" element={<MasterLoginPage />} />
            <Route path="/tenant-login" element={<TenantLoginPage />} />
            <Route path="/change-password" element={<ChangePasswordPage />} />

            <Route element={<ProtectedRoute />}>
              <Route element={<PasswordChangeGuard />}>
                <Route path="/app" element={<AppShell />}>
                  <Route index element={<DashboardPage />} />
                  <Route
                    path="module/:schema/:entity"
                    element={<ModuleWorkspacePage />}
                  />
                </Route>
              </Route>
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
