import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import DependentForm from "../../components/Rh/DependentForm";
import DependentTable from "../../components/Rh/DependentTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./dependents-page.css";

type EmployeeOption = {
  id: number;
  label: string;
};

type EmployeeAccess = "idle" | "loaded" | "unavailable";

export type Dependent = {
  id?: number;
  colaborador: string;
  nome: string;
  dataNascimento: string;
  parentesco: string;
  cpf: string;
  createdAt?: string;
};

type DependentsPageProps = {
  embedded?: boolean;
};

const emptyDependent: Dependent = {
  colaborador: "",
  nome: "",
  dataNascimento: "",
  parentesco: "",
  cpf: "",
};

function normalizeDependent(data: Record<string, unknown>): Dependent {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    colaborador: data.colaborador == null ? "" : String(data.colaborador),
    nome: String(data.nome ?? ""),
    dataNascimento: String(data.dataNascimento ?? ""),
    parentesco: String(data.parentesco ?? ""),
    cpf: String(data.cpf ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(dependent: Dependent) {
  return {
    colaborador:
      dependent.colaborador.trim() === ""
        ? null
        : Number(dependent.colaborador),
    nome: dependent.nome.trim() || null,
    dataNascimento: dependent.dataNascimento.trim() || null,
    parentesco: dependent.parentesco.trim() || null,
    cpf: dependent.cpf.replace(/\D/g, "") || null,
  };
}

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message = error.response?.data?.message;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  return fallback;
}

export default function DependentsPage({
  embedded = false,
}: DependentsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<Dependent[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Dependent | null>(null);
  const [draft, setDraft] = useState<Dependent>(emptyDependent);
  const [employeeOptions, setEmployeeOptions] = useState<EmployeeOption[]>([]);
  const [employeeAccess, setEmployeeAccess] = useState<EmployeeAccess>("idle");
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("rh:dependentes:read");
  const canCreate = isMasterScope || permissionSet.has("rh:dependentes:create");
  const canUpdate = isMasterScope || permissionSet.has("rh:dependentes:update");
  const canDelete = isMasterScope || permissionSet.has("rh:dependentes:delete");
  const canReadEmployees =
    isMasterScope || permissionSet.has("rh:colaboradores:read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadDependents = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyDependent });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("rh", "dependentes");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeDependent(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível carregar os dependentes."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadEmployees = useCallback(async () => {
    if (!canReadEmployees) {
      setEmployeeOptions([]);
      setEmployeeAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("rh", "colaboradores");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nome ?? "Colaborador")} (#${String(item.id)})`,
            }))
        : [];
      setEmployeeOptions(nextItems);
      setEmployeeAccess("loaded");
    } catch {
      setEmployeeOptions([]);
      setEmployeeAccess("unavailable");
    }
  }, [canReadEmployees]);

  useEffect(() => {
    void loadDependents();
  }, [loadDependents]);

  useEffect(() => {
    void loadEmployees();
  }, [loadEmployees]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.nome, item.cpf, item.parentesco, item.colaborador]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyDependent });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Dependent) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Dependent) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar dependentes."
          : "Seu perfil não possui permissão para criar dependentes.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("rh", "dependentes", selected.id, payload)
        : await createResource("rh", "dependentes", payload);

      const saved = normalizeDependent(
        response.data as Record<string, unknown>,
      );
      await loadDependents();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Dependente atualizado com sucesso."
          : "Dependente criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar o dependente."
            : "Não foi possível criar o dependente.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Dependent) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir dependentes.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar o dependente para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o dependente "${item.nome || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("rh", "dependentes", item.id);
      await loadDependents();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyDependent });
      }

      setSuccess("Dependente excluído com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível excluir o dependente."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "dependents-page dependents-page--embedded"
          : "dependents-page"
      }
    >
      {!embedded ? (
        <header className="dependents-page__header">
          <div>
            <span className="dependents-page__eyebrow">RH</span>
            <h2 className="dependents-page__title">Dependentes</h2>
            <p className="dependents-page__subtitle">
              Gerencie o cadastro de dependentes vinculados aos colaboradores
              com parentesco, CPF e data de nascimento.
            </p>
          </div>

          <div className="dependents-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por nome, CPF, parentesco ou colaborador"
              className="dependents-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="dependents-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo dependente
            </button>
          </div>
        </header>
      ) : (
        <div className="dependents-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por nome, CPF, parentesco ou colaborador"
            className="dependents-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="dependents-page__toolbar-actions">
            <button
              type="button"
              className="dependents-page__ghost"
              onClick={() => void loadDependents()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="dependents-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo dependente
            </button>
          </div>
        </div>
      )}

      {error ? <div className="dependents-page__alert">{error}</div> : null}
      {success ? (
        <div className="dependents-page__alert dependents-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="dependents-page__alert dependents-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="dependents-page__layout">
        <DependentTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          employeeOptions={employeeOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <DependentForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          employeeOptions={employeeOptions}
          employeeAccess={employeeAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
