import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import InspectionForm from "../../components/Qm/InspectionForm";
import InspectionsTable from "../../components/Qm/InspectionsTable";
import { useAuth } from "../../auth/useAuth";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./inspections-page.css";

export type ProductOption = {
  id: number;
  label: string;
};

export type ProductAccess = "idle" | "loaded" | "unavailable";

export type EmployeeOption = {
  id: number;
  label: string;
};

export type EmployeeAccess = "idle" | "loaded" | "unavailable";

export type InspectionEntry = {
  id?: number;
  tipoInspecao: string;
  produto: string;
  lote: string;
  quantidadeInspecionada: string;
  quantidadeAprovada: string;
  quantidadeReprovada: string;
  dataInspecao: string;
  inspetor: string;
  resultado: string;
  observacoes: string;
  createdAt?: string;
};

type InspectionsPageProps = {
  embedded?: boolean;
};

const emptyInspection: InspectionEntry = {
  tipoInspecao: "",
  produto: "",
  lote: "",
  quantidadeInspecionada: "",
  quantidadeAprovada: "",
  quantidadeReprovada: "",
  dataInspecao: "",
  inspetor: "",
  resultado: "",
  observacoes: "",
};

function normalizeInspection(data: Record<string, unknown>): InspectionEntry {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    tipoInspecao: String(data.tipoInspecao ?? ""),
    produto: data.produto == null ? "" : String(data.produto),
    lote: String(data.lote ?? ""),
    quantidadeInspecionada:
      data.quantidadeInspecionada == null
        ? ""
        : String(data.quantidadeInspecionada),
    quantidadeAprovada:
      data.quantidadeAprovada == null ? "" : String(data.quantidadeAprovada),
    quantidadeReprovada:
      data.quantidadeReprovada == null ? "" : String(data.quantidadeReprovada),
    dataInspecao: String(data.dataInspecao ?? ""),
    inspetor: data.inspetor == null ? "" : String(data.inspetor),
    resultado: String(data.resultado ?? ""),
    observacoes: String(data.observacoes ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value.replace(",", "."));
}

function toRequestPayload(entry: InspectionEntry) {
  return {
    tipoInspecao: entry.tipoInspecao.trim() || null,
    produto: entry.produto.trim() === "" ? null : Number(entry.produto),
    lote: entry.lote.trim() || null,
    quantidadeInspecionada: toNullableNumber(entry.quantidadeInspecionada),
    quantidadeAprovada: toNullableNumber(entry.quantidadeAprovada),
    quantidadeReprovada: toNullableNumber(entry.quantidadeReprovada),
    dataInspecao: entry.dataInspecao.trim() || null,
    inspetor: entry.inspetor.trim() === "" ? null : Number(entry.inspetor),
    resultado: entry.resultado.trim() || null,
    observacoes: entry.observacoes.trim() || null,
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

export default function InspectionsPage({
  embedded = false,
}: InspectionsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<InspectionEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<InspectionEntry | null>(null);
  const [draft, setDraft] = useState<InspectionEntry>(emptyInspection);
  const [productOptions, setProductOptions] = useState<ProductOption[]>([]);
  const [productAccess, setProductAccess] = useState<ProductAccess>("idle");
  const [employeeOptions, setEmployeeOptions] = useState<EmployeeOption[]>([]);
  const [employeeAccess, setEmployeeAccess] = useState<EmployeeAccess>("idle");
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("qm:inspecoes:read");
  const canCreate = isMasterScope || permissionSet.has("qm:inspecoes:create");
  const canUpdate = isMasterScope || permissionSet.has("qm:inspecoes:update");
  const canDelete = isMasterScope || permissionSet.has("qm:inspecoes:delete");
  const canReadProducts =
    isMasterScope || permissionSet.has("core:produtos:read");
  const canReadEmployees =
    isMasterScope || permissionSet.has("rh:colaboradores:read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadInspections() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyInspection });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("qm", "inspecoes");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeInspection(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar as inspecoes."));
    } finally {
      setLoading(false);
    }
  }

  async function loadProducts() {
    if (!canReadProducts) {
      setProductOptions([]);
      setProductAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("core", "produtos");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nome ?? "Produto")} (#${String(item.id)})`,
            }))
        : [];
      setProductOptions(nextItems);
      setProductAccess("loaded");
    } catch {
      setProductOptions([]);
      setProductAccess("unavailable");
    }
  }

  async function loadEmployees() {
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
  }

  useEffect(() => {
    void loadInspections();
  }, [canRead]);

  useEffect(() => {
    void loadProducts();
  }, [canReadProducts]);

  useEffect(() => {
    void loadEmployees();
  }, [canReadEmployees]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.tipoInspecao,
        item.lote,
        item.resultado,
        item.dataInspecao,
        item.observacoes,
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
    setDraft({ ...emptyInspection });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: InspectionEntry) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: InspectionEntry) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar inspecoes."
          : "Seu perfil nao possui permissao para criar inspecoes.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("qm", "inspecoes", selected.id, payload)
        : await createResource("qm", "inspecoes", payload);

      const saved = normalizeInspection(response.data as Record<string, unknown>);
      await loadInspections();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Inspecao atualizada com sucesso."
          : "Inspecao criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar a inspecao."
            : "Nao foi possivel criar a inspecao.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: InspectionEntry) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir inspecoes.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar a inspecao para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a inspecao "${item.tipoInspecao || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("qm", "inspecoes", item.id);
      await loadInspections();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyInspection });
      }

      setSuccess("Inspecao excluida com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir a inspecao."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "inspections-page inspections-page--embedded" : "inspections-page"
      }
    >
      {!embedded ? (
        <header className="inspections-page__header">
          <div>
            <span className="inspections-page__eyebrow">QM</span>
            <h2 className="inspections-page__title">Inspecoes</h2>
            <p className="inspections-page__subtitle">
              Gerencie inspecoes de recebimento, processo, final e expedicao com resultado e rastreabilidade.
            </p>
          </div>

          <div className="inspections-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por tipo, lote, resultado ou data"
              className="inspections-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="inspections-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova inspecao
            </button>
          </div>
        </header>
      ) : (
        <div className="inspections-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por tipo, lote, resultado ou data"
            className="inspections-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="inspections-page__toolbar-actions">
            <button
              type="button"
              className="inspections-page__ghost"
              onClick={() => void loadInspections()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="inspections-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova inspecao
            </button>
          </div>
        </div>
      )}

      {error ? <div className="inspections-page__alert">{error}</div> : null}
      {success ? (
        <div className="inspections-page__alert inspections-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="inspections-page__alert inspections-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="inspections-page__layout">
        <InspectionsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          productOptions={productOptions}
          employeeOptions={employeeOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <InspectionForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          productOptions={productOptions}
          productAccess={productAccess}
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
