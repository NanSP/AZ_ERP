import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import ContactTable from "../../components/Core/ContactTable";
import ContactForm from "../../components/Core/ContactForm";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./contacts-page.css";

export type Contact = {
  id?: number;
  entidadeTipo: string;
  entidadeId: string;
  tipoContato: string;
  valor: string;
  principal: boolean;
  observacao: string;
  createdAt?: string;
};

export type RelatedEntityOption = {
  value: string;
  label: string;
};

type ContactsPageProps = {
  embedded?: boolean;
};

const emptyContact: Contact = {
  entidadeTipo: "empresa",
  entidadeId: "",
  tipoContato: "email",
  valor: "",
  principal: true,
  observacao: "",
};

function normalizeContact(data: Record<string, unknown>): Contact {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    entidadeTipo: String(data.entidadeTipo ?? "empresa"),
    entidadeId:
      data.entidadeId == null ? "" : String(data.entidadeId),
    tipoContato: String(data.tipoContato ?? "email"),
    valor: String(data.valor ?? ""),
    principal: Boolean(data.principal),
    observacao: String(data.observacao ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(contact: Contact) {
  return {
    entidadeTipo: contact.entidadeTipo.trim() || null,
    entidadeId:
      contact.entidadeId.trim() === ""
        ? null
        : Number(contact.entidadeId),
    tipoContato: contact.tipoContato.trim() || null,
    valor: contact.valor.trim(),
    principal: contact.principal,
    observacao: contact.observacao.trim() || null,
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

function normalizeEntityOptions(
  items: unknown[],
  tipo: "empresa" | "parceiro",
): RelatedEntityOption[] {
  return items
    .map((item) => item as Record<string, unknown>)
    .map((item) => {
      const id = item.id == null ? "" : String(item.id);
      const label =
        tipo === "empresa"
          ? String(item.razaoSocial ?? item.nomeFantasia ?? item.codigo ?? id)
          : String(item.nome ?? item.nomeFantasia ?? item.codigo ?? id);

      return {
        value: id,
        label: id ? `${label} (#${id})` : label,
      };
    })
    .filter((item) => item.value !== "");
}

export default function ContactsPage({
  embedded = false,
}: ContactsPageProps) {
  const [items, setItems] = useState<Contact[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Contact | null>(null);
  const [draft, setDraft] = useState<Contact>(emptyContact);
  const [empresaOptions, setEmpresaOptions] = useState<RelatedEntityOption[]>(
    [],
  );
  const [parceiroOptions, setParceiroOptions] = useState<RelatedEntityOption[]>(
    [],
  );
  const isBusy = loading || saving;

  async function loadContacts() {
    setLoading(true);
    setError(null);

    try {
      const response = await listResource("core", "contatos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeContact(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar os contatos."));
    } finally {
      setLoading(false);
    }
  }

  async function loadRelatedEntities() {
    try {
      const [empresasResponse, parceirosResponse] = await Promise.all([
        listResource("core", "empresas"),
        listResource("core", "parceiros"),
      ]);

      const empresas = Array.isArray(empresasResponse.data)
        ? normalizeEntityOptions(empresasResponse.data, "empresa")
        : [];
      const parceiros = Array.isArray(parceirosResponse.data)
        ? normalizeEntityOptions(parceirosResponse.data, "parceiro")
        : [];

      setEmpresaOptions(empresas);
      setParceiroOptions(parceiros);
    } catch {
      // Mantem o formulario funcional com input manual de ID.
    }
  }

  useEffect(() => {
    void loadContacts();
    void loadRelatedEntities();
  }, []);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.entidadeTipo,
        item.entidadeId,
        item.tipoContato,
        item.valor,
        item.observacao,
      ]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    setSelected(null);
    setDraft({ ...emptyContact });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Contact) {
    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Contact) {
    setDraft(next);
  }

  async function handleSave() {
    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("core", "contatos", selected.id, payload)
        : await createResource("core", "contatos", payload);

      const saved = normalizeContact(response.data as Record<string, unknown>);
      await loadContacts();
      setSelected(saved);
      setDraft(saved);
      setSuccess(
        selected?.id
          ? "Contato atualizado com sucesso."
          : "Contato criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o contato."
            : "Nao foi possivel criar o contato.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Contact) {
    if (!item.id) {
      setError("Nao foi possivel identificar o contato para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o contato "${item.valor}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("core", "contatos", item.id);
      await loadContacts();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyContact });
      }

      setSuccess("Contato excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o contato."));
    } finally {
      setSaving(false);
    }
  }

  const relatedEntityOptions =
    draft.entidadeTipo === "empresa" ? empresaOptions : parceiroOptions;

  return (
    <div
      className={embedded ? "contacts-page contacts-page--embedded" : "contacts-page"}
    >
      {!embedded ? (
        <header className="contacts-page__header">
          <div>
            <span className="contacts-page__eyebrow">CORE</span>
            <h2 className="contacts-page__title">Contatos</h2>
            <p className="contacts-page__subtitle">
              Gerencie canais de contato de empresas, parceiros e colaboradores.
            </p>
          </div>

          <div className="contacts-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por entidade, tipo, valor ou observacao"
              className="contacts-page__search"
              disabled={isBusy}
            />
            <button
              type="button"
              className="contacts-page__button"
              onClick={handleCreateNew}
              disabled={isBusy}
            >
              Novo contato
            </button>
          </div>
        </header>
      ) : (
        <div className="contacts-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por entidade, tipo, valor ou observacao"
            className="contacts-page__search"
            disabled={isBusy}
          />
          <div className="contacts-page__toolbar-actions">
            <button
              type="button"
              className="contacts-page__ghost"
              onClick={() => void loadContacts()}
              disabled={isBusy}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="contacts-page__button"
              onClick={handleCreateNew}
              disabled={isBusy}
            >
              Novo contato
            </button>
          </div>
        </div>
      )}

      {error ? <div className="contacts-page__alert">{error}</div> : null}
      {success ? (
        <div className="contacts-page__alert contacts-page__alert--success">
          {success}
        </div>
      ) : null}

      <div className="contacts-page__layout">
        <ContactTable
          items={filteredItems}
          loading={loading}
          selectedId={selected?.id}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ContactForm
          value={draft}
          editing={!!selected}
          saving={saving}
          relatedEntityOptions={relatedEntityOptions}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
