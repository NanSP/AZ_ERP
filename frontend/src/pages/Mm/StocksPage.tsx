import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import StockForm from "../../components/Mm/StockForm";
import StocksTable from "../../components/Mm/StocksTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { Company } from "../Core/CompaniesPage";
import type { Product } from "../Core/ProductsPage";
import "./stocks-page.css";

export type StockRecord = {
  id?: number;
  produtoId: string;
  empresaId: string;
  localizacao: string;
  lote: string;
  quantidade: string;
  quantidadeMinima: string;
  quantidadeMaxima: string;
  valorUnitario: string;
  dataValidade: string;
  createdAt?: string;
};

type StocksPageProps = {
  embedded?: boolean;
};

const stocksResource = {
  schema: "mm",
  entity: "estoques",
  label: "Estoques",
  description: "Posicoes e controle de estoque.",
} as const;

const productsResource = {
  schema: "core",
  entity: "produtos",
  label: "Produtos",
  description: "Catalogo de produtos e itens.",
} as const;

const companiesResource = {
  schema: "core",
  entity: "empresas",
  label: "Empresas",
  description: "Cadastro das empresas da operacao.",
} as const;

const emptyStock: StockRecord = {
  produtoId: "",
  empresaId: "",
  localizacao: "",
  lote: "",
  quantidade: "0",
  quantidadeMinima: "",
  quantidadeMaxima: "",
  valorUnitario: "",
  dataValidade: "",
};

function normalizeStock(data: Record<string, unknown>): StockRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    produtoId: data.produto == null ? "" : String(data.produto),
    empresaId: data.empresa == null ? "" : String(data.empresa),
    localizacao: String(data.localizacao ?? ""),
    lote: String(data.lote ?? ""),
    quantidade: data.quantidade == null ? "0" : String(data.quantidade),
    quantidadeMinima:
      data.quantidadeMinima == null ? "" : String(data.quantidadeMinima),
    quantidadeMaxima:
      data.quantidadeMaxima == null ? "" : String(data.quantidadeMaxima),
    valorUnitario:
      data.valorUnitario == null ? "" : String(data.valorUnitario),
    dataValidade: data.dataValidade == null ? "" : String(data.dataValidade),
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
    tipoItem: String(data.tipoItem ?? "produto"),
    unidadeMedida: String(data.unidadeMedida ?? ""),
    ncm: String(data.ncm ?? ""),
    cest: String(data.cest ?? ""),
    pesoBruto: data.pesoBruto == null ? "" : String(data.pesoBruto),
    pesoLiquido: data.pesoLiquido == null ? "" : String(data.pesoLiquido),
    origem: data.origem == null ? "0" : String(data.origem),
    situacao: String(data.situacao ?? "ativo"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function normalizeCompany(data: Record<string, unknown>): Company {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    codigo: String(data.codigo ?? ""),
    razaoSocial: String(data.razaoSocial ?? ""),
    nomeFantasia: String(data.nomeFantasia ?? ""),
    cnpj: String(data.cnpj ?? ""),
    inscricaoEstadual: String(data.inscricaoEstadual ?? ""),
    inscricaoMunicipal: String(data.inscricaoMunicipal ?? ""),
    regimeTributario: String(data.regimeTributario ?? ""),
    dataFundacao: String(data.dataFundacao ?? ""),
    situacao: String(data.situacao ?? "ativo"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(item: StockRecord) {
  function toNumberOrNull(value: string) {
    const normalized = value.trim();
    if (normalized === "") {
      return null;
    }

    return Number(normalized.replace(",", "."));
  }

  return {
    produto: item.produtoId.trim() === "" ? null : Number(item.produtoId),
    empresa: item.empresaId.trim() === "" ? null : Number(item.empresaId),
    localizacao: item.localizacao.trim() || null,
    lote: item.lote.trim() || null,
    quantidade: toNumberOrNull(item.quantidade),
    quantidadeMinima: toNumberOrNull(item.quantidadeMinima),
    quantidadeMaxima: toNumberOrNull(item.quantidadeMaxima),
    valorUnitario: toNumberOrNull(item.valorUnitario),
    dataValidade: item.dataValidade.trim() || null,
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

export default function StocksPage({ embedded = false }: StocksPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<StockRecord[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<StockRecord | null>(null);
  const [draft, setDraft] = useState<StockRecord>(emptyStock);
  const canRead = canAccessResourceAction(session, stocksResource, "read");
  const canCreate = canAccessResourceAction(session, stocksResource, "create");
  const canUpdate = canAccessResourceAction(session, stocksResource, "update");
  const canDelete = canAccessResourceAction(session, stocksResource, "delete");
  const canReadProducts = canAccessResourceAction(session, productsResource, "read");
  const canReadCompanies = canAccessResourceAction(session, companiesResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadStocks() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyStock });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("mm", "estoques");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeStock(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar os estoques."));
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

  async function loadCompanies() {
    if (!canReadCompanies) {
      setCompanies([]);
      return;
    }

    try {
      const response = await listResource("core", "empresas");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeCompany(item as Record<string, unknown>),
          )
        : [];
      setCompanies(nextItems);
    } catch {
      setCompanies([]);
    }
  }

  useEffect(() => {
    void loadStocks();
  }, [canRead]);

  useEffect(() => {
    void loadProducts();
  }, [canReadProducts]);

  useEffect(() => {
    void loadCompanies();
  }, [canReadCompanies]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.produtoId,
        item.empresaId,
        item.localizacao,
        item.lote,
        item.dataValidade,
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
    setDraft({ ...emptyStock });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: StockRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: StockRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar estoques."
          : "Seu perfil nao possui permissao para criar estoques.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("mm", "estoques", selected.id, payload)
        : await createResource("mm", "estoques", payload);

      const saved = normalizeStock(response.data as Record<string, unknown>);
      await loadStocks();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Estoque atualizado com sucesso."
          : "Estoque criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o estoque."
            : "Nao foi possivel criar o estoque.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: StockRecord) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir estoques.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o estoque para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o estoque do produto "${item.produtoId || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("mm", "estoques", item.id);
      await loadStocks();
      setSuccess("Estoque excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o estoque."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "stocks-page stocks-page--embedded" : "stocks-page"}>
      {!embedded ? (
        <header className="stocks-page__header">
          <div>
            <span className="stocks-page__eyebrow">MM</span>
            <h2 className="stocks-page__title">Estoques</h2>
            <p className="stocks-page__subtitle">
              Gerencie posicoes de estoque por produto, empresa, lote e localizacao,
              com limites, valor unitario e validade.
            </p>
          </div>

          <div className="stocks-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por produto, empresa, localizacao, lote ou validade"
              className="stocks-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="stocks-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo estoque
            </button>
          </div>
        </header>
      ) : (
        <div className="stocks-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por produto, empresa, localizacao, lote ou validade"
            className="stocks-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="stocks-page__toolbar-actions">
            <button
              type="button"
              className="stocks-page__ghost"
              onClick={() => void loadStocks()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="stocks-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo estoque
            </button>
          </div>
        </div>
      )}

      {error ? <div className="stocks-page__alert">{error}</div> : null}
      {success ? (
        <div className="stocks-page__alert stocks-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="stocks-page__alert stocks-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="stocks-page__layout">
        <StocksTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <StockForm
          value={draft}
          editing={!!selected}
          products={products}
          companies={companies}
          canReadProducts={canReadProducts}
          canReadCompanies={canReadCompanies}
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
