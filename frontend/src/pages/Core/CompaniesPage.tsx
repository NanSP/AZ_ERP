import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import CompanyTable from "../../components/Core/CompanyTable";
import CompanyForm from "../../components/Core/CompanyForm";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./companies-page.css";

export type Company = {
  id?: number;
  codigo: string;
  razaoSocial: string;
  nomeFantasia: string;
  cnpj: string;
  inscricaoEstadual: string;
  inscricaoMunicipal: string;
  regimeTributario: string;
  dataFundacao: string;
  situacao: string;
  createdAt?: string;
};

type CompaniesPageProps = {
  embedded?: boolean;
};

const emptyCompany: Company = {
  codigo: "",
  razaoSocial: "",
  nomeFantasia: "",
  cnpj: "",
  inscricaoEstadual: "",
  inscricaoMunicipal: "",
  regimeTributario: "",
  dataFundacao: "",
  situacao: "ativo",
};

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

function toRequestPayload(company: Company) {
  return {
    codigo: company.codigo.trim() || null,
    razaoSocial: company.razaoSocial.trim(),
    nomeFantasia: company.nomeFantasia.trim() || null,
    cnpj: company.cnpj.trim() || null,
    inscricaoEstadual: company.inscricaoEstadual.trim() || null,
    inscricaoMunicipal: company.inscricaoMunicipal.trim() || null,
    regimeTributario: company.regimeTributario.trim() || null,
    dataFundacao: company.dataFundacao.trim() || null,
    situacao: company.situacao.trim() || null,
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

export default function CompaniesPage({
  embedded = false,
}: CompaniesPageProps) {
  const [items, setItems] = useState<Company[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Company | null>(null);
  const [draft, setDraft] = useState<Company>(emptyCompany);
  const isBusy = loading || saving;

  async function loadCompanies() {
    setLoading(true);
    setError(null);

    try {
      const response = await listResource("core", "empresas");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeCompany(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar as empresas."),
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadCompanies();
  }, []);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.codigo,
        item.razaoSocial,
        item.nomeFantasia,
        item.cnpj,
        item.regimeTributario,
      ]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    setSelected(null);
    setDraft({ ...emptyCompany });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Company) {
    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Company) {
    setDraft(next);
  }

  async function handleSave() {
    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("core", "empresas", selected.id, payload)
        : await createResource("core", "empresas", payload);

      const saved = normalizeCompany(response.data as Record<string, unknown>);
      await loadCompanies();
      setSelected(saved);
      setDraft(saved);
      setSuccess(
        selected?.id
          ? "Empresa atualizada com sucesso."
          : "Empresa criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar a empresa."
            : "Nao foi possivel criar a empresa.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Company) {
    if (!item.id) {
      setError("Nao foi possivel identificar a empresa para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a empresa "${item.razaoSocial}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("core", "empresas", item.id);
      await loadCompanies();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyCompany });
      }

      setSuccess("Empresa excluida com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir a empresa."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "companies-page companies-page--embedded"
          : "companies-page"
      }
    >
      {!embedded ? (
        <header className="companies-page__header">
          <div>
            <span className="companies-page__eyebrow">CORE</span>
            <h2 className="companies-page__title">Empresas</h2>
            <p className="companies-page__subtitle">
              Gerencie empresas, filiais e estruturas corporativas do ERP.
            </p>
          </div>

          <div className="companies-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por razao social, CNPJ, codigo ou regime"
              className="companies-page__search"
              disabled={isBusy}
            />
            <button
              type="button"
              className="companies-page__button"
              onClick={handleCreateNew}
              disabled={isBusy}
            >
              Nova empresa
            </button>
          </div>
        </header>
      ) : (
        <div className="companies-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por razao social, CNPJ, codigo ou regime"
            className="companies-page__search"
            disabled={isBusy}
          />
          <div className="companies-page__toolbar-actions">
            <button
              type="button"
              className="companies-page__ghost"
              onClick={() => void loadCompanies()}
              disabled={isBusy}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="companies-page__button"
              onClick={handleCreateNew}
              disabled={isBusy}
            >
              Nova empresa
            </button>
          </div>
        </div>
      )}

      {error ? <div className="companies-page__alert">{error}</div> : null}
      {success ? (
        <div className="companies-page__alert companies-page__alert--success">
          {success}
        </div>
      ) : null}

      <div className="companies-page__layout">
        <CompanyTable
          items={filteredItems}
          loading={loading}
          selectedId={selected?.id}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <CompanyForm
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
