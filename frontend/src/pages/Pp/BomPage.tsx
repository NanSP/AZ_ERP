import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import BomForm from "../../components/Pp/BomForm";
import BomTable from "../../components/Pp/BomTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./bom-page.css";

export type ProductOption = {
  id: number;
  label: string;
};

export type ProductAccess = "idle" | "loaded" | "unavailable";

export type BomItem = {
  id?: number;
  produtoPai: string;
  componente: string;
  quantidade: string;
  unidadeMedida: string;
  nivel: string;
  tempoPreparacao: string;
  tempoProducao: string;
  roteiro: string;
  createdAt?: string;
};

type BomPageProps = {
  embedded?: boolean;
};

const bomResource = {
  schema: "pp",
  entity: "bom",
  label: "BOM",
  description: "Estruturas de materiais e composicao.",
} as const;

const productsResource = {
  schema: "core",
  entity: "produtos",
  label: "Produtos",
  description: "Produtos e itens.",
} as const;

const emptyBom: BomItem = {
  produtoPai: "",
  componente: "",
  quantidade: "",
  unidadeMedida: "",
  nivel: "0",
  tempoPreparacao: "",
  tempoProducao: "",
  roteiro: "",
};

function normalizeBom(data: Record<string, unknown>): BomItem {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    produtoPai: data.produtoPai == null ? "" : String(data.produtoPai),
    componente: data.componente == null ? "" : String(data.componente),
    quantidade: data.quantidade == null ? "" : String(data.quantidade),
    unidadeMedida: String(data.unidadeMedida ?? ""),
    nivel: data.nivel == null ? "0" : String(data.nivel),
    tempoPreparacao:
      data.tempoPreparacao == null ? "" : String(data.tempoPreparacao),
    tempoProducao:
      data.tempoProducao == null ? "" : String(data.tempoProducao),
    roteiro: data.roteiro == null ? "" : String(data.roteiro),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value.replace(",", "."));
}

function toNullableInteger(value: string) {
  return value.trim() === "" ? null : Number(value);
}

function toRequestPayload(item: BomItem) {
  return {
    produtoPai: item.produtoPai.trim() === "" ? null : Number(item.produtoPai),
    componente: item.componente.trim() === "" ? null : Number(item.componente),
    quantidade: toNullableNumber(item.quantidade),
    unidadeMedida: item.unidadeMedida.trim() || null,
    nivel: toNullableInteger(item.nivel),
    tempoPreparacao: toNullableNumber(item.tempoPreparacao),
    tempoProducao: toNullableNumber(item.tempoProducao),
    roteiro: toNullableInteger(item.roteiro),
  };
}

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message =
      error.response?.data?.message ?? error.response?.data?.error;

    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  return fallback;
}

function mapProductOption(item: Record<string, unknown>): ProductOption | null {
  if (typeof item.id !== "number") {
    return null;
  }

  return {
    id: item.id,
    label: `${String(item.nome ?? item.codigo ?? "Produto")} (#${String(item.id)})`,
  };
}

export default function BomPage({ embedded = false }: BomPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<BomItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<BomItem | null>(null);
  const [draft, setDraft] = useState<BomItem>(emptyBom);
  const [productOptions, setProductOptions] = useState<ProductOption[]>([]);
  const [productAccess, setProductAccess] = useState<ProductAccess>("idle");
  const canRead = canAccessResourceAction(session, bomResource, "read");
  const canCreate = canAccessResourceAction(session, bomResource, "create");
  const canUpdate = canAccessResourceAction(session, bomResource, "update");
  const canDelete = canAccessResourceAction(session, bomResource, "delete");
  const canReadProducts = canAccessResourceAction(
    session,
    productsResource,
    "read",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadBomItems() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyBom });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("pp", "bom");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) => normalizeBom(item as Record<string, unknown>))
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar a BOM."));
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
            .map((item) => mapProductOption(item as Record<string, unknown>))
            .filter((item): item is ProductOption => item !== null)
        : [];
      setProductOptions(nextItems);
      setProductAccess("loaded");
    } catch {
      setProductOptions([]);
      setProductAccess("unavailable");
    }
  }

  useEffect(() => {
    void loadBomItems();
  }, [canRead]);

  useEffect(() => {
    void loadProducts();
  }, [canReadProducts]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.unidadeMedida, item.nivel, item.roteiro]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyBom });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: BomItem) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: BomItem) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar a BOM."
          : "Seu perfil nao possui permissao para criar a BOM.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("pp", "bom", selected.id, payload)
        : await createResource("pp", "bom", payload);

      const saved = normalizeBom(response.data as Record<string, unknown>);
      await loadBomItems();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "BOM atualizada com sucesso."
          : "BOM criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar a BOM."
            : "Nao foi possivel criar a BOM.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: BomItem) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir a BOM.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar a BOM para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a composicao BOM #${item.id}?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("pp", "bom", item.id);
      await loadBomItems();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyBom });
      }

      setSuccess("BOM excluida com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir a BOM."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "bom-page bom-page--embedded" : "bom-page"}>
      {!embedded ? (
        <header className="bom-page__header">
          <div>
            <span className="bom-page__eyebrow">PP</span>
            <h2 className="bom-page__title">BOM</h2>
            <p className="bom-page__subtitle">
              Estruture produto pai, componentes, niveis e tempos da composicao
              produtiva.
            </p>
          </div>

          <div className="bom-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por unidade, nivel ou roteiro"
              className="bom-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="bom-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova composicao
            </button>
          </div>
        </header>
      ) : (
        <div className="bom-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por unidade, nivel ou roteiro"
            className="bom-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="bom-page__toolbar-actions">
            <button
              type="button"
              className="bom-page__ghost"
              onClick={() => void loadBomItems()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="bom-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova composicao
            </button>
          </div>
        </div>
      )}

      {error ? <div className="bom-page__alert">{error}</div> : null}
      {success ? (
        <div className="bom-page__alert bom-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="bom-page__alert bom-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="bom-page__layout">
        <BomTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          productOptions={productOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <BomForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          productOptions={productOptions}
          productAccess={productAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
