import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import ProductTable from "../../components/Core/ProductTable";
import ProductForm from "../../components/Core/ProductForm";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./products-page.css";

export type Product = {
  id?: number;
  codigo: string;
  codigoBarras: string;
  nome: string;
  descricao: string;
  tipoItem: string;
  unidadeMedida: string;
  ncm: string;
  cest: string;
  pesoBruto: string;
  pesoLiquido: string;
  origem: string;
  situacao: string;
  createdAt?: string;
};

type ProductsPageProps = {
  embedded?: boolean;
};

const emptyProduct: Product = {
  codigo: "",
  codigoBarras: "",
  nome: "",
  descricao: "",
  tipoItem: "produto",
  unidadeMedida: "",
  ncm: "",
  cest: "",
  pesoBruto: "",
  pesoLiquido: "",
  origem: "0",
  situacao: "ativo",
};

function normalizeProduct(data: Record<string, unknown>): Product {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    codigo: String(data.codigo ?? ""),
    codigoBarras: String(data.codigoBarras ?? ""),
    nome: String(data.nome ?? ""),
    descricao: String(data.descricao ?? ""),
    tipoItem: String(data.tipoItem ?? "produto"),
    unidadeMedida: String(data.unidadeMedida ?? ""),
    ncm: String(data.ncm ?? ""),
    cest: String(data.cest ?? ""),
    pesoBruto:
      data.pesoBruto == null ? "" : String(data.pesoBruto ?? ""),
    pesoLiquido:
      data.pesoLiquido == null ? "" : String(data.pesoLiquido ?? ""),
    origem: data.origem == null ? "0" : String(data.origem),
    situacao: String(data.situacao ?? "ativo"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(product: Product) {
  return {
    codigo: product.codigo.trim(),
    codigoBarras: product.codigoBarras.trim() || null,
    nome: product.nome.trim(),
    descricao: product.descricao.trim() || null,
    tipoItem: product.tipoItem.trim() || null,
    unidadeMedida: product.unidadeMedida.trim() || null,
    ncm: product.ncm.trim() || null,
    cest: product.cest.trim() || null,
    pesoBruto:
      product.pesoBruto.trim() === ""
        ? null
        : Number(product.pesoBruto.replace(",", ".")),
    pesoLiquido:
      product.pesoLiquido.trim() === ""
        ? null
        : Number(product.pesoLiquido.replace(",", ".")),
    origem:
      product.origem.trim() === "" ? null : Number(product.origem),
    situacao: product.situacao.trim() || null,
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

export default function ProductsPage({
  embedded = false,
}: ProductsPageProps) {
  const [items, setItems] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Product | null>(null);
  const [draft, setDraft] = useState<Product>(emptyProduct);

  async function loadProducts() {
    setLoading(true);
    setError(null);

    try {
      const response = await listResource("core", "produtos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeProduct(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar os produtos."));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadProducts();
  }, []);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.codigo,
        item.nome,
        item.codigoBarras,
        item.tipoItem,
        item.ncm,
      ]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    setSelected(null);
    setDraft({ ...emptyProduct });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Product) {
    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Product) {
    setDraft(next);
  }

  async function handleSave() {
    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("core", "produtos", selected.id, payload)
        : await createResource("core", "produtos", payload);

      const saved = normalizeProduct(response.data as Record<string, unknown>);
      await loadProducts();
      setSelected(saved);
      setDraft(saved);
      setSuccess(
        selected?.id
          ? "Produto atualizado com sucesso."
          : "Produto criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o produto."
            : "Nao foi possivel criar o produto.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Product) {
    if (!item.id) {
      setError("Nao foi possivel identificar o produto para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o produto "${item.nome}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("core", "produtos", item.id);
      await loadProducts();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyProduct });
      }

      setSuccess("Produto excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o produto."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "products-page products-page--embedded" : "products-page"
      }
    >
      {!embedded ? (
        <header className="products-page__header">
          <div>
            <span className="products-page__eyebrow">CORE</span>
            <h2 className="products-page__title">Produtos</h2>
            <p className="products-page__subtitle">
              Gerencie itens, servicos, insumos e embalagens do catalogo.
            </p>
          </div>

          <div className="products-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por nome, codigo, barras, NCM ou tipo"
              className="products-page__search"
            />
            <button
              type="button"
              className="products-page__button"
              onClick={handleCreateNew}
            >
              Novo produto
            </button>
          </div>
        </header>
      ) : (
        <div className="products-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por nome, codigo, barras, NCM ou tipo"
            className="products-page__search"
          />
          <div className="products-page__toolbar-actions">
            <button
              type="button"
              className="products-page__ghost"
              onClick={() => void loadProducts()}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="products-page__button"
              onClick={handleCreateNew}
            >
              Novo produto
            </button>
          </div>
        </div>
      )}

      {error ? <div className="products-page__alert">{error}</div> : null}
      {success ? (
        <div className="products-page__alert products-page__alert--success">
          {success}
        </div>
      ) : null}

      <div className="products-page__layout">
        <ProductTable
          items={filteredItems}
          loading={loading}
          selectedId={selected?.id}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ProductForm
          value={draft}
          editing={!!selected}
          saving={saving}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
