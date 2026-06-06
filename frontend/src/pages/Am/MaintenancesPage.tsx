import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import MaintenanceForm from "../../components/Am/MaintenanceForm";
import MaintenanceTable from "../../components/Am/MaintenanceTable";
import { useAuth } from "../../auth/useAuth";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./maintenances-page.css";

export type AssetOption = {
  id: number;
  label: string;
};

export type AssetAccess = "idle" | "loaded" | "unavailable";

export type EmployeeOption = {
  id: number;
  label: string;
};

export type EmployeeAccess = "idle" | "loaded" | "unavailable";

export type MaintenanceEntry = {
  id?: number;
  ativo: string;
  tipoManutencao: string;
  dataSolicitacao: string;
  dataExecucao: string;
  descricao: string;
  custoMaoObra: string;
  custoMaterial: string;
  custoTotal: string;
  tecnico: string;
  createdAt?: string;
};

type MaintenancesPageProps = {
  embedded?: boolean;
};

const emptyMaintenance: MaintenanceEntry = {
  ativo: "",
  tipoManutencao: "",
  dataSolicitacao: "",
  dataExecucao: "",
  descricao: "",
  custoMaoObra: "",
  custoMaterial: "",
  custoTotal: "",
  tecnico: "",
};

function normalizeMaintenance(
  data: Record<string, unknown>,
): MaintenanceEntry {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    ativo: data.ativo == null ? "" : String(data.ativo),
    tipoManutencao: String(data.tipoManutencao ?? ""),
    dataSolicitacao: String(data.dataSolicitacao ?? ""),
    dataExecucao: String(data.dataExecucao ?? ""),
    descricao: String(data.descricao ?? ""),
    custoMaoObra: data.custoMaoObra == null ? "" : String(data.custoMaoObra),
    custoMaterial:
      data.custoMaterial == null ? "" : String(data.custoMaterial),
    custoTotal: data.custoTotal == null ? "" : String(data.custoTotal),
    tecnico: data.tecnico == null ? "" : String(data.tecnico),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value.replace(",", "."));
}

function toRequestPayload(entry: MaintenanceEntry) {
  return {
    ativo: entry.ativo.trim() === "" ? null : Number(entry.ativo),
    tipoManutencao: entry.tipoManutencao.trim() || null,
    dataSolicitacao: entry.dataSolicitacao.trim() || null,
    dataExecucao: entry.dataExecucao.trim() || null,
    descricao: entry.descricao.trim() || null,
    custoMaoObra: toNullableNumber(entry.custoMaoObra),
    custoMaterial: toNullableNumber(entry.custoMaterial),
    tecnico: entry.tecnico.trim() === "" ? null : Number(entry.tecnico),
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

export default function MaintenancesPage({
  embedded = false,
}: MaintenancesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<MaintenanceEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<MaintenanceEntry | null>(null);
  const [draft, setDraft] = useState<MaintenanceEntry>(emptyMaintenance);
  const [assetOptions, setAssetOptions] = useState<AssetOption[]>([]);
  const [assetAccess, setAssetAccess] = useState<AssetAccess>("idle");
  const [employeeOptions, setEmployeeOptions] = useState<EmployeeOption[]>([]);
  const [employeeAccess, setEmployeeAccess] = useState<EmployeeAccess>("idle");
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("am:manutencoes:read");
  const canCreate = isMasterScope || permissionSet.has("am:manutencoes:create");
  const canUpdate = isMasterScope || permissionSet.has("am:manutencoes:update");
  const canDelete = isMasterScope || permissionSet.has("am:manutencoes:delete");
  const canReadAssets =
    isMasterScope || permissionSet.has("am:bensPatrimoniais:read");
  const canReadEmployees =
    isMasterScope || permissionSet.has("rh:colaboradores:read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadMaintenances() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyMaintenance });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("am", "manutencoes");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeMaintenance(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar as manutencoes."));
    } finally {
      setLoading(false);
    }
  }

  async function loadAssets() {
    if (!canReadAssets) {
      setAssetOptions([]);
      setAssetAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("am", "bensPatrimoniais");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nome ?? "Ativo")} (#${String(item.id)})`,
            }))
        : [];
      setAssetOptions(nextItems);
      setAssetAccess("loaded");
    } catch {
      setAssetOptions([]);
      setAssetAccess("unavailable");
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
    void loadMaintenances();
  }, [canRead]);

  useEffect(() => {
    void loadAssets();
  }, [canReadAssets]);

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
        item.ativo,
        item.tipoManutencao,
        item.dataSolicitacao,
        item.dataExecucao,
        item.descricao,
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
    setDraft({ ...emptyMaintenance });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: MaintenanceEntry) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: MaintenanceEntry) {
    const custoMaoObra = toNullableNumber(next.custoMaoObra) ?? 0;
    const custoMaterial = toNullableNumber(next.custoMaterial) ?? 0;

    setDraft({
      ...next,
      custoTotal: String(custoMaoObra + custoMaterial),
    });
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar manutencoes."
          : "Seu perfil nao possui permissao para criar manutencoes.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("am", "manutencoes", selected.id, payload)
        : await createResource("am", "manutencoes", payload);

      const saved = normalizeMaintenance(response.data as Record<string, unknown>);
      await loadMaintenances();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Manutencao atualizada com sucesso."
          : "Manutencao criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar a manutencao."
            : "Nao foi possivel criar a manutencao.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: MaintenanceEntry) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir manutencoes.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar a manutencao para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a manutencao "${item.tipoManutencao || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("am", "manutencoes", item.id);
      await loadMaintenances();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyMaintenance });
      }

      setSuccess("Manutencao excluida com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir a manutencao."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "maintenances-page maintenances-page--embedded"
          : "maintenances-page"
      }
    >
      {!embedded ? (
        <header className="maintenances-page__header">
          <div>
            <span className="maintenances-page__eyebrow">AM</span>
            <h2 className="maintenances-page__title">Manutencoes</h2>
            <p className="maintenances-page__subtitle">
              Controle manutencoes preventivas, corretivas e preditivas dos ativos.
            </p>
          </div>

          <div className="maintenances-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por ativo, tipo, data ou descricao"
              className="maintenances-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="maintenances-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova manutencao
            </button>
          </div>
        </header>
      ) : (
        <div className="maintenances-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por ativo, tipo, data ou descricao"
            className="maintenances-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="maintenances-page__toolbar-actions">
            <button
              type="button"
              className="maintenances-page__ghost"
              onClick={() => void loadMaintenances()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="maintenances-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova manutencao
            </button>
          </div>
        </div>
      )}

      {error ? <div className="maintenances-page__alert">{error}</div> : null}
      {success ? (
        <div className="maintenances-page__alert maintenances-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="maintenances-page__alert maintenances-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="maintenances-page__layout">
        <MaintenanceTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          assetOptions={assetOptions}
          employeeOptions={employeeOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <MaintenanceForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          assetOptions={assetOptions}
          assetAccess={assetAccess}
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
