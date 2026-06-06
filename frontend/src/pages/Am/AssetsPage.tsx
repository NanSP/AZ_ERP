import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import AssetForm from "../../components/Am/AssetForm";
import AssetsTable from "../../components/Am/AssetsTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./assets-page.css";

export type PartnerOption = {
  id: number;
  label: string;
};

export type PartnerAccess = "idle" | "loaded" | "unavailable";

export type EmployeeOption = {
  id: number;
  label: string;
};

export type EmployeeAccess = "idle" | "loaded" | "unavailable";

export type Asset = {
  id?: number;
  codigoPatrimonio: string;
  nome: string;
  descricao: string;
  tipoAtivo: string;
  localizacao: string;
  dataAquisicao: string;
  valorAquisicao: string;
  valorAtual: string;
  vidaUtilAnos: string;
  taxaDepreciacao: string;
  dataDepreciacao: string;
  fornecedor: string;
  responsavel: string;
  status: string;
  createdAt?: string;
};

type AssetsPageProps = {
  embedded?: boolean;
};

const emptyAsset: Asset = {
  codigoPatrimonio: "",
  nome: "",
  descricao: "",
  tipoAtivo: "",
  localizacao: "",
  dataAquisicao: "",
  valorAquisicao: "",
  valorAtual: "",
  vidaUtilAnos: "",
  taxaDepreciacao: "",
  dataDepreciacao: "",
  fornecedor: "",
  responsavel: "",
  status: "ativo",
};

function normalizeAsset(data: Record<string, unknown>): Asset {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    codigoPatrimonio: String(data.codigoPatrimonio ?? ""),
    nome: String(data.nome ?? ""),
    descricao: String(data.descricao ?? ""),
    tipoAtivo: String(data.tipoAtivo ?? ""),
    localizacao: String(data.localizacao ?? ""),
    dataAquisicao: String(data.dataAquisicao ?? ""),
    valorAquisicao: data.valorAquisicao == null ? "" : String(data.valorAquisicao),
    valorAtual: data.valorAtual == null ? "" : String(data.valorAtual),
    vidaUtilAnos: data.vidaUtilAnos == null ? "" : String(data.vidaUtilAnos),
    taxaDepreciacao:
      data.taxaDepreciacao == null ? "" : String(data.taxaDepreciacao),
    dataDepreciacao: String(data.dataDepreciacao ?? ""),
    fornecedor: data.fornecedor == null ? "" : String(data.fornecedor),
    responsavel: data.responsavel == null ? "" : String(data.responsavel),
    status: String(data.status ?? "ativo"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value.replace(",", "."));
}

function toRequestPayload(asset: Asset) {
  return {
    codigoPatrimonio: asset.codigoPatrimonio.trim() || null,
    nome: asset.nome.trim() || null,
    descricao: asset.descricao.trim() || null,
    tipoAtivo: asset.tipoAtivo.trim() || null,
    localizacao: asset.localizacao.trim() || null,
    dataAquisicao: asset.dataAquisicao.trim() || null,
    valorAquisicao: toNullableNumber(asset.valorAquisicao),
    valorAtual: toNullableNumber(asset.valorAtual),
    vidaUtilAnos:
      asset.vidaUtilAnos.trim() === "" ? null : Number(asset.vidaUtilAnos),
    taxaDepreciacao: toNullableNumber(asset.taxaDepreciacao),
    dataDepreciacao: asset.dataDepreciacao.trim() || null,
    fornecedor: asset.fornecedor.trim() === "" ? null : Number(asset.fornecedor),
    responsavel:
      asset.responsavel.trim() === "" ? null : Number(asset.responsavel),
    status: asset.status.trim() || null,
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

export default function AssetsPage({ embedded = false }: AssetsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<Asset[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Asset | null>(null);
  const [draft, setDraft] = useState<Asset>(emptyAsset);
  const [partnerOptions, setPartnerOptions] = useState<PartnerOption[]>([]);
  const [partnerAccess, setPartnerAccess] = useState<PartnerAccess>("idle");
  const [employeeOptions, setEmployeeOptions] = useState<EmployeeOption[]>([]);
  const [employeeAccess, setEmployeeAccess] = useState<EmployeeAccess>("idle");
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("am:bensPatrimoniais:read");
  const canCreate =
    isMasterScope || permissionSet.has("am:bensPatrimoniais:create");
  const canUpdate =
    isMasterScope || permissionSet.has("am:bensPatrimoniais:update");
  const canDelete =
    isMasterScope || permissionSet.has("am:bensPatrimoniais:delete");
  const canReadPartners =
    isMasterScope || permissionSet.has("core:parceiros:read");
  const canReadEmployees =
    isMasterScope || permissionSet.has("rh:colaboradores:read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadAssets() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyAsset });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("am", "bensPatrimoniais");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeAsset(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar os bens patrimoniais."),
      );
    } finally {
      setLoading(false);
    }
  }

  async function loadPartners() {
    if (!canReadPartners) {
      setPartnerOptions([]);
      setPartnerAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("core", "parceiros");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nome ?? "Parceiro")} (#${String(item.id)})`,
            }))
        : [];
      setPartnerOptions(nextItems);
      setPartnerAccess("loaded");
    } catch {
      setPartnerOptions([]);
      setPartnerAccess("unavailable");
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
    void loadAssets();
  }, [canRead]);

  useEffect(() => {
    void loadPartners();
  }, [canReadPartners]);

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
        item.codigoPatrimonio,
        item.nome,
        item.tipoAtivo,
        item.localizacao,
        item.status,
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
    setDraft({ ...emptyAsset });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Asset) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Asset) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar bens patrimoniais."
          : "Seu perfil nao possui permissao para criar bens patrimoniais.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("am", "bensPatrimoniais", selected.id, payload)
        : await createResource("am", "bensPatrimoniais", payload);

      const saved = normalizeAsset(response.data as Record<string, unknown>);
      await loadAssets();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Bem patrimonial atualizado com sucesso."
          : "Bem patrimonial criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o bem patrimonial."
            : "Nao foi possivel criar o bem patrimonial.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Asset) {
    if (!canDelete) {
      setError(
        "Seu perfil nao possui permissao para excluir bens patrimoniais.",
      );
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o bem patrimonial para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o bem "${item.nome || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("am", "bensPatrimoniais", item.id);
      await loadAssets();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyAsset });
      }

      setSuccess("Bem patrimonial excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Nao foi possivel excluir o bem patrimonial.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "assets-page assets-page--embedded" : "assets-page"}>
      {!embedded ? (
        <header className="assets-page__header">
          <div>
            <span className="assets-page__eyebrow">AM</span>
            <h2 className="assets-page__title">Bens Patrimoniais</h2>
            <p className="assets-page__subtitle">
              Gerencie ativos, valores, depreciacao e responsabilidades
              operacionais.
            </p>
          </div>

          <div className="assets-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por codigo, nome, tipo, localizacao ou status"
              className="assets-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="assets-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo bem
            </button>
          </div>
        </header>
      ) : (
        <div className="assets-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por codigo, nome, tipo, localizacao ou status"
            className="assets-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="assets-page__toolbar-actions">
            <button
              type="button"
              className="assets-page__ghost"
              onClick={() => void loadAssets()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="assets-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo bem
            </button>
          </div>
        </div>
      )}

      {error ? <div className="assets-page__alert">{error}</div> : null}
      {success ? (
        <div className="assets-page__alert assets-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="assets-page__alert assets-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="assets-page__layout">
        <AssetsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          partnerOptions={partnerOptions}
          employeeOptions={employeeOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <AssetForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          partnerOptions={partnerOptions}
          partnerAccess={partnerAccess}
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
