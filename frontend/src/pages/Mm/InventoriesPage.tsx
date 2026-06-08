import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import InventoryForm from "../../components/Mm/InventoryForm";
import InventoriesTable from "../../components/Mm/InventoriesTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./inventories-page.css";

export type InventoryRecord = {
  id?: number;
  dataInicio: string;
  dataFim: string;
  tipoInventario: string;
  status: string;
  observacoes: string;
  createdAt?: string;
};

type InventoriesPageProps = {
  embedded?: boolean;
};

const inventoriesResource = {
  schema: "mm",
  entity: "inventarios",
  label: "Inventarios",
  description: "Inventarios fisicos e ajustes.",
} as const;

const emptyInventory: InventoryRecord = {
  dataInicio: "",
  dataFim: "",
  tipoInventario: "rotativo",
  status: "planejado",
  observacoes: "",
};

function normalizeInventory(data: Record<string, unknown>): InventoryRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    dataInicio: data.dataInicio == null ? "" : String(data.dataInicio),
    dataFim: data.dataFim == null ? "" : String(data.dataFim),
    tipoInventario: String(data.tipoInventario ?? "rotativo"),
    status: String(data.status ?? "planejado"),
    observacoes: String(data.observacoes ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(item: InventoryRecord) {
  return {
    dataInicio: item.dataInicio.trim() || null,
    dataFim: item.dataFim.trim() || null,
    tipoInventario: item.tipoInventario.trim() || null,
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

export default function InventoriesPage({
  embedded = false,
}: InventoriesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<InventoryRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<InventoryRecord | null>(null);
  const [draft, setDraft] = useState<InventoryRecord>(emptyInventory);
  const canRead = canAccessResourceAction(session, inventoriesResource, "read");
  const canCreate = canAccessResourceAction(
    session,
    inventoriesResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    inventoriesResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    inventoriesResource,
    "delete",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadInventories() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyInventory });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("mm", "inventarios");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeInventory(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar os inventarios."),
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadInventories();
  }, [canRead]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.tipoInventario,
        item.status,
        item.dataInicio,
        item.dataFim,
        item.observacoes,
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
    setDraft({ ...emptyInventory });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: InventoryRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: InventoryRecord) {
    const nextDraft = { ...next };

    if (nextDraft.status === "planejado") {
      nextDraft.dataFim = "";
    }

    setDraft(nextDraft);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar inventarios."
          : "Seu perfil nao possui permissao para criar inventarios.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("mm", "inventarios", selected.id, payload)
        : await createResource("mm", "inventarios", payload);

      const saved = normalizeInventory(response.data as Record<string, unknown>);
      await loadInventories();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Inventario atualizado com sucesso."
          : "Inventario criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o inventario."
            : "Nao foi possivel criar o inventario.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: InventoryRecord) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir inventarios.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o inventario para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o inventario "${item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("mm", "inventarios", item.id);
      await loadInventories();
      setSuccess("Inventario excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o inventario."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "inventories-page inventories-page--embedded"
          : "inventories-page"
      }
    >
      {!embedded ? (
        <header className="inventories-page__header">
          <div>
            <span className="inventories-page__eyebrow">MM</span>
            <h2 className="inventories-page__title">Inventarios</h2>
            <p className="inventories-page__subtitle">
              Planeje contagens fisicas, acompanhe inventarios em andamento e
              feche ciclos anuais, rotativos ou por amostragem.
            </p>
          </div>

          <div className="inventories-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por tipo, status, datas ou observacoes"
              className="inventories-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="inventories-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo inventario
            </button>
          </div>
        </header>
      ) : (
        <div className="inventories-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por tipo, status, datas ou observacoes"
            className="inventories-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="inventories-page__toolbar-actions">
            <button
              type="button"
              className="inventories-page__ghost"
              onClick={() => void loadInventories()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="inventories-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo inventario
            </button>
          </div>
        </div>
      )}

      {error ? <div className="inventories-page__alert">{error}</div> : null}
      {success ? (
        <div className="inventories-page__alert inventories-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="inventories-page__alert inventories-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="inventories-page__layout">
        <InventoriesTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <InventoryForm
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
