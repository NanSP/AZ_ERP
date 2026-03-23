import { BrowserRouter, Routes, Route, NavLink } from "react-router-dom";
import "./App.css";
import Home from "./pages/Home";
import ModulePage from "./pages/ModulePage";

function App() {
  return (
    <BrowserRouter basename="/">
      <div>
        <header
          className="app-header"
          style={{ padding: "16px", background: "#f0f2f5" }}
        >
          <h1>AZ_ERP Frontend</h1>
          <nav>
            <NavLink to="/" end style={{ marginRight: 12 }}>
              Início
            </NavLink>
            <NavLink to="/module/ps/projetos" style={{ marginRight: 12 }}>
              Projetos
            </NavLink>
            <NavLink to="/module/ps/tarefas" style={{ marginRight: 12 }}>
              Tarefas
            </NavLink>
            <NavLink to="/module/mm/compras" style={{ marginRight: 12 }}>
              Compras
            </NavLink>
            <NavLink to="/module/fi/contasPagar" style={{ marginRight: 12 }}>
              Contas a Pagar
            </NavLink>
          </nav>
        </header>

        <main>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/module/:schema/:entity" element={<ModulePage />} />
            <Route
              path="*"
              element={
                <div style={{ padding: 24 }}>
                  <h2>Página não encontrada</h2>
                  <p>Use o menu para selecionar um módulo.</p>
                </div>
              }
            />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
