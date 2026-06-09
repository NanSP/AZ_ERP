import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import TimeTrackingForm from "../../components/Rh/TimeTrackingForm";
import TimeTrackingTable from "../../components/Rh/TimeTrackingTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./time-tracking-page.css";

export type EmployeeOption = {
  id: number;
  label: string;
};

export type EmployeeAccess = "idle" | "loaded" | "unavailable";

export type TimeTrackingEntry = {
  id?: number;
  colaborador: string;
  data: string;
  horaEntrada: string;
  horaSaidaAlmoco: string;
  horaRetornoAlmoco: string;
  horaSaida: string;
  horasTrabalhadas: string;
  horasExtras: string;
  atrasos?: number;
  createdAt?: string;
};

type TimeTrackingPageProps = {
  embedded?: boolean;
};

const emptyEntry: TimeTrackingEntry = {
  colaborador: "",
  data: "",
  horaEntrada: "",
  horaSaidaAlmoco: "",
  horaRetornoAlmoco: "",
  horaSaida: "",
  horasTrabalhadas: "",
  horasExtras: "",
  atrasos: 0,
};

function normalizeEntry(data: Record<string, unknown>): TimeTrackingEntry {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    colaborador: data.colaborador == null ? "" : String(data.colaborador),
    data: String(data.data ?? ""),
    horaEntrada: String(data.horaEntrada ?? ""),
    horaSaidaAlmoco: String(data.horaSaidaAlmoco ?? ""),
    horaRetornoAlmoco: String(data.horaRetornoAlmoco ?? ""),
    horaSaida: String(data.horaSaida ?? ""),
    horasTrabalhadas:
      data.horasTrabalhadas == null ? "" : String(data.horasTrabalhadas),
    horasExtras: data.horasExtras == null ? "" : String(data.horasExtras),
    atrasos: typeof data.atrasos === "number" ? data.atrasos : 0,
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(entry: TimeTrackingEntry) {
  return {
    colaborador:
      entry.colaborador.trim() === "" ? null : Number(entry.colaborador),
    data: entry.data.trim() || null,
    horaEntrada: entry.horaEntrada.trim() || null,
    horaSaidaAlmoco: entry.horaSaidaAlmoco.trim() || null,
    horaRetornoAlmoco: entry.horaRetornoAlmoco.trim() || null,
    horaSaida: entry.horaSaida.trim() || null,
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

export default function TimeTrackingPage({
  embedded = false,
}: TimeTrackingPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<TimeTrackingEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<TimeTrackingEntry | null>(null);
  const [draft, setDraft] = useState<TimeTrackingEntry>(emptyEntry);
  const [employeeOptions, setEmployeeOptions] = useState<EmployeeOption[]>([]);
  const [employeeAccess, setEmployeeAccess] = useState<EmployeeAccess>("idle");
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead =
    isMasterScope || permissionSet.has("rh:controle_de_ponto:read");
  const canCreate =
    isMasterScope || permissionSet.has("rh:controle_de_ponto:create");
  const canUpdate =
    isMasterScope || permissionSet.has("rh:controle_de_ponto:update");
  const canDelete =
    isMasterScope || permissionSet.has("rh:controle_de_ponto:delete");
  const canReadEmployees =
    isMasterScope || permissionSet.has("rh:colaboradores:read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadEntries = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyEntry });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("rh", "controleDePonto");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeEntry(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Não foi possível carregar os registros de ponto.",
        ),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadEmployees = useCallback(async () => {
    if (!canReadEmployees) {
      setEmployeeOptions([]);
      setEmployeeAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("rh", "colaboradores");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nome ?? "Colaborador")} (#${String(item.id)})`,
            }))
        : [];
      setEmployeeOptions(nextItems);
      setEmployeeAccess("loaded");
    } catch {
      setEmployeeOptions([]);
      setEmployeeAccess("unavailable");
    }
  }, [canReadEmployees]);

  useEffect(() => {
    void loadEntries();
  }, [loadEntries]);

  useEffect(() => {
    void loadEmployees();
  }, [loadEmployees]);

  const employeeLabelMap = useMemo(
    () =>
      new Map(employeeOptions.map((option) => [String(option.id), option.label])),
    [employeeOptions],
  );

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.colaborador,
        employeeLabelMap.get(item.colaborador) ?? "",
        item.data,
        item.horaEntrada,
        item.horaSaida,
        item.horasTrabalhadas,
        item.horasExtras,
      ]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [employeeLabelMap, items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyEntry });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: TimeTrackingEntry) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: TimeTrackingEntry) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar registros de ponto."
          : "Seu perfil não possui permissão para criar registros de ponto.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("rh", "controleDePonto", selected.id, payload)
        : await createResource("rh", "controleDePonto", payload);

      const saved = normalizeEntry(response.data as Record<string, unknown>);
      await loadEntries();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Registro de ponto atualizado com sucesso."
          : "Registro de ponto criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar o registro de ponto."
            : "Não foi possível criar o registro de ponto.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: TimeTrackingEntry) {
    if (!canDelete) {
      setError(
        "Seu perfil não possui permissão para excluir registros de ponto.",
      );
      return;
    }

    if (!item.id) {
      setError(
        "Não foi possível identificar o registro de ponto para exclusão.",
      );
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o registro de ponto do dia "${item.data || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("rh", "controleDePonto", item.id);
      await loadEntries();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyEntry });
      }

      setSuccess("Registro de ponto excluído com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível excluir o registro de ponto."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "time-tracking-page time-tracking-page--embedded"
          : "time-tracking-page"
      }
    >
      {!embedded ? (
        <header className="time-tracking-page__header">
          <div>
            <span className="time-tracking-page__eyebrow">RH</span>
            <h2 className="time-tracking-page__title">Controle de Ponto</h2>
            <p className="time-tracking-page__subtitle">
              Registre a jornada dos colaboradores com cálculo automático de
              horas trabalhadas, extras e atrasos.
            </p>
          </div>

          <div className="time-tracking-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por colaborador, data, horas ou jornada"
              className="time-tracking-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="time-tracking-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo registro
            </button>
          </div>
        </header>
      ) : (
        <div className="time-tracking-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por colaborador, data, horas ou jornada"
            className="time-tracking-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="time-tracking-page__toolbar-actions">
            <button
              type="button"
              className="time-tracking-page__ghost"
              onClick={() => void loadEntries()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="time-tracking-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo registro
            </button>
          </div>
        </div>
      )}

      {error ? <div className="time-tracking-page__alert">{error}</div> : null}
      {success ? (
        <div className="time-tracking-page__alert time-tracking-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="time-tracking-page__alert time-tracking-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="time-tracking-page__layout">
        <TimeTrackingTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          employeeOptions={employeeOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <TimeTrackingForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          employeeOptions={employeeOptions}
          employeeAccess={employeeAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
