import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import BankMovementForm from "../../components/Fi/BankMovementForm";
import BankMovementTable from "../../components/Fi/BankMovementTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./bank-movements-page.css";

export type BankMovement = {
  id?: number;
  contaBancariaId: string;
  tipoMovimento: string;
  valor: string;
  dataMovimento: string;
  historico: string;
  documentoVinculado: string;
  conciliado: boolean;
  dataConciliacao: string;
  createdAt?: string;
};

type BankMovementsPageProps = {
  embedded?: boolean;
};

const emptyMovement: BankMovement = {
  contaBancariaId: "",
  tipoMovimento: "credito",
  valor: "",
  dataMovimento: "",
  historico: "",
  documentoVinculado: "",
  conciliado: false,
  dataConciliacao: "",
};

function normalizeMovement(data: Record<string, unknown>): BankMovement {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    contaBancariaId:
      data.contaBancariaId == null ? "" : String(data.contaBancariaId),
    tipoMovimento: String(data.tipoMovimento ?? "credito"),
    valor: data.valor == null ? "" : String(data.valor),
    dataMovimento: String(data.dataMovimento ?? ""),
    historico: String(data.historico ?? ""),
    documentoVinculado: String(data.documentoVinculado ?? ""),
    conciliado: Boolean(data.conciliado),
    dataConciliacao: String(data.dataConciliacao ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(movement: BankMovement) {
  return {
    contaBancariaId:
      movement.contaBancariaId.trim() === ""
        ? null
        : Number(movement.contaBancariaId),
    tipoMovimento: movement.tipoMovimento.trim(),
    valor:
      movement.valor.trim() === ""
        ? null
        : Number(movement.valor.replace(",", ".")),
    dataMovimento: movement.dataMovimento.trim() || null,
    historico: movement.historico.trim() || null,
    documentoVinculado: movement.documentoVinculado.trim() || null,
    conciliado: movement.conciliado,
    dataConciliacao: movement.conciliado
      ? movement.dataConciliacao.trim() || null
      : null,
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

export default function BankMovementsPage({
  embedded = false,
}: BankMovementsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<BankMovement[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<BankMovement | null>(null);
  const [draft, setDraft] = useState<BankMovement>(emptyMovement);
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead =
    isMasterScope || permissionSet.has("fi:movimentacoes_bancarias:read");
  const canCreate =
    isMasterScope || permissionSet.has("fi:movimentacoes_bancarias:create");
  const canUpdate =
    isMasterScope || permissionSet.has("fi:movimentacoes_bancarias:update");
  const canDelete =
    isMasterScope || permissionSet.has("fi:movimentacoes_bancarias:delete");
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
      const response = await listResource("fi", "movimentacoesBancarias");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeMovement(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Nao foi possivel carregar as movimentacoes bancarias.",
        ),
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadMovements();
  }, [canRead]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.contaBancariaId,
        item.tipoMovimento,
        item.valor,
        item.dataMovimento,
        item.historico,
        item.documentoVinculado,
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

  function handleSelect(item: BankMovement) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: BankMovement) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar movimentacoes bancarias."
          : "Seu perfil nao possui permissao para criar movimentacoes bancarias.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource(
            "fi",
            "movimentacoesBancarias",
            selected.id,
            payload,
          )
        : await createResource("fi", "movimentacoesBancarias", payload);

      const saved = normalizeMovement(response.data as Record<string, unknown>);
      await loadMovements();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Movimentacao bancaria atualizada com sucesso."
          : "Movimentacao bancaria criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar a movimentacao bancaria."
            : "Nao foi possivel criar a movimentacao bancaria.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: BankMovement) {
    if (!canDelete) {
      setError(
        "Seu perfil nao possui permissao para excluir movimentacoes bancarias.",
      );
      return;
    }

    if (!item.id) {
      setError(
        "Nao foi possivel identificar a movimentacao bancaria para exclusao.",
      );
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
      await deleteResource("fi", "movimentacoesBancarias", item.id);
      await loadMovements();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyMovement });
      }

      setSuccess("Movimentacao bancaria excluida com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Nao foi possivel excluir a movimentacao bancaria.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "bank-movements-page bank-movements-page--embedded"
          : "bank-movements-page"
      }
    >
      {!embedded ? (
        <header className="bank-movements-page__header">
          <div>
            <span className="bank-movements-page__eyebrow">FI</span>
            <h2 className="bank-movements-page__title">
              Movimentacoes Bancarias
            </h2>
            <p className="bank-movements-page__subtitle">
              Registre creditos, debitos e transferencias com controle de
              conciliacao e documento vinculado.
            </p>
          </div>

          <div className="bank-movements-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por conta, tipo, valor, data ou historico"
              className="bank-movements-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="bank-movements-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova movimentacao
            </button>
          </div>
        </header>
      ) : (
        <div className="bank-movements-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por conta, tipo, valor, data ou historico"
            className="bank-movements-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="bank-movements-page__toolbar-actions">
            <button
              type="button"
              className="bank-movements-page__ghost"
              onClick={() => void loadMovements()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="bank-movements-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova movimentacao
            </button>
          </div>
        </div>
      )}

      {error ? (
        <div className="bank-movements-page__alert">{error}</div>
      ) : null}
      {success ? (
        <div className="bank-movements-page__alert bank-movements-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="bank-movements-page__alert bank-movements-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="bank-movements-page__layout">
        <BankMovementTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <BankMovementForm
          value={draft}
          editing={!!selected}
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
