import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import PartnerTable from "../../components/Core/PartnerTable";
import PartnerForm from "../../components/Core/PartnerForm";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./partners-page.css";

export type Partner = {
  id?: number;
  tipoParceiro: string;
  codigo: string;
  nome: string;
  nomeFantasia: string;
  documento: string;
  tipoPessoa: string;
  situacao: string;
  limiteCredito: string;
  diasPrazo: string;
  observacoes: string;
  createdAt?: string;
};

type PartnersPageProps = {
  embedded?: boolean;
};

const emptyPartner: Partner = {
  tipoParceiro: "cliente",
  codigo: "",
  nome: "",
  nomeFantasia: "",
  documento: "",
  tipoPessoa: "",
  situacao: "ativo",
  limiteCredito: "",
  diasPrazo: "",
  observacoes: "",
};

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

function toRequestPayload(partner: Partner) {
  return {
    tipoParceiro: partner.tipoParceiro,
    codigo: partner.codigo.trim() || null,
    nome: partner.nome.trim(),
    nomeFantasia: partner.nomeFantasia.trim() || null,
    documento: partner.documento.trim() || null,
    tipoPessoa: partner.tipoPessoa.trim() || null,
    situacao: partner.situacao,
    limiteCredito:
      partner.limiteCredito.trim() === ""
        ? null
        : Number(partner.limiteCredito.replace(",", ".")),
    diasPrazo:
      partner.diasPrazo.trim() === "" ? null : Number(partner.diasPrazo),
    observacoes: partner.observacoes.trim() || null,
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

export default function PartnersPage({
  embedded = false,
}: PartnersPageProps) {
  const [items, setItems] = useState<Partner[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Partner | null>(null);
  const [draft, setDraft] = useState<Partner>(emptyPartner);

  async function loadPartners() {
    setLoading(true);
    setError(null);

    try {
      const response = await listResource("core", "parceiros");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizePartner(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar os parceiros."),
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadPartners();
  }, []);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) return items;

    return items.filter((item) =>
      [
        item.nome,
        item.nomeFantasia,
        item.documento,
        item.codigo,
        item.tipoParceiro,
      ]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    setSelected(null);
    setDraft({ ...emptyPartner });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Partner) {
    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Partner) {
    setDraft(next);
  }

  async function handleSave() {
    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("core", "parceiros", selected.id, payload)
        : await createResource("core", "parceiros", payload);

      const saved = normalizePartner(response.data as Record<string, unknown>);
      await loadPartners();
      setSelected(saved);
      setDraft(saved);
      setSuccess(
        selected?.id
          ? "Parceiro atualizado com sucesso."
          : "Parceiro criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o parceiro."
            : "Nao foi possivel criar o parceiro.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Partner) {
    if (!item.id) {
      setError("Nao foi possivel identificar o parceiro para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o parceiro "${item.nome}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("core", "parceiros", item.id);
      await loadPartners();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyPartner });
      }

      setSuccess("Parceiro excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir o parceiro."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "partners-page partners-page--embedded"
          : "partners-page"
      }
    >
      {!embedded ? (
        <header className="partners-page__header">
          <div>
            <span className="partners-page__eyebrow">CORE</span>
            <h2 className="partners-page__title">Parceiros</h2>
            <p className="partners-page__subtitle">
              Gerencie clientes, fornecedores e parceiros de negocio.
            </p>
          </div>

          <div className="partners-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por nome, documento, codigo ou tipo"
              className="partners-page__search"
            />
            <button
              type="button"
              className="partners-page__button"
              onClick={handleCreateNew}
            >
              Novo parceiro
            </button>
          </div>
        </header>
      ) : (
        <div className="partners-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por nome, documento, codigo ou tipo"
            className="partners-page__search"
          />
          <div className="partners-page__toolbar-actions">
            <button
              type="button"
              className="partners-page__ghost"
              onClick={() => void loadPartners()}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="partners-page__button"
              onClick={handleCreateNew}
            >
              Novo parceiro
            </button>
          </div>
        </div>
      )}

      {error ? <div className="partners-page__alert">{error}</div> : null}
      {success ? (
        <div className="partners-page__alert partners-page__alert--success">
          {success}
        </div>
      ) : null}

      <div className="partners-page__layout">
        <PartnerTable
          items={filteredItems}
          loading={loading}
          selectedId={selected?.id}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <PartnerForm
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
