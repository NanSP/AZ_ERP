import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ClientForm from "../../components/Sd/ClientForm";
import ClientsTable from "../../components/Sd/ClientsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { Partner } from "../Core/PartnersPage";
import "./clients-page.css";

export type ClientRecord = {
  id?: number;
  parceiroId: string;
  classificacao: string;
  origem: string;
  website: string;
  faturamentoAnual: string;
  numeroFuncionarios: string;
  createdAt?: string;
};

type ClientsPageProps = {
  embedded?: boolean;
};

const clientsResource = {
  schema: "sd",
  entity: "clientes",
  label: "Clientes",
  description: "Base comercial de clientes.",
} as const;

const partnersResource = {
  schema: "core",
  entity: "parceiros",
  label: "Parceiros",
  description: "Clientes, fornecedores e parceiros de negocio.",
} as const;

const emptyClient: ClientRecord = {
  parceiroId: "",
  classificacao: "lead",
  origem: "",
  website: "",
  faturamentoAnual: "",
  numeroFuncionarios: "",
};

function normalizeClient(data: Record<string, unknown>): ClientRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    parceiroId: data.parceiro == null ? "" : String(data.parceiro),
    classificacao: String(data.classificacao ?? "lead"),
    origem: String(data.origem ?? ""),
    website: String(data.website ?? ""),
    faturamentoAnual:
      data.faturamentoAnual == null ? "" : String(data.faturamentoAnual),
    numeroFuncionarios:
      data.numeroFuncionarios == null ? "" : String(data.numeroFuncionarios),
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

function toRequestPayload(item: ClientRecord) {
  function toNumberOrNull(value: string) {
    const normalized = value.trim();

    if (normalized === "") {
      return null;
    }

    return Number(normalized.replace(",", "."));
  }

  return {
    parceiro: item.parceiroId.trim() === "" ? null : Number(item.parceiroId),
    classificacao: item.classificacao.trim() || null,
    origem: item.origem.trim() || null,
    website: item.website.trim() || null,
    faturamentoAnual: toNumberOrNull(item.faturamentoAnual),
    numeroFuncionarios:
      item.numeroFuncionarios.trim() === ""
        ? null
        : Number(item.numeroFuncionarios),
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

export default function ClientsPage({ embedded = false }: ClientsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<ClientRecord[]>([]);
  const [partners, setPartners] = useState<Partner[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<ClientRecord | null>(null);
  const [draft, setDraft] = useState<ClientRecord>(emptyClient);
  const canRead = canAccessResourceAction(session, clientsResource, "read");
  const canCreate = canAccessResourceAction(session, clientsResource, "create");
  const canUpdate = canAccessResourceAction(session, clientsResource, "update");
  const canDelete = canAccessResourceAction(session, clientsResource, "delete");
  const canReadPartners = canAccessResourceAction(
    session,
    partnersResource,
    "read",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadClients() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyClient });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("sd", "clientes");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeClient(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar os clientes."));
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
    void loadClients();
  }, [canRead]);

  useEffect(() => {
    void loadPartners();
  }, [canReadPartners]);

  const partnerOptions = useMemo(() => {
    const selectedPartnerId = selected?.parceiroId ?? "";
    const usedPartnerIds = new Set(
      items
        .filter((item) => item.parceiroId !== selectedPartnerId)
        .map((item) => item.parceiroId),
    );

    return partners.filter(
      (partner) =>
        partner.situacao === "ativo" &&
        !usedPartnerIds.has(String(partner.id ?? "")),
    );
  }, [items, partners, selected?.parceiroId]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.parceiroId,
        item.classificacao,
        item.origem,
        item.website,
        item.faturamentoAnual,
        item.numeroFuncionarios,
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
    setDraft({ ...emptyClient });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: ClientRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: ClientRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar clientes."
          : "Seu perfil nao possui permissao para criar clientes.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("sd", "clientes", selected.id, payload)
        : await createResource("sd", "clientes", payload);

      const saved = normalizeClient(response.data as Record<string, unknown>);
      await loadClients();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Cliente atualizado com sucesso."
          : "Cliente criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o cliente."
            : "Nao foi possivel criar o cliente.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: ClientRecord) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir clientes.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o cliente para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o cliente "${item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sd", "clientes", item.id);
      await loadClients();
      setSuccess("Cliente excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o cliente."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "clients-page clients-page--embedded" : "clients-page"}>
      {!embedded ? (
        <header className="clients-page__header">
          <div>
            <span className="clients-page__eyebrow">SD</span>
            <h2 className="clients-page__title">Clientes</h2>
            <p className="clients-page__subtitle">
              Estruture a base comercial a partir dos parceiros, classificando
              leads, prospects e clientes ativos para o funil de vendas.
            </p>
          </div>

          <div className="clients-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por parceiro, classificacao, origem, website ou porte"
              className="clients-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="clients-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo cliente
            </button>
          </div>
        </header>
      ) : (
        <div className="clients-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por parceiro, classificacao, origem, website ou porte"
            className="clients-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="clients-page__toolbar-actions">
            <button
              type="button"
              className="clients-page__ghost"
              onClick={() => void loadClients()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="clients-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo cliente
            </button>
          </div>
        </div>
      )}

      {error ? <div className="clients-page__alert">{error}</div> : null}
      {success ? (
        <div className="clients-page__alert clients-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="clients-page__alert clients-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="clients-page__layout">
        <ClientsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ClientForm
          value={draft}
          editing={!!selected}
          partners={partnerOptions}
          canReadPartners={canReadPartners}
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
