import { useMemo } from "react";
import { Link, useParams } from "react-router-dom";
import ModuleCrud from "../components/ModuleCrud/ModuleCrud";
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
        <h2>Modulo nao encontrado</h2>
        <p>Verifique a URL e selecione algo valido.</p>
        <Link to="/app">Voltar</Link>
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
