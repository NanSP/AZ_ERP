import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import MovementForm from "../../components/Mm/MovementForm";
import MovementsTable from "../../components/Mm/MovementsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { StockRecord } from "./StocksPage";
import type { User } from "../Sys/UsersPage";
import "./movements-page.css";

export type MovementRecord = {
  id?: number;
  estoqueId: string;
  tipoMovimento: string;
  quantidade: string;
  valorUnitario: string;
  valorTotal: string;
  documentoReferencia: string;
  motivo: string;
  usuarioId: string;
  createdAt?: string;
};

type MovementsPageProps = {
  embedded?: boolean;
};

const movementsResource = {
  schema: "mm",
  entity: "movimentacoes",
  label: "Movimentacoes",
  description: "Entradas, saidas e transferencias.",
} as const;

const stocksResource = {
  schema: "mm",
  entity: "estoques",
  label: "Estoques",
  description: "Posicoes e controle de estoque.",
} as const;

const usersResource = {
  schema: "sys",
  entity: "usuarios",
  label: "Usuarios",
  description: "Gestao de usuarios do tenant.",
} as const;

const emptyMovement: MovementRecord = {
  estoqueId: "",
  tipoMovimento: "entrada",
  quantidade: "",
  valorUnitario: "",
  valorTotal: "",
  documentoReferencia: "",
  motivo: "",
  usuarioId: "",
};

function normalizeMovement(data: Record<string, unknown>): MovementRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    estoqueId: data.estoque == null ? "" : String(data.estoque),
    tipoMovimento: String(data.tipoMovimento ?? "entrada"),
    quantidade: data.quantidade == null ? "" : String(data.quantidade),
    valorUnitario:
      data.valorUnitario == null ? "" : String(data.valorUnitario),
    valorTotal: data.valorTotal == null ? "" : String(data.valorTotal),
    documentoReferencia: String(data.documentoReferencia ?? ""),
    motivo: String(data.motivo ?? ""),
    usuarioId: data.usuario == null ? "" : String(data.usuario),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

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

function normalizeUser(data: Record<string, unknown>): User {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    nome: String(data.nome ?? ""),
    email: String(data.email ?? ""),
    login: String(data.login ?? ""),
    senha: "",
    documento: String(data.documento ?? ""),
    tipoUsuario: String(data.tipoUsuario ?? "operador"),
    status: String(data.status ?? "ativo"),
    expiracaoSenha: String(data.expiracaoSenha ?? ""),
    tentativasLogin:
      data.tentativasLogin == null ? "0" : String(data.tentativasLogin),
    ultimoAcesso:
      data.ultimoAcesso == null ? undefined : String(data.ultimoAcesso),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(item: MovementRecord) {
  function toNumberOrNull(value: string) {
    const normalized = value.trim();
    if (normalized === "") {
      return null;
    }

    return Number(normalized.replace(",", "."));
  }

  return {
    estoque: item.estoqueId.trim() === "" ? null : Number(item.estoqueId),
    tipoMovimento: item.tipoMovimento.trim() || null,
    quantidade: toNumberOrNull(item.quantidade),
    valorUnitario: toNumberOrNull(item.valorUnitario),
    documentoReferencia: item.documentoReferencia.trim() || null,
    motivo: item.motivo.trim() || null,
    usuario: item.usuarioId.trim() === "" ? null : Number(item.usuarioId),
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

export default function MovementsPage({ embedded = false }: MovementsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<MovementRecord[]>([]);
  const [stocks, setStocks] = useState<StockRecord[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<MovementRecord | null>(null);
  const [draft, setDraft] = useState<MovementRecord>(emptyMovement);
  const canRead = canAccessResourceAction(session, movementsResource, "read");
  const canCreate = canAccessResourceAction(session, movementsResource, "create");
  const canUpdate = canAccessResourceAction(session, movementsResource, "update");
  const canDelete = canAccessResourceAction(session, movementsResource, "delete");
  const canReadStocks = canAccessResourceAction(session, stocksResource, "read");
  const canReadUsers = canAccessResourceAction(session, usersResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadMovements() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyMovement });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("mm", "movimentacoes");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeMovement(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar as movimentacoes."),
      );
    } finally {
      setLoading(false);
    }
  }

  async function loadStocks() {
    if (!canReadStocks) {
      setStocks([]);
      return;
    }

    try {
      const response = await listResource("mm", "estoques");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeStock(item as Record<string, unknown>),
          )
        : [];
      setStocks(nextItems);
    } catch {
      setStocks([]);
    }
  }

  async function loadUsers() {
    if (!canReadUsers) {
      setUsers([]);
      return;
    }

    try {
      const response = await listResource("sys", "usuarios");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) => normalizeUser(item as Record<string, unknown>))
        : [];
      setUsers(nextItems);
    } catch {
      setUsers([]);
    }
  }

  useEffect(() => {
    void loadMovements();
  }, [canRead]);

  useEffect(() => {
    void loadStocks();
  }, [canReadStocks]);

  useEffect(() => {
    void loadUsers();
  }, [canReadUsers]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.estoqueId,
        item.tipoMovimento,
        item.documentoReferencia,
        item.motivo,
        item.usuarioId,
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
    setDraft({ ...emptyMovement });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: MovementRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: MovementRecord) {
    const quantidade = Number((next.quantidade || "").replace(",", "."));
    const valorUnitario = Number((next.valorUnitario || "").replace(",", "."));
    const valorTotal =
      Number.isFinite(quantidade) && Number.isFinite(valorUnitario)
        ? String(quantidade * valorUnitario)
        : "";

    setDraft({
      ...next,
      valorTotal,
    });
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar movimentacoes."
          : "Seu perfil nao possui permissao para criar movimentacoes.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("mm", "movimentacoes", selected.id, payload)
        : await createResource("mm", "movimentacoes", payload);

      const saved = normalizeMovement(response.data as Record<string, unknown>);
      await loadMovements();
      await loadStocks();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Movimentacao atualizada com sucesso."
          : "Movimentacao criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar a movimentacao."
            : "Nao foi possivel criar a movimentacao.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: MovementRecord) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir movimentacoes.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar a movimentacao para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a movimentacao "${item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("mm", "movimentacoes", item.id);
      await loadMovements();
      await loadStocks();
      setSuccess("Movimentacao excluida com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir a movimentacao."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "movements-page movements-page--embedded" : "movements-page"}>
      {!embedded ? (
        <header className="movements-page__header">
          <div>
            <span className="movements-page__eyebrow">MM</span>
            <h2 className="movements-page__title">Movimentacoes</h2>
            <p className="movements-page__subtitle">
              Registre entradas, saidas, transferencias, ajustes e inventarios
              com reflexo direto no saldo do estoque.
            </p>
          </div>

          <div className="movements-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por estoque, tipo, documento, motivo ou usuario"
              className="movements-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="movements-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova movimentacao
            </button>
          </div>
        </header>
      ) : (
        <div className="movements-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por estoque, tipo, documento, motivo ou usuario"
            className="movements-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="movements-page__toolbar-actions">
            <button
              type="button"
              className="movements-page__ghost"
              onClick={() => void loadMovements()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="movements-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova movimentacao
            </button>
          </div>
        </div>
      )}

      {error ? <div className="movements-page__alert">{error}</div> : null}
      {success ? (
        <div className="movements-page__alert movements-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="movements-page__alert movements-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="movements-page__layout">
        <MovementsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <MovementForm
          value={draft}
          editing={!!selected}
          stocks={stocks}
          users={users}
          canReadStocks={canReadStocks}
          canReadUsers={canReadUsers}
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
