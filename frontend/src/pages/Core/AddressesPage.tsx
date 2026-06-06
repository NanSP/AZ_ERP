import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import AddressTable from "../../components/Core/AddressTable";
import AddressForm from "../../components/Core/AddressForm";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./addresses-page.css";

export type Address = {
  id?: number;
  entidadeTipo: string;
  entidadeId: string;
  tipoEndereco: string;
  logradouro: string;
  numero: string;
  complemento: string;
  bairro: string;
  cidade: string;
  uf: string;
  cep: string;
  pais: string;
  principal: boolean;
  createdAt?: string;
};

export type AddressRelatedEntityOption = {
  value: string;
  label: string;
};

type AddressesPageProps = {
  embedded?: boolean;
};

const emptyAddress: Address = {
  entidadeTipo: "empresa",
  entidadeId: "",
  tipoEndereco: "comercial",
  logradouro: "",
  numero: "",
  complemento: "",
  bairro: "",
  cidade: "",
  uf: "",
  cep: "",
  pais: "BRASIL",
  principal: true,
};

function normalizeAddress(data: Record<string, unknown>): Address {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    entidadeTipo: String(data.entidadeTipo ?? "empresa"),
    entidadeId: data.entidadeId == null ? "" : String(data.entidadeId),
    tipoEndereco: String(data.tipoEndereco ?? "comercial"),
    logradouro: String(data.logradouro ?? ""),
    numero: String(data.numero ?? ""),
    complemento: String(data.complemento ?? ""),
    bairro: String(data.bairro ?? ""),
    cidade: String(data.cidade ?? ""),
    uf: String(data.uf ?? ""),
    cep: String(data.cep ?? ""),
    pais: String(data.pais ?? "BRASIL"),
    principal: Boolean(data.principal),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(address: Address) {
  return {
    entidadeTipo: address.entidadeTipo.trim() || null,
    entidadeId:
      address.entidadeId.trim() === "" ? null : Number(address.entidadeId),
    tipoEndereco: address.tipoEndereco.trim() || null,
    logradouro: address.logradouro.trim() || null,
    numero: address.numero.trim() || null,
    complemento: address.complemento.trim() || null,
    bairro: address.bairro.trim() || null,
    cidade: address.cidade.trim() || null,
    uf: address.uf.trim() || null,
    cep: address.cep.trim() || null,
    pais: address.pais.trim() || null,
    principal: address.principal,
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
): AddressRelatedEntityOption[] {
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

export default function AddressesPage({
  embedded = false,
}: AddressesPageProps) {
  const [items, setItems] = useState<Address[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Address | null>(null);
  const [draft, setDraft] = useState<Address>(emptyAddress);
  const [empresaOptions, setEmpresaOptions] = useState<
    AddressRelatedEntityOption[]
  >([]);
  const [parceiroOptions, setParceiroOptions] = useState<
    AddressRelatedEntityOption[]
  >([]);
  const isBusy = loading || saving;

  async function loadAddresses() {
    setLoading(true);
    setError(null);

    try {
      const response = await listResource("core", "enderecos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeAddress(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar os enderecos."),
      );
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
    void loadAddresses();
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
        item.tipoEndereco,
        item.logradouro,
        item.cidade,
        item.cep,
      ]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    setSelected(null);
    setDraft({ ...emptyAddress });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Address) {
    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Address) {
    setDraft(next);
  }

  async function handleSave() {
    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("core", "enderecos", selected.id, payload)
        : await createResource("core", "enderecos", payload);

      const saved = normalizeAddress(response.data as Record<string, unknown>);
      await loadAddresses();
      setSelected(saved);
      setDraft(saved);
      setSuccess(
        selected?.id
          ? "Endereco atualizado com sucesso."
          : "Endereco criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o endereco."
            : "Nao foi possivel criar o endereco.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Address) {
    if (!item.id) {
      setError("Nao foi possivel identificar o endereco para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o endereco "${item.logradouro || item.cep}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("core", "enderecos", item.id);
      await loadAddresses();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyAddress });
      }

      setSuccess("Endereco excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir o endereco."),
      );
    } finally {
      setSaving(false);
    }
  }

  const relatedEntityOptions =
    draft.entidadeTipo === "empresa" ? empresaOptions : parceiroOptions;

  return (
    <div
      className={
        embedded
          ? "addresses-page addresses-page--embedded"
          : "addresses-page"
      }
    >
      {!embedded ? (
        <header className="addresses-page__header">
          <div>
            <span className="addresses-page__eyebrow">CORE</span>
            <h2 className="addresses-page__title">Enderecos</h2>
            <p className="addresses-page__subtitle">
              Gerencie enderecos comerciais, de cobranca, entrega e residenciais.
            </p>
          </div>

          <div className="addresses-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por entidade, logradouro, cidade, CEP ou tipo"
              className="addresses-page__search"
              disabled={isBusy}
            />
            <button
              type="button"
              className="addresses-page__button"
              onClick={handleCreateNew}
              disabled={isBusy}
            >
              Novo endereco
            </button>
          </div>
        </header>
      ) : (
        <div className="addresses-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por entidade, logradouro, cidade, CEP ou tipo"
            className="addresses-page__search"
            disabled={isBusy}
          />
          <div className="addresses-page__toolbar-actions">
            <button
              type="button"
              className="addresses-page__ghost"
              onClick={() => void loadAddresses()}
              disabled={isBusy}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="addresses-page__button"
              onClick={handleCreateNew}
              disabled={isBusy}
            >
              Novo endereco
            </button>
          </div>
        </div>
      )}

      {error ? <div className="addresses-page__alert">{error}</div> : null}
      {success ? (
        <div className="addresses-page__alert addresses-page__alert--success">
          {success}
        </div>
      ) : null}

      <div className="addresses-page__layout">
        <AddressTable
          items={filteredItems}
          loading={loading}
          selectedId={selected?.id}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <AddressForm
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
