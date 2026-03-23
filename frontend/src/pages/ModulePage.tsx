import { useMemo } from "react";
import { useParams, Link } from "react-router-dom";
import ModuleCrud from "../components/ModuleCrud";
import { modules } from "../services/resourceService";

export default function ModulePage() {
  const params = useParams();
  const schema = params.schema || "";
  const entity = params.entity || "";

  const module = useMemo(
    () => modules.find((m) => m.schema === schema && m.entity === entity),
    [schema, entity],
  );

  if (!module) {
    return (
      <div style={{ padding: "24px" }}>
        <h2>Módulo não encontrado</h2>
        <p>Verifique a URL e selecione algo válido.</p>
        <Link to="/">Voltar</Link>
      </div>
    );
  }

  return (
    <ModuleCrud
      schema={module.schema}
      entity={module.entity}
      label={module.label}
    />
  );
}
