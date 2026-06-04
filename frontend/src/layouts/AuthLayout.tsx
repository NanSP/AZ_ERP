import type { ReactNode } from "react";

export default function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <main style={{ minHeight: "100vh", display: "grid", placeItems: "center" }}>
      <section style={{ width: "100%", maxWidth: 420, padding: 24 }}>
        {children}
      </section>
    </main>
  );
}
