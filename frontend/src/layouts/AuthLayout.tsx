import type { ReactNode } from "react";
import "../pages/Login/login.css";

const verticalLogo = "/branding/verticalLogo.webp";

export default function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <main className="auth-page">
      <section className="auth-shell">
        <img src={verticalLogo} alt="AZ ERP" className="auth-brand" />
        <section className="auth-card">{children}</section>
      </section>
    </main>
  );
}
