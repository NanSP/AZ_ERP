import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import MaterialForm from "../../components/Mm/MaterialForm";
import MaterialsTable from "../../components/Mm/MaterialsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { Product } from "../Core/ProductsPage";
import "./materials-page.css";

export type MaterialRecord = {
  id?: number;
  produtoId: string;
  tipoMaterial: string;
  categoria: string;
  subcategoria: string;
  marca: string;
  modelo: string;
  especificacoesTecnicas: string;
  condicaoArmazenamento: string;
  classePerigo: string;
  createdAt?: string;
};

type MaterialsPageProps = {
  embedded?: boolean;
};

const materialsResource = {
  schema: "mm",
  entity: "materiais",
  label: "Materiais",
  description: "Cadastro de materiais e insumos.",
} as const;

const productsResource = {
  schema: "core",
  entity: "produtos",
  label: "Produtos",
  description: "Catalogo de produtos e itens.",
} as const;

const emptyMaterial: MaterialRecord = {
  produtoId: "",
  tipoMaterial: "materia_prima",
  categoria: "",
  subcategoria: "",
  marca: "",
  modelo: "",
  especificacoesTecnicas: "",
  condicaoArmazenamento: "",
  classePerigo: "",
};

function normalizeMaterial(data: Record<string, unknown>): MaterialRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    produtoId: data.produto == null ? "" : String(data.produto),
    tipoMaterial: String(data.tipoMaterial ?? "materia_prima"),
    categoria: String(data.categoria ?? ""),
    subcategoria: String(data.subcategoria ?? ""),
    marca: String(data.marca ?? ""),
    modelo: String(data.modelo ?? ""),
    especificacoesTecnicas: String(data.especificacoesTecnicas ?? ""),
    condicaoArmazenamento: String(data.condicaoArmazenamento ?? ""),
    classePerigo: String(data.classePerigo ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function normalizeProduct(data: Record<string, unknown>): Product {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    codigo: String(data.codigo ?? ""),
    codigoBarras: String(data.codigoBarras ?? ""),
    nome: String(data.nome ?? ""),
    descricao: String(data.descricao ?? ""),
    unidadeMedida: String(data.unidadeMedida ?? ""),
    tipoItem: String(data.tipoItem ?? "produto"),
    origem: data.origem == null ? "0" : String(data.origem),
    ncm: String(data.ncm ?? ""),
    cest: String(data.cest ?? ""),
    pesoBruto: data.pesoBruto == null ? "" : String(data.pesoBruto),
    pesoLiquido: data.pesoLiquido == null ? "" : String(data.pesoLiquido),
    situacao: String(data.situacao ?? "ativo"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(item: MaterialRecord) {
  return {
    produto: item.produtoId.trim() === "" ? null : Number(item.produtoId),
    tipoMaterial: item.tipoMaterial.trim() || null,
    categoria: item.categoria.trim() || null,
    subcategoria: item.subcategoria.trim() || null,
    marca: item.marca.trim() || null,
    modelo: item.modelo.trim() || null,
    especificacoesTecnicas: item.especificacoesTecnicas.trim() || null,
    condicaoArmazenamento: item.condicaoArmazenamento.trim() || null,
    classePerigo: item.classePerigo.trim() || null,
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

export default function MaterialsPage({ embedded = false }: MaterialsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<MaterialRecord[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<MaterialRecord | null>(null);
  const [draft, setDraft] = useState<MaterialRecord>(emptyMaterial);
  const canRead = canAccessResourceAction(session, materialsResource, "read");
  const canCreate = canAccessResourceAction(session, materialsResource, "create");
  const canUpdate = canAccessResourceAction(session, materialsResource, "update");
  const canDelete = canAccessResourceAction(session, materialsResource, "delete");
  const canReadProducts = canAccessResourceAction(session, productsResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadMaterials() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyMaterial });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("mm", "materiais");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeMaterial(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar os materiais."));
    } finally {
      setLoading(false);
    }
  }

  async function loadProducts() {
    if (!canReadProducts) {
      setProducts([]);
      return;
    }

    try {
      const response = await listResource("core", "produtos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeProduct(item as Record<string, unknown>),
          )
        : [];
      setProducts(nextItems);
    } catch {
      setProducts([]);
    }
  }

  useEffect(() => {
    void loadMaterials();
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
      [
        item.produtoId,
        item.tipoMaterial,
        item.categoria,
        item.subcategoria,
        item.marca,
        item.modelo,
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
    setDraft({ ...emptyMaterial });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: MaterialRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: MaterialRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar materiais."
          : "Seu perfil nao possui permissao para criar materiais.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("mm", "materiais", selected.id, payload)
        : await createResource("mm", "materiais", payload);

      const saved = normalizeMaterial(response.data as Record<string, unknown>);
      await loadMaterials();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Material atualizado com sucesso."
          : "Material criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o material."
            : "Nao foi possivel criar o material.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: MaterialRecord) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir materiais.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o material para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o material do produto "${item.produtoId || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("mm", "materiais", item.id);
      await loadMaterials();
      setSuccess("Material excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o material."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "materials-page materials-page--embedded" : "materials-page"}>
      {!embedded ? (
        <header className="materials-page__header">
          <div>
            <span className="materials-page__eyebrow">MM</span>
            <h2 className="materials-page__title">Materiais</h2>
            <p className="materials-page__subtitle">
              Estruture materiais, insumos e componentes vinculados ao catalogo
              de produtos, com classificacao tecnica e cuidados de armazenagem.
            </p>
          </div>

          <div className="materials-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por produto, tipo, categoria, marca ou modelo"
              className="materials-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="materials-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo material
            </button>
          </div>
        </header>
      ) : (
        <div className="materials-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por produto, tipo, categoria, marca ou modelo"
            className="materials-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="materials-page__toolbar-actions">
            <button
              type="button"
              className="materials-page__ghost"
              onClick={() => void loadMaterials()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="materials-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo material
            </button>
          </div>
        </div>
      )}

      {error ? <div className="materials-page__alert">{error}</div> : null}
      {success ? (
        <div className="materials-page__alert materials-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="materials-page__alert materials-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="materials-page__layout">
        <MaterialsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <MaterialForm
          value={draft}
          editing={!!selected}
          products={products}
          canReadProducts={canReadProducts}
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
