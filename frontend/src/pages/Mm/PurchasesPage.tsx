import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import PurchaseForm from "../../components/Mm/PurchaseForm";
import PurchasesTable from "../../components/Mm/PurchasesTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { Partner } from "../Core/PartnersPage";
import "./purchases-page.css";

export type PurchaseRecord = {
  id?: number;
  fornecedorId: string;
  dataPedido: string;
  dataPrevistaEntrega: string;
  dataEntrega: string;
  valorTotal: string;
  condicoesPagamento: string;
  status: string;
  observacoes: string;
  createdAt?: string;
};

type PurchasesPageProps = {
  embedded?: boolean;
};

const purchasesResource = {
  schema: "mm",
  entity: "compras",
  label: "Compras",
  description: "Processos de compra e aquisicao.",
} as const;

const partnersResource = {
  schema: "core",
  entity: "parceiros",
  label: "Parceiros",
  description: "Clientes, fornecedores e parceiros de negocio.",
} as const;

const emptyPurchase: PurchaseRecord = {
  fornecedorId: "",
  dataPedido: "",
  dataPrevistaEntrega: "",
  dataEntrega: "",
  valorTotal: "",
  condicoesPagamento: "",
  status: "aberto",
  observacoes: "",
};

function normalizePurchase(data: Record<string, unknown>): PurchaseRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    fornecedorId: data.fornecedorId == null ? "" : String(data.fornecedorId),
    dataPedido: data.dataPedido == null ? "" : String(data.dataPedido),
    dataPrevistaEntrega:
      data.dataPrevistaEntrega == null ? "" : String(data.dataPrevistaEntrega),
    dataEntrega: data.dataEntrega == null ? "" : String(data.dataEntrega),
    valorTotal: data.valorTotal == null ? "" : String(data.valorTotal),
    condicoesPagamento: String(data.condicoesPagamento ?? ""),
    status: String(data.status ?? "aberto"),
    observacoes: String(data.observacoes ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function normalizePartner(data: Record<string, unknown>): Partner {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    tipoParceiro: String(data.tipoParceiro ?? "cliente"),
    codigo: String(data.codigo ?? ""),
    nome: String(data.nome ?? ""),
    nomeFantasia: String(data.nomeFantasia ?? ""),
    documento: String(data.documento ?? ""),
    tipoPessoa: String(data.tipoPessoa ?? ""),
    situacao: String(data.situacao ?? "ativo"),
    limiteCredito:
      data.limiteCredito == null ? "" : String(data.limiteCredito ?? ""),
    diasPrazo: data.diasPrazo == null ? "" : String(data.diasPrazo ?? ""),
    observacoes: String(data.observacoes ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(item: PurchaseRecord) {
  const normalizedValue = item.valorTotal.trim();

  return {
    fornecedor: item.fornecedorId.trim() === "" ? null : Number(item.fornecedorId),
    dataPedido: item.dataPedido.trim() || null,
    dataPrevistaEntrega: item.dataPrevistaEntrega.trim() || null,
    dataEntrega: item.dataEntrega.trim() || null,
    valorTotal:
      normalizedValue === "" ? null : Number(normalizedValue.replace(",", ".")),
    condicoesPagamento: item.condicoesPagamento.trim() || null,
    status: item.status.trim() || null,
    observacoes: item.observacoes.trim() || null,
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

export default function PurchasesPage({ embedded = false }: PurchasesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<PurchaseRecord[]>([]);
  const [partners, setPartners] = useState<Partner[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<PurchaseRecord | null>(null);
  const [draft, setDraft] = useState<PurchaseRecord>(emptyPurchase);
  const canRead = canAccessResourceAction(session, purchasesResource, "read");
  const canCreate = canAccessResourceAction(session, purchasesResource, "create");
  const canUpdate = canAccessResourceAction(session, purchasesResource, "update");
  const canDelete = canAccessResourceAction(session, purchasesResource, "delete");
  const canReadPartners = canAccessResourceAction(session, partnersResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadPurchases() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyPurchase });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("mm", "compras");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizePurchase(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar as compras."));
    } finally {
      setLoading(false);
    }
  }

  async function loadPartners() {
    if (!canReadPartners) {
      setPartners([]);
      return;
    }

    try {
      const response = await listResource("core", "parceiros");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizePartner(item as Record<string, unknown>),
          )
        : [];
      setPartners(nextItems);
    } catch {
      setPartners([]);
    }
  }

  useEffect(() => {
    void loadPurchases();
  }, [canRead]);

  useEffect(() => {
    void loadPartners();
  }, [canReadPartners]);

  const supplierOptions = useMemo(
    () =>
      partners.filter(
        (partner) =>
          partner.tipoParceiro === "fornecedor" && partner.situacao === "ativo",
      ),
    [partners],
  );

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.fornecedorId,
        item.status,
        item.condicoesPagamento,
        item.observacoes,
        item.dataPedido,
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
    setDraft({ ...emptyPurchase });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: PurchaseRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: PurchaseRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar compras."
          : "Seu perfil nao possui permissao para criar compras.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("mm", "compras", selected.id, payload)
        : await createResource("mm", "compras", payload);

      const saved = normalizePurchase(response.data as Record<string, unknown>);
      await loadPurchases();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Compra atualizada com sucesso."
          : "Compra criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar a compra."
            : "Nao foi possivel criar a compra.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: PurchaseRecord) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir compras.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar a compra para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a compra "${item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("mm", "compras", item.id);
      await loadPurchases();
      setSuccess("Compra excluida com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir a compra."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "purchases-page purchases-page--embedded" : "purchases-page"}>
      {!embedded ? (
        <header className="purchases-page__header">
          <div>
            <span className="purchases-page__eyebrow">MM</span>
            <h2 className="purchases-page__title">Compras</h2>
            <p className="purchases-page__subtitle">
              Registre pedidos de compra, acompanhe status de recebimento e
              organize condicoes comerciais com fornecedores.
            </p>
          </div>

          <div className="purchases-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por fornecedor, status, pagamento, observacoes ou data"
              className="purchases-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="purchases-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova compra
            </button>
          </div>
        </header>
      ) : (
        <div className="purchases-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por fornecedor, status, pagamento, observacoes ou data"
            className="purchases-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="purchases-page__toolbar-actions">
            <button
              type="button"
              className="purchases-page__ghost"
              onClick={() => void loadPurchases()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="purchases-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova compra
            </button>
          </div>
        </div>
      )}

      {error ? <div className="purchases-page__alert">{error}</div> : null}
      {success ? (
        <div className="purchases-page__alert purchases-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="purchases-page__alert purchases-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="purchases-page__layout">
        <PurchasesTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <PurchaseForm
          value={draft}
          editing={!!selected}
          suppliers={supplierOptions}
          canReadSuppliers={canReadPartners}
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
