import { Outlet } from "react-router-dom";
import Sidebar from "../../components/Sidebar/Sidebar";
import Topbar from "../../components/Topbar/Topbar";
import "./app-shell.css";

export default function AppShell() {
  return (
    <div className="app-shell">
      <aside className="app-shell__sidebar">
        <Sidebar />
      </aside>

      <div className="app-shell__main">
        <header className="app-shell__topbar">
          <Topbar />
        </header>

        <main className="app-shell__content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
