import { useEffect, useMemo, useState } from "react";
import {
  listResource,
  createResource,
  updateResource,
  deleteResource,
} from "../../services/resourceService";
import "./module-crud.css";

export type GenericItem = { id?: number; [key: string]: unknown };

type ModuleCrudProps = {
  schema: string;
  entity: string;
  label: string;
};

export default function ModuleCrud({ schema, entity, label }: ModuleCrudProps) {
  const [items, setItems] = useState<GenericItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedItem, setSelectedItem] = useState<GenericItem | null>(null);
  const [payload, setPayload] = useState("{}");

  const endpoint = `${schema}/${entity}`;

  const columns = useMemo(() => {
    const keys = new Set<string>();
    items.forEach((item) => {
      Object.keys(item).forEach((key) => keys.add(key));
    });
    return Array.from(keys);
  }, [items]);

  async function loadData() {
    setLoading(true);
    setError(null);

    try {
      const response = await listResource(schema, entity);
      setItems(Array.isArray(response.data) ? response.data : []);
    } catch {
      setError(`Nao foi possivel carregar ${label}.`);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadData();
  }, [schema, entity]);

  async function handleCreate() {
    let body: GenericItem;

    try {
      body = JSON.parse(payload) as GenericItem;
    } catch {
      setError("JSON invalido para criacao.");
      return;
    }

    setLoading(true);

    try {
      await createResource(schema, entity, body);
      setPayload("{}");
      await loadData();
    } catch {
      setError(`Erro ao criar ${label}.`);
    } finally {
      setLoading(false);
    }
  }

  async function handleUpdate() {
    if (!selectedItem?.id) {
      setError("Selecione um registro com id para atualizar.");
      return;
    }

    let body: GenericItem;

    try {
      body = JSON.parse(payload) as GenericItem;
    } catch {
      setError("JSON invalido para atualizacao.");
      return;
    }

    setLoading(true);

    try {
      await updateResource(schema, entity, selectedItem.id, body);
      setSelectedItem(null);
      setPayload("{}");
      await loadData();
    } catch {
      setError(`Erro ao atualizar ${label}.`);
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(id?: number) {
    if (id == null) {
      setError("ID invalido para exclusao.");
      return;
    }

    setLoading(true);

    try {
      await deleteResource(schema, entity, id);
      await loadData();
    } catch {
      setError(`Erro ao excluir ${label}.`);
    } finally {
      setLoading(false);
    }
  }

  function selectItem(item: GenericItem) {
    setSelectedItem(item);
    setPayload(JSON.stringify(item, null, 2));
  }

  return (
    <div className="module-crud">
      <header className="module-crud__header">
        <div>
          <span className="module-crud__eyebrow">Recurso</span>
          <h3 className="module-crud__title">{label}</h3>
          <p className="module-crud__subtitle">
            Endpoint ativo: <code>{endpoint}</code>
          </p>
        </div>

        <div className="module-crud__actions">
          <button
            type="button"
            className="module-crud__button module-crud__button--ghost"
            onClick={loadData}
          >
            Recarregar
          </button>
          <button
            type="button"
            className="module-crud__button"
            onClick={handleCreate}
          >
            Criar
          </button>
          <button
            type="button"
            className="module-crud__button module-crud__button--secondary"
            onClick={handleUpdate}
            disabled={!selectedItem}
          >
            Atualizar
          </button>
        </div>
      </header>

      {error ? (
        <div className="module-crud__alert module-crud__alert--error">
          {error}
        </div>
      ) : null}
      {loading ? (
        <div className="module-crud__alert">Carregando dados...</div>
      ) : null}

      <div className="module-crud__layout">
        <section className="module-crud__table-panel">
          <div className="module-crud__panel-head">
            <h4 className="module-crud__panel-title">Registros</h4>
            <span className="module-crud__panel-meta">
              {items.length} itens
            </span>
          </div>

          <div className="module-crud__table-wrap">
            <table className="module-crud__table">
              <thead>
                <tr>
                  {columns.map((column) => (
                    <th key={column}>{column}</th>
                  ))}
                  <th>Ações</th>
                </tr>
              </thead>

              <tbody>
                {items.length > 0 ? (
                  items.map((item) => (
                    <tr key={item.id ?? JSON.stringify(item)}>
                      {columns.map((column) => (
                        <td key={column}>{String(item[column] ?? "")}</td>
                      ))}
                      <td className="module-crud__row-actions">
                        <button
                          type="button"
                          className="module-crud__mini-button"
                          onClick={() => selectItem(item)}
                        >
                          Editar
                        </button>
                        {item.id != null ? (
                          <button
                            type="button"
                            className="module-crud__mini-button module-crud__mini-button--danger"
                            onClick={() => handleDelete(item.id)}
                          >
                            Excluir
                          </button>
                        ) : null}
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td
                      className="module-crud__empty"
                      colSpan={Math.max(columns.length + 1, 2)}
                    >
                      Nenhum registro encontrado para este recurso.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </section>

        <aside className="module-crud__editor-panel">
          <div className="module-crud__panel-head">
            <h4 className="module-crud__panel-title">Editor JSON</h4>
            <span className="module-crud__panel-meta">
              {selectedItem ? "Modo edicao" : "Novo registro"}
            </span>
          </div>

          <label className="module-crud__label" htmlFor="module-crud-payload">
            Payload
          </label>
          <textarea
            id="module-crud-payload"
            className="module-crud__textarea"
            value={payload}
            onChange={(event) => setPayload(event.target.value)}
            placeholder='{"nome":"exemplo","ativo":true}'
          />

          <p className="module-crud__hint">
            Para criar, informe um JSON valido. Para editar, selecione uma linha
            da tabela.
          </p>
        </aside>
      </div>
    </div>
  );
}
