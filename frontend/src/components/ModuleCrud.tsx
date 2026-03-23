import { useEffect, useMemo, useState } from "react";
import {
  listResource,
  createResource,
  updateResource,
  deleteResource,
} from "../services/resourceService";

export type GenericItem = { id?: number; [key: string]: any };

interface ModuleCrudProps {
  schema: string;
  entity: string;
  label: string;
}

export default function ModuleCrud({ schema, entity, label }: ModuleCrudProps) {
  const [items, setItems] = useState<GenericItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedItem, setSelectedItem] = useState<GenericItem | null>(null);
  const [payload, setPayload] = useState("{}");

  const urlEntity = `${schema}/${entity}`;

  const columns = useMemo(() => {
    const keys = new Set<string>();
    items.forEach((item) => Object.keys(item).forEach((k) => keys.add(k)));
    return Array.from(keys);
  }, [items]);

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await listResource(schema, entity);
      setItems(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      setError(`Não foi possível carregar ${label}.`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [schema, entity]);

  const handleCreate = async () => {
    let body: any;
    try {
      body = JSON.parse(payload);
    } catch {
      setError("JSON inválido em novo item.");
      return;
    }
    setLoading(true);
    try {
      await createResource(schema, entity, body);
      setPayload("{}");
      await loadData();
    } catch {
      setError(`Erro na criação de ${label}.`);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async () => {
    if (!selectedItem?.id) {
      setError("Selecione um item com id para atualizar.");
      return;
    }
    let body: any;
    try {
      body = JSON.parse(payload);
    } catch {
      setError("JSON inválido na atualização do item.");
      return;
    }
    setLoading(true);
    try {
      await updateResource(schema, entity, selectedItem.id, body);
      setSelectedItem(null);
      setPayload("{}");
      await loadData();
    } catch {
      setError(`Erro na atualização de ${label}.`);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id?: number) => {
    if (id == null) {
      setError("ID inválido para exclusão.");
      return;
    }
    setLoading(true);
    try {
      await deleteResource(schema, entity, id);
      await loadData();
    } catch {
      setError(`Erro na exclusão de ${label}.`);
    } finally {
      setLoading(false);
    }
  };

  const selectItem = (item: GenericItem) => {
    setSelectedItem(item);
    setPayload(JSON.stringify(item, null, 2));
  };

  return (
    <div className="page fade-in">
      <h2>{label}</h2>
      <p>
        <strong>API:</strong>{" "}
        <code
          style={{
            background: "#f1f5f9",
            padding: "0.25rem 0.5rem",
            borderRadius: "4px",
          }}
        >
          {urlEntity}
        </code>
      </p>

      {error && <div className="error">{error}</div>}
      {loading && <div className="loading">Carregando... aguarde</div>}

      <div className="form-row" style={{ marginBottom: "1.5rem" }}>
        <div>
          <button onClick={loadData} style={{ background: "#10b981" }}>
            🔄 Recarregar
          </button>
          <button onClick={handleCreate} style={{ background: "#059669" }}>
            ➕ Criar
          </button>
          <button
            onClick={handleUpdate}
            disabled={!selectedItem}
            style={{ background: "#0ea5e9" }}
          >
            ✏️ Atualizar
          </button>
        </div>
      </div>

      <div className="form-row">
        <div style={{ flex: 3 }}>
          <h3>Dados ({items.length} registros)</h3>
          <div style={{ overflowX: "auto" }}>
            <table>
              <thead>
                <tr>
                  {columns.map((col) => (
                    <th key={col}>{col}</th>
                  ))}
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.id ?? JSON.stringify(item)}>
                    {columns.map((col) => (
                      <td
                        key={col}
                        style={{
                          maxWidth: "200px",
                          overflow: "hidden",
                          textOverflow: "ellipsis",
                        }}
                      >
                        {String(item[col] ?? "")}
                      </td>
                    ))}
                    <td>
                      <button
                        onClick={() => selectItem(item)}
                        style={{
                          background: "#f59e0b",
                          color: "white",
                          fontSize: "0.8rem",
                          padding: "0.25rem 0.5rem",
                        }}
                      >
                        ✏️ Editar
                      </button>
                      {item.id != null && (
                        <button
                          onClick={() => handleDelete(item.id)}
                          style={{
                            background: "#ef4444",
                            color: "white",
                            fontSize: "0.8rem",
                            padding: "0.25rem 0.5rem",
                          }}
                        >
                          🗑️ Excluir
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div style={{ flex: 2 }}>
          <h3>Editor JSON</h3>
          <label>Payload para criar/atualizar:</label>
          <textarea
            value={payload}
            onChange={(e) => setPayload(e.target.value)}
            placeholder='{"nome": "exemplo", "ativo": true}'
          />
          <small style={{ color: "#64748b" }}>
            Para criar: insira JSON válido
            <br />
            Para editar: selecione um item da tabela
          </small>
        </div>
      </div>
    </div>
  );
}
