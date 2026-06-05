import type { ReactNode } from "react";
import verticalLogo from "../assets/verticalLogo.png";
import "../pages/Login/login.css";

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
