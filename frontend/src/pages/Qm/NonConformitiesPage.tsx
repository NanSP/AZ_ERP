import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import NonConformityForm from "../../components/Qm/NonConformityForm";
import NonConformitiesTable from "../../components/Qm/NonConformitiesTable";
import { useAuth } from "../../auth/useAuth";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./non-conformities-page.css";

export type InspectionOption = {
  id: number;
  label: string;
};

export type InspectionAccess = "idle" | "loaded" | "unavailable";

export type EmployeeOption = {
  id: number;
  label: string;
};

export type EmployeeAccess = "idle" | "loaded" | "unavailable";

export type NonConformityEntry = {
  id?: number;
  inspecao: string;
  tipoNaoConformidade: string;
  descricao: string;
  causaRaiz: string;
  acaoImediata: string;
  acaoCorretiva: string;
  responsavel: string;
  dataIdentificacao: string;
  dataResolucao: string;
  status: string;
  createdAt?: string;
};

type NonConformitiesPageProps = {
  embedded?: boolean;
};

const emptyEntry: NonConformityEntry = {
  inspecao: "",
  tipoNaoConformidade: "",
  descricao: "",
  causaRaiz: "",
  acaoImediata: "",
  acaoCorretiva: "",
  responsavel: "",
  dataIdentificacao: "",
  dataResolucao: "",
  status: "aberta",
};

function normalizeEntry(data: Record<string, unknown>): NonConformityEntry {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    inspecao: data.inspecao == null ? "" : String(data.inspecao),
    tipoNaoConformidade: String(data.tipoNaoConformidade ?? ""),
    descricao: String(data.descricao ?? ""),
    causaRaiz: String(data.causaRaiz ?? ""),
    acaoImediata: String(data.acaoImediata ?? ""),
    acaoCorretiva: String(data.acaoCorretiva ?? ""),
    responsavel: data.responsavel == null ? "" : String(data.responsavel),
    dataIdentificacao: String(data.dataIdentificacao ?? ""),
    dataResolucao: String(data.dataResolucao ?? ""),
    status: String(data.status ?? "aberta"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value);
}

function toRequestPayload(entry: NonConformityEntry) {
  return {
    inspecao: toNullableNumber(entry.inspecao),
    tipoNaoConformidade: entry.tipoNaoConformidade.trim() || null,
    descricao: entry.descricao.trim() || null,
    causaRaiz: entry.causaRaiz.trim() || null,
    acaoImediata: entry.acaoImediata.trim() || null,
    acaoCorretiva: entry.acaoCorretiva.trim() || null,
    responsavel: toNullableNumber(entry.responsavel),
    dataIdentificacao: entry.dataIdentificacao.trim() || null,
    dataResolucao:
      entry.status === "resolvida" && entry.dataResolucao.trim() !== ""
        ? entry.dataResolucao.trim()
        : null,
    status: entry.status.trim() || null,
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

export default function NonConformitiesPage({
  embedded = false,
}: NonConformitiesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<NonConformityEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<NonConformityEntry | null>(null);
  const [draft, setDraft] = useState<NonConformityEntry>(emptyEntry);
  const [inspectionOptions, setInspectionOptions] = useState<
    InspectionOption[]
  >([]);
  const [inspectionAccess, setInspectionAccess] =
    useState<InspectionAccess>("idle");
  const [employeeOptions, setEmployeeOptions] = useState<EmployeeOption[]>([]);
  const [employeeAccess, setEmployeeAccess] = useState<EmployeeAccess>("idle");
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead =
    isMasterScope || permissionSet.has("qm:nao_conformidade:read");
  const canCreate =
    isMasterScope || permissionSet.has("qm:nao_conformidade:create");
  const canUpdate =
    isMasterScope || permissionSet.has("qm:nao_conformidade:update");
  const canDelete =
    isMasterScope || permissionSet.has("qm:nao_conformidade:delete");
  const canReadInspections =
    isMasterScope || permissionSet.has("qm:inspecoes:read");
  const canReadEmployees =
    isMasterScope || permissionSet.has("rh:colaboradores:read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadNonConformities = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyEntry });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("qm", "naoConformidade");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeEntry(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível carregar as não conformidades."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadInspections = useCallback(async () => {
    if (!canReadInspections) {
      setInspectionOptions([]);
      setInspectionAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("qm", "inspecoes");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.tipoInspecao ?? "Inspecao")} - ${String(item.resultado ?? "sem resultado")} (#${String(item.id)})`,
            }))
        : [];
      setInspectionOptions(nextItems);
      setInspectionAccess("loaded");
    } catch {
      setInspectionOptions([]);
      setInspectionAccess("unavailable");
    }
  }, [canReadInspections]);

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
    void loadNonConformities();
  }, [loadNonConformities]);

  useEffect(() => {
    void loadInspections();
  }, [loadInspections]);

  useEffect(() => {
    void loadEmployees();
  }, [loadEmployees]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.tipoNaoConformidade,
        item.descricao,
        item.status,
        item.dataIdentificacao,
      ]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyEntry });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: NonConformityEntry) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: NonConformityEntry) {
    const patched =
      next.status !== "resolvida" ? { ...next, dataResolucao: "" } : next;
    setDraft(patched);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar não conformidades."
          : "Seu perfil não possui permissão para criar não conformidades.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("qm", "naoConformidade", selected.id, payload)
        : await createResource("qm", "naoConformidade", payload);

      const saved = normalizeEntry(response.data as Record<string, unknown>);
      await loadNonConformities();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Não conformidade atualizada com sucesso."
          : "Não conformidade criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar a não conformidade."
            : "Não foi possível criar a não conformidade.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: NonConformityEntry) {
    if (!canDelete) {
      setError(
        "Seu perfil não possui permissão para excluir não conformidades.",
      );
      return;
    }

    if (!item.id) {
      setError(
        "Não foi possível identificar a não conformidade para exclusão.",
      );
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a não conformidade "${item.tipoNaoConformidade || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("qm", "naoConformidade", item.id);
      await loadNonConformities();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyEntry });
      }

      setSuccess("Não conformidade excluída com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível excluir a não conformidade."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "non-conformities-page non-conformities-page--embedded"
          : "non-conformities-page"
      }
    >
      {!embedded ? (
        <header className="non-conformities-page__header">
          <div>
            <span className="non-conformities-page__eyebrow">QM</span>
            <h2 className="non-conformities-page__title">Não conformidades</h2>
            <p className="non-conformities-page__subtitle">
              Registre, acompanhe e resolva ocorrências de qualidade ligadas as
              inspeções.
            </p>
          </div>

          <div className="non-conformities-page__toolbar-actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por tipo, descricao, status ou data"
              className="non-conformities-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="non-conformities-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova não conformidade
            </button>
          </div>
        </header>
      ) : (
        <div className="non-conformities-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por tipo, descricao, status ou data"
            className="non-conformities-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="non-conformities-page__toolbar-actions">
            <button
              type="button"
              className="non-conformities-page__ghost"
              onClick={() => void loadNonConformities()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="non-conformities-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova não conformidade
            </button>
          </div>
        </div>
      )}

      {error ? (
        <div className="non-conformities-page__alert">{error}</div>
      ) : null}
      {success ? (
        <div className="non-conformities-page__alert non-conformities-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="non-conformities-page__alert non-conformities-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="non-conformities-page__layout">
        <NonConformitiesTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          inspectionOptions={inspectionOptions}
          employeeOptions={employeeOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <NonConformityForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          inspectionOptions={inspectionOptions}
          inspectionAccess={inspectionAccess}
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
