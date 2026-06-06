import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import CostCenterForm from "../../components/Fi/CostCenterForm";
import CostCenterTable from "../../components/Fi/CostCenterTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./cost-centers-page.css";

export type CostCenter = {
  id?: number;
  codigo: string;
  nome: string;
  tipo: string;
  responsavel: string;
  ativo: boolean;
};

type CostCentersPageProps = {
  embedded?: boolean;
};

const emptyCostCenter: CostCenter = {
  codigo: "",
  nome: "",
  tipo: "",
  responsavel: "",
  ativo: true,
};

function normalizeCostCenter(data: Record<string, unknown>): CostCenter {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    codigo: String(data.codigo ?? ""),
    nome: String(data.nome ?? ""),
    tipo: String(data.tipo ?? ""),
    responsavel: String(data.responsavel ?? ""),
    ativo: Boolean(data.ativo),
  };
}

function toRequestPayload(costCenter: CostCenter) {
  return {
    codigo: costCenter.codigo.trim(),
    nome: costCenter.nome.trim(),
    tipo: costCenter.tipo.trim() || null,
    responsavel: costCenter.responsavel.trim() || null,
    ativo: costCenter.ativo,
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

export default function CostCentersPage({
  embedded = false,
}: CostCentersPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<CostCenter[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<CostCenter | null>(null);
  const [draft, setDraft] = useState<CostCenter>(emptyCostCenter);
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("fi:centros_custo:read");
  const canCreate =
    isMasterScope || permissionSet.has("fi:centros_custo:create");
  const canUpdate =
    isMasterScope || permissionSet.has("fi:centros_custo:update");
  const canDelete =
    isMasterScope || permissionSet.has("fi:centros_custo:delete");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadCostCenters() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyCostCenter });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("fi", "centrosCusto");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeCostCenter(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar os centros de custo."),
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadCostCenters();
  }, [canRead]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.codigo, item.nome, item.tipo, item.responsavel]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyCostCenter });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: CostCenter) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: CostCenter) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar centros de custo."
          : "Seu perfil nao possui permissao para criar centros de custo.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("fi", "centrosCusto", selected.id, payload)
        : await createResource("fi", "centrosCusto", payload);

      const saved = normalizeCostCenter(response.data as Record<string, unknown>);
      await loadCostCenters();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Centro de custo atualizado com sucesso."
          : "Centro de custo criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o centro de custo."
            : "Nao foi possivel criar o centro de custo.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: CostCenter) {
    if (!canDelete) {
      setError(
        "Seu perfil nao possui permissao para excluir centros de custo.",
      );
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o centro de custo para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o centro de custo "${item.codigo || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("fi", "centrosCusto", item.id);
      await loadCostCenters();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyCostCenter });
      }

      setSuccess("Centro de custo excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir o centro de custo."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "cost-centers-page cost-centers-page--embedded"
          : "cost-centers-page"
      }
    >
      {!embedded ? (
        <header className="cost-centers-page__header">
          <div>
            <span className="cost-centers-page__eyebrow">FI</span>
            <h2 className="cost-centers-page__title">Centros de Custo</h2>
            <p className="cost-centers-page__subtitle">
              Estruture a classificacao financeira por codigo, nome,
              responsavel e situacao.
            </p>
          </div>

          <div className="cost-centers-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por codigo, nome, tipo ou responsavel"
              className="cost-centers-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="cost-centers-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo centro
            </button>
          </div>
        </header>
      ) : (
        <div className="cost-centers-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por codigo, nome, tipo ou responsavel"
            className="cost-centers-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="cost-centers-page__toolbar-actions">
            <button
              type="button"
              className="cost-centers-page__ghost"
              onClick={() => void loadCostCenters()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="cost-centers-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo centro
            </button>
          </div>
        </div>
      )}

      {error ? <div className="cost-centers-page__alert">{error}</div> : null}
      {success ? (
        <div className="cost-centers-page__alert cost-centers-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="cost-centers-page__alert cost-centers-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="cost-centers-page__layout">
        <CostCenterTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <CostCenterForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
