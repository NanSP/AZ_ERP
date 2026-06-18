import { Suspense, lazy } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./auth/AuthProvider";
import ProtectedRoute from "./auth/ProtectedRoute";
import PasswordChangeGuard from "./auth/PasswordChangeGuard";
import SiteLayout from "./layouts/SiteLayout";
import AppShell from "./layouts/AppShell/AppShell";

const EntryPage = lazy(() => import("./pages/EntryPage/EntryPage"));
const MasterLoginPage = lazy(() => import("./pages/Login/MasterLoginPage"));
const TenantLoginPage = lazy(() => import("./pages/Login/TenantLoginPage"));
const ChangePasswordPage = lazy(() => import("./pages/Login/ChangePasswordPage"));
const DashboardPage = lazy(() => import("./pages/Dashboard/DashboardPage"));
const ModuleWorkspacePage = lazy(
  () => import("./pages/Module/ModuleWorkspacePage"),
);
const PlatformWorkspacePage = lazy(
  () => import("./pages/Platform/PlatformWorkspacePage"),
);

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Suspense fallback={null}>
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
                      path="platform/:entity"
                      element={<PlatformWorkspacePage />}
                    />
                    <Route
                      path="module/:schema/:entity"
                      element={<ModuleWorkspacePage />}
                    />
                  </Route>
                </Route>
              </Route>
            </Route>
          </Routes>
        </Suspense>
      </BrowserRouter>
    </AuthProvider>
  );
}
