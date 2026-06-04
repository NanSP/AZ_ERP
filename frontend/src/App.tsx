import { BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./auth/AuthContext";
import ProtectedRoute from "./auth/ProtectedRoute";
import PasswordChangeGuard from "./auth/PasswordChangeGuard";
import Home from "./pages/Home/Home";
import ModulePage from "./pages/ModulePage";
import EntryPage from "./pages/EntryPage/EntryPage";
import MasterLoginPage from "./pages/Login/MasterLoginPage";
import TenantLoginPage from "./pages/Login/TenantLoginPage";
import ChangePasswordPage from "./pages/Login/ChangePasswordPage";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<EntryPage />} />
          <Route path="/login" element={<MasterLoginPage />} />
          <Route path="/tenant-login" element={<TenantLoginPage />} />
          <Route path="/change-password" element={<ChangePasswordPage />} />

          <Route element={<ProtectedRoute />}>
            <Route element={<PasswordChangeGuard />}>
              <Route path="/app" element={<Home />} />
              <Route
                path="/app/module/:schema/:entity"
                element={<ModulePage />}
              />
            </Route>
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
