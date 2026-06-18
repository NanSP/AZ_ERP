import { useCallback, useEffect, useMemo, useState, type ChangeEvent } from "react";
import {
  listResource,
  createResource,
  updateResource,
  deleteResource,
} from "../../services/resourceService";
import {
  buildPayloadFromForm,
  createEmptyFormValues,
  getCrudFormSchema,
  populateFormValues,
} from "./platformCrudSchemas";
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
  const formSchema = getCrudFormSchema(schema, entity);
  const [formValues, setFormValues] = useState<Record<string, string>>(
    formSchema ? createEmptyFormValues(formSchema) : {},
  );

  const endpoint = `${schema}/${entity}`;

  const columns = useMemo(() => {
    const keys = new Set<string>();
    items.forEach((item) => {
      Object.keys(item).forEach((key) => keys.add(key));
    });
    return Array.from(keys);
  }, [items]);

  const loadData = useCallback(async () => {
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
  }, [entity, label, schema]);

  useEffect(() => {
    void loadData();
  }, [loadData]);

  useEffect(() => {
    if (formSchema) {
      setFormValues(createEmptyFormValues(formSchema));
    }
  }, [formSchema]);

  async function handleCreate() {
    let body: GenericItem;

    try {
      body = formSchema
        ? (buildPayloadFromForm(formSchema, formValues) as GenericItem)
        : (JSON.parse(payload) as GenericItem);
    } catch {
      setError(
        formSchema
          ? "Formulario invalido para criacao."
          : "JSON invalido para criacao.",
      );
      return;
    }

    setLoading(true);

    try {
      await createResource(schema, entity, body);
      if (formSchema) {
        setFormValues(createEmptyFormValues(formSchema));
      } else {
        setPayload("{}");
      }
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
      body = formSchema
        ? (buildPayloadFromForm(formSchema, formValues) as GenericItem)
        : (JSON.parse(payload) as GenericItem);
    } catch {
      setError(
        formSchema
          ? "Formulario invalido para atualizacao."
          : "JSON invalido para atualizacao.",
      );
      return;
    }

    setLoading(true);

    try {
      await updateResource(schema, entity, selectedItem.id, body);
      setSelectedItem(null);
      if (formSchema) {
        setFormValues(createEmptyFormValues(formSchema));
      } else {
        setPayload("{}");
      }
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
    if (formSchema) {
      setFormValues(populateFormValues(formSchema, item));
      return;
    }

    setPayload(JSON.stringify(item, null, 2));
  }

  function resetEditor() {
    setSelectedItem(null);
    if (formSchema) {
      setFormValues(createEmptyFormValues(formSchema));
    } else {
      setPayload("{}");
    }
    setError(null);
  }

  function updateField(name: string, value: string) {
    setFormValues((current) => ({
      ...current,
      [name]: value,
    }));
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
            <div>
              <h4 className="module-crud__panel-title">Formulario tecnico</h4>
              <p className="module-crud__panel-description">
                Preencha o payload do recurso em um formato estruturado para
                criar ou atualizar registros da plataforma.
              </p>
            </div>

            <span className="module-crud__panel-meta">
              {selectedItem ? "Modo edicao" : "Novo registro"}
            </span>
          </div>

          <div className="module-crud__editor-shell">
            {formSchema ? (
              <div className="module-crud__form-grid">
                {formSchema.fields.map((field) => {
                  const fieldId = `module-crud-${field.name}`;
                  const value = formValues[field.name] ?? "";
                  const hasOptions = Array.isArray(field.options) && field.options.length > 0;
                  const commonProps = {
                    id: fieldId,
                    className:
                      field.type === "textarea" || field.type === "json"
                        ? "module-crud__textarea module-crud__textarea--compact"
                        : "module-crud__input",
                    value,
                    onChange: (
                      event:
                        | ChangeEvent<HTMLInputElement>
                        | ChangeEvent<HTMLTextAreaElement>
                        | ChangeEvent<HTMLSelectElement>,
                    ) => updateField(field.name, event.target.value),
                  };

                  return (
                    <div
                      key={field.name}
                      className={
                        field.type === "textarea" || field.type === "json"
                          ? "module-crud__field module-crud__field--full"
                          : "module-crud__field"
                      }
                    >
                      <div className="module-crud__field-head">
                        <label className="module-crud__label" htmlFor={fieldId}>
                          {field.label}
                        </label>
                        {field.required ? (
                          <span className="module-crud__field-required">
                            Obrigatorio
                          </span>
                        ) : null}
                      </div>

                      {hasOptions ? (
                        <select {...commonProps}>
                          <option value="">Selecione</option>
                          {field.options?.map((option) => (
                            <option key={option.value} value={option.value}>
                              {option.label}
                            </option>
                          ))}
                        </select>
                      ) : field.type === "textarea" || field.type === "json" ? (
                        <textarea
                          {...commonProps}
                          placeholder={field.placeholder}
                        />
                      ) : (
                        <input
                          {...commonProps}
                          type={field.type}
                          placeholder={field.placeholder}
                        />
                      )}
                    </div>
                  );
                })}
              </div>
            ) : (
              <div className="module-crud__field">
                <div className="module-crud__field-head">
                  <label
                    className="module-crud__label"
                    htmlFor="module-crud-payload"
                  >
                    Payload do formulario
                  </label>
                  <span className="module-crud__field-status">
                    {selectedItem ? "Registro selecionado" : "Aguardando dados"}
                  </span>
                </div>

                <textarea
                  id="module-crud-payload"
                  className="module-crud__textarea"
                  value={payload}
                  onChange={(event) => setPayload(event.target.value)}
                  placeholder={`{\n  "nome": "Exemplo",\n  "status": "ATIVO"\n}`}
                />
              </div>
            )}

            <div className="module-crud__editor-actions">
              <button
                type="button"
                className="module-crud__button module-crud__button--ghost"
                onClick={resetEditor}
              >
                Limpar formulario
              </button>
              <button
                type="button"
                className="module-crud__button"
                onClick={selectedItem ? handleUpdate : handleCreate}
              >
                {selectedItem ? "Salvar alteracoes" : "Criar registro"}
              </button>
            </div>

            <p className="module-crud__hint">
              {formSchema
                ? "Preencha os campos do formulario. Para editar, selecione uma linha da tabela e ajuste apenas os dados necessarios."
                : "Para criar, informe um JSON valido. Para editar, selecione uma linha da tabela e ajuste apenas os campos necessarios."}
            </p>
          </div>
        </aside>
      </div>
    </div>
  );
}
