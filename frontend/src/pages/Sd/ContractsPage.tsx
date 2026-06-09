import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ContractForm from "../../components/Sd/ContractForm";
import ContractsTable from "../../components/Sd/ContractsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { ClientRecord } from "./ClientsPage";
import "./contracts-page.css";

export type ContractRecord = {
  id?: number;
  clienteId: string;
  numeroContrato: string;
  objeto: string;
  valorTotal: string;
  dataInicio: string;
  dataFim: string;
  status: string;
  createdAt?: string;
};

type ContractsPageProps = {
  embedded?: boolean;
};

const contractsResource = {
  schema: "sd",
  entity: "contratos",
  label: "Contratos",
  description: "Contratos e acordos comerciais.",
} as const;

const clientsResource = {
  schema: "sd",
  entity: "clientes",
  label: "Clientes",
  description: "Base comercial de clientes.",
} as const;

const emptyContract: ContractRecord = {
  clienteId: "",
  numeroContrato: "",
  objeto: "",
  valorTotal: "",
  dataInicio: "",
  dataFim: "",
  status: "vigente",
};

function normalizeContract(data: Record<string, unknown>): ContractRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    clienteId: data.cliente == null ? "" : String(data.cliente),
    numeroContrato: String(data.numeroContrato ?? ""),
    objeto: String(data.objeto ?? ""),
    valorTotal: data.valorTotal == null ? "" : String(data.valorTotal),
    dataInicio: data.dataInicio == null ? "" : String(data.dataInicio),
    dataFim: data.dataFim == null ? "" : String(data.dataFim),
    status: String(data.status ?? "vigente"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

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

function toRequestPayload(item: ContractRecord) {
  const normalizedValue = item.valorTotal.trim();

  return {
    cliente: item.clienteId.trim() === "" ? null : Number(item.clienteId),
    numeroContrato: item.numeroContrato.trim() || null,
    objeto: item.objeto.trim() || null,
    valorTotal:
      normalizedValue === "" ? null : Number(normalizedValue.replace(",", ".")),
    dataInicio: item.dataInicio.trim() || null,
    dataFim: item.dataFim.trim() || null,
    status: item.status.trim() || null,
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

export default function ContractsPage({
  embedded = false,
}: ContractsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<ContractRecord[]>([]);
  const [clients, setClients] = useState<ClientRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<ContractRecord | null>(null);
  const [draft, setDraft] = useState<ContractRecord>(emptyContract);
  const canRead = canAccessResourceAction(session, contractsResource, "read");
  const canCreate = canAccessResourceAction(
    session,
    contractsResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    contractsResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    contractsResource,
    "delete",
  );
  const canReadClients = canAccessResourceAction(
    session,
    clientsResource,
    "read",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadContracts = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyContract });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("sd", "contratos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeContract(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possivel carregar os contratos."));
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadClients = useCallback(async () => {
    if (!canReadClients) {
      setClients([]);
      return;
    }

    try {
      const response = await listResource("sd", "clientes");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeClient(item as Record<string, unknown>),
          )
        : [];
      setClients(nextItems);
    } catch {
      setClients([]);
    }
  }, [canReadClients]);

  useEffect(() => {
    void loadContracts();
  }, [loadContracts]);

  useEffect(() => {
    void loadClients();
  }, [loadClients]);

  const clientOptions = useMemo(() => clients, [clients]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.clienteId,
        item.numeroContrato,
        item.objeto,
        item.status,
        item.valorTotal,
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
    setDraft({ ...emptyContract });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: ContractRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: ContractRecord) {
    const nextDraft = { ...next };

    if (nextDraft.status === "vigente") {
      nextDraft.dataFim = "";
    }

    setDraft(nextDraft);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar contratos."
          : "Seu perfil não possui permissão para criar contratos.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("sd", "contratos", selected.id, payload)
        : await createResource("sd", "contratos", payload);

      const saved = normalizeContract(response.data as Record<string, unknown>);
      await loadContracts();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Contrato atualizado com sucesso."
          : "Contrato criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar o contrato."
            : "Não foi possível criar o contrato.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: ContractRecord) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir contratos.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar o contrato para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o contrato "${item.numeroContrato || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sd", "contratos", item.id);
      await loadContracts();
      setSuccess("Contrato excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível excluir o contrato."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "contracts-page contracts-page--embedded" : "contracts-page"
      }
    >
      {!embedded ? (
        <header className="contracts-page__header">
          <div>
            <span className="contracts-page__eyebrow">SD</span>
            <h2 className="contracts-page__title">Contratos</h2>
            <p className="contracts-page__subtitle">
              Formalize acordos comerciais a partir dos clientes do CRM, com
              numero de contrato, vigência, valor total e status operacional.
            </p>
          </div>

          <div className="contracts-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por cliente, numero, objeto, status ou valor"
              className="contracts-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="contracts-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo contrato
            </button>
          </div>
        </header>
      ) : (
        <div className="contracts-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por cliente, numero, objeto, status ou valor"
            className="contracts-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="contracts-page__toolbar-actions">
            <button
              type="button"
              className="contracts-page__ghost"
              onClick={() => void loadContracts()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="contracts-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo contrato
            </button>
          </div>
        </div>
      )}

      {error ? <div className="contracts-page__alert">{error}</div> : null}
      {success ? (
        <div className="contracts-page__alert contracts-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="contracts-page__alert contracts-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="contracts-page__layout">
        <ContractsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ContractForm
          value={draft}
          editing={!!selected}
          clients={clientOptions}
          canReadClients={canReadClients}
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
