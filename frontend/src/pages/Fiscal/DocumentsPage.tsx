import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import DocumentsForm from "../../components/Fiscal/DocumentsForm";
import DocumentsTable from "../../components/Fiscal/DocumentsTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./documents-page.css";

export type ClientOption = {
  id: number;
  label: string;
};

export type ClientAccess = "idle" | "loaded" | "unavailable";
export type OrderAccess = "idle" | "loaded" | "unavailable";

export type DocumentEntry = {
  id?: number;
  tipoDocumento: string;
  numero: string;
  serie: string;
  chaveAcesso: string;
  dataEmissao: string;
  pedido: string;
  cliente: string;
  valorTotal: string;
  status: string;
  xml_file: string;
  createdAt?: string;
};

type DocumentsPageProps = {
  embedded?: boolean;
};

const emptyDocument: DocumentEntry = {
  tipoDocumento: "",
  numero: "",
  serie: "",
  chaveAcesso: "",
  dataEmissao: "",
  pedido: "",
  cliente: "",
  valorTotal: "",
  status: "digitado",
  xml_file: "",
};

function normalizeDocument(data: Record<string, unknown>): DocumentEntry {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    tipoDocumento: String(data.tipoDocumento ?? ""),
    numero: String(data.numero ?? ""),
    serie: String(data.serie ?? ""),
    chaveAcesso: String(data.chaveAcesso ?? ""),
    dataEmissao: String(data.dataEmissao ?? ""),
    pedido: data.pedido == null ? "" : String(data.pedido),
    cliente: data.cliente == null ? "" : String(data.cliente),
    valorTotal: data.valorTotal == null ? "" : String(data.valorTotal),
    status: String(data.status ?? "digitado"),
    xml_file: String(data.xml_file ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value);
}

function toNullableDecimal(value: string) {
  return value.trim() === "" ? null : Number(value.replace(",", "."));
}

function toRequestPayload(entry: DocumentEntry) {
  return {
    tipoDocumento: entry.tipoDocumento.trim() || null,
    numero: entry.numero.trim() || null,
    serie: entry.serie.trim() || null,
    chaveAcesso: entry.chaveAcesso.trim() || null,
    dataEmissao: entry.dataEmissao.trim() || null,
    pedido: toNullableNumber(entry.pedido),
    cliente: toNullableNumber(entry.cliente),
    valorTotal: toNullableDecimal(entry.valorTotal),
    status: entry.status.trim() || null,
    xml_file: entry.xml_file.trim() || null,
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

export default function DocumentsPage({
  embedded = false,
}: DocumentsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<DocumentEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<DocumentEntry | null>(null);
  const [draft, setDraft] = useState<DocumentEntry>(emptyDocument);
  const [clients, setClients] = useState<ClientOption[]>([]);
  const [clientAccess, setClientAccess] = useState<ClientAccess>("idle");
  const [orderAccess] = useState<OrderAccess>("unavailable");
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("fiscal:documentos:read");
  const canCreate =
    isMasterScope || permissionSet.has("fiscal:documentos:create");
  const canUpdate =
    isMasterScope || permissionSet.has("fiscal:documentos:update");
  const canDelete =
    isMasterScope || permissionSet.has("fiscal:documentos:delete");
  const canReadClients =
    isMasterScope || permissionSet.has("core:parceiros:read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadDocuments = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyDocument });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("fiscal", "documentos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeDocument(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível carregar os documentos."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadClients = useCallback(async () => {
    if (!canReadClients) {
      setClients([]);
      setClientAccess("unavailable");
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
              label: `${String(item.nome ?? "Cliente")} (#${String(item.id)})`,
            }))
        : [];
      setClients(nextItems);
      setClientAccess("loaded");
    } catch {
      setClients([]);
      setClientAccess("unavailable");
    }
  }, [canReadClients]);

  useEffect(() => {
    void loadDocuments();
  }, [loadDocuments]);

  useEffect(() => {
    void loadClients();
  }, [loadClients]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.tipoDocumento,
        item.numero,
        item.serie,
        item.status,
        item.chaveAcesso,
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
    setDraft({ ...emptyDocument });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: DocumentEntry) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: DocumentEntry) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar documentos."
          : "Seu perfil não possui permissão para criar documentos.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("fiscal", "documentos", selected.id, payload)
        : await createResource("fiscal", "documentos", payload);

      const saved = normalizeDocument(response.data as Record<string, unknown>);
      await loadDocuments();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Documento atualizado com sucesso."
          : "Documento criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar o documento."
            : "Não foi possível criar o documento.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: DocumentEntry) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir documentos.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar o documento para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o documento "${item.numero || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("fiscal", "documentos", item.id);
      await loadDocuments();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyDocument });
      }

      setSuccess("Documento excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível excluir o documento."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "documents-page documents-page--embedded" : "documents-page"
      }
    >
      {!embedded ? (
        <header className="documents-page__header">
          <div>
            <span className="documents-page__eyebrow">FISCAL</span>
            <h2 className="documents-page__title">Documentos</h2>
            <p className="documents-page__subtitle">
              Gerencie documentos fiscais com status, chave de acesso e
              relacionamento comercial.
            </p>
          </div>

          <div className="documents-page__toolbar-actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por tipo, numero, serie, status ou chave"
              className="documents-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="documents-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo documento
            </button>
          </div>
        </header>
      ) : (
        <div className="documents-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por tipo, numero, serie, status ou chave"
            className="documents-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="documents-page__toolbar-actions">
            <button
              type="button"
              className="documents-page__ghost"
              onClick={() => void loadDocuments()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="documents-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo documento
            </button>
          </div>
        </div>
      )}

      {error ? <div className="documents-page__alert">{error}</div> : null}
      {success ? (
        <div className="documents-page__alert documents-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="documents-page__alert documents-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="documents-page__layout">
        <DocumentsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          clients={clients}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <DocumentsForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          clients={clients}
          clientAccess={clientAccess}
          orderAccess={orderAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
