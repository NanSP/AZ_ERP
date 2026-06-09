import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import AttendanceForm from "../../components/Sm/AttendanceForm";
import AttendancesTable from "../../components/Sm/AttendancesTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./attendances-page.css";

export type OrderOption = {
  id: number;
  label: string;
};

export type EmployeeOption = {
  id: number;
  label: string;
};

export type RelatedAccess = "idle" | "loaded" | "unavailable";

export type Attendance = {
  id?: number;
  os: string;
  tecnico: string;
  dataHora: string;
  descricao: string;
  horasGastas: string;
  materiaisUtilizados: string;
  createdAt?: string;
};

type AttendancesPageProps = {
  embedded?: boolean;
};

const attendancesResource = {
  schema: "sm",
  entity: "atendimentos",
  label: "Atendimentos",
  description: "Chamados e atendimentos.",
} as const;

const ordersResource = {
  schema: "sm",
  entity: "ordensServico",
  label: "Ordens de Servico",
  description: "Ordens e execucao de servicos.",
} as const;

const employeesResource = {
  schema: "rh",
  entity: "colaboradores",
  label: "Colaboradores",
  description: "Tecnicos e equipe.",
} as const;

const emptyAttendance: Attendance = {
  os: "",
  tecnico: "",
  dataHora: "",
  descricao: "",
  horasGastas: "",
  materiaisUtilizados: "",
};

function normalizeDateTime(value: unknown) {
  if (value == null) {
    return "";
  }

  const raw = String(value);
  return raw.length >= 16 ? raw.slice(0, 16) : raw;
}

function normalizeMaterials(value: unknown) {
  if (value == null) {
    return "";
  }

  try {
    return JSON.stringify(value, null, 2);
  } catch {
    return "";
  }
}

function normalizeAttendance(data: Record<string, unknown>): Attendance {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    os: data.os == null ? "" : String(data.os),
    tecnico: data.tecnico == null ? "" : String(data.tecnico),
    dataHora: normalizeDateTime(data.dataHora),
    descricao: String(data.descricao ?? ""),
    horasGastas: data.horasGastas == null ? "" : String(data.horasGastas),
    materiaisUtilizados: normalizeMaterials(data.materiaisUtilizados),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function parseMaterials(value: string) {
  const trimmed = value.trim();
  if (trimmed === "") {
    return null;
  }

  return JSON.parse(trimmed) as Record<string, unknown>;
}

function toRequestPayload(attendance: Attendance) {
  return {
    os: attendance.os.trim() === "" ? null : Number(attendance.os),
    tecnico:
      attendance.tecnico.trim() === "" ? null : Number(attendance.tecnico),
    dataHora: attendance.dataHora.trim() || null,
    descricao: attendance.descricao.trim() || null,
    horasGastas:
      attendance.horasGastas.trim() === ""
        ? null
        : Number(attendance.horasGastas.replace(",", ".")),
    materiaisUtilizados: parseMaterials(attendance.materiaisUtilizados),
  };
}

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message = error.response?.data?.message;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  if (error instanceof SyntaxError) {
    return "Materiais utilizados deve conter um JSON válido.";
  }

  return fallback;
}

function mapOption(
  item: Record<string, unknown>,
  fallbackLabel: string,
): { id: number; label: string } | null {
  if (typeof item.id !== "number") {
    return null;
  }

  return {
    id: item.id,
    label: `${String(item.numeroOs ?? item.nome ?? fallbackLabel)} (#${String(item.id)})`,
  };
}

export default function AttendancesPage({
  embedded = false,
}: AttendancesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<Attendance[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Attendance | null>(null);
  const [draft, setDraft] = useState<Attendance>(emptyAttendance);
  const [orderOptions, setOrderOptions] = useState<OrderOption[]>([]);
  const [orderAccess, setOrderAccess] = useState<RelatedAccess>("idle");
  const [employeeOptions, setEmployeeOptions] = useState<EmployeeOption[]>([]);
  const [employeeAccess, setEmployeeAccess] = useState<RelatedAccess>("idle");
  const canRead = canAccessResourceAction(session, attendancesResource, "read");
  const canCreate = canAccessResourceAction(
    session,
    attendancesResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    attendancesResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    attendancesResource,
    "delete",
  );
  const canReadOrders = canAccessResourceAction(
    session,
    ordersResource,
    "read",
  );
  const canReadEmployees = canAccessResourceAction(
    session,
    employeesResource,
    "read",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadAttendances = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyAttendance });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("sm", "atendimentos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeAttendance(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível carregar os atendimentos."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadOrders = useCallback(async () => {
    if (!canReadOrders) {
      setOrderOptions([]);
      setOrderAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("sm", "ordensServico");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => mapOption(item as Record<string, unknown>, "OS"))
            .filter((item): item is OrderOption => item !== null)
        : [];
      setOrderOptions(nextItems);
      setOrderAccess("loaded");
    } catch {
      setOrderOptions([]);
      setOrderAccess("unavailable");
    }
  }, [canReadOrders]);

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
            .map((item) =>
              mapOption(item as Record<string, unknown>, "Tecnico"),
            )
            .filter((item): item is EmployeeOption => item !== null)
        : [];
      setEmployeeOptions(nextItems);
      setEmployeeAccess("loaded");
    } catch {
      setEmployeeOptions([]);
      setEmployeeAccess("unavailable");
    }
  }, [canReadEmployees]);

  useEffect(() => {
    void loadAttendances();
  }, [loadAttendances]);

  useEffect(() => {
    void loadOrders();
  }, [loadOrders]);

  useEffect(() => {
    void loadEmployees();
  }, [loadEmployees]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.descricao, item.os, item.tecnico, item.horasGastas]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyAttendance });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Attendance) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Attendance) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar atendimentos."
          : "Seu perfil não possui permissão para criar atendimentos.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("sm", "atendimentos", selected.id, payload)
        : await createResource("sm", "atendimentos", payload);

      const saved = normalizeAttendance(
        response.data as Record<string, unknown>,
      );
      await loadAttendances();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Atendimento atualizado com sucesso."
          : "Atendimento criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar o atendimento."
            : "Não foi possível criar o atendimento.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Attendance) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir atendimentos.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar o atendimento para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o atendimento "${item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sm", "atendimentos", item.id);
      await loadAttendances();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyAttendance });
      }

      setSuccess("Atendimento excluído com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível excluir o atendimento."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "attendances-page attendances-page--embedded"
          : "attendances-page"
      }
    >
      {!embedded ? (
        <header className="attendances-page__header">
          <div>
            <span className="attendances-page__eyebrow">SM</span>
            <h2 className="attendances-page__title">Atendimentos</h2>
            <p className="attendances-page__subtitle">
              Registre execução técnica, horas gastas e materiais utilizados por
              ordem de serviço.
            </p>
          </div>

          <div className="attendances-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por OS, técnico, descrição ou horas"
              className="attendances-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="attendances-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo atendimento
            </button>
          </div>
        </header>
      ) : (
        <div className="attendances-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por OS, técnico, descrição ou horas"
            className="attendances-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="attendances-page__toolbar-actions">
            <button
              type="button"
              className="attendances-page__ghost"
              onClick={() => void loadAttendances()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="attendances-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo atendimento
            </button>
          </div>
        </div>
      )}

      {error ? <div className="attendances-page__alert">{error}</div> : null}
      {success ? (
        <div className="attendances-page__alert attendances-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="attendances-page__alert attendances-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="attendances-page__layout">
        <AttendancesTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          orderOptions={orderOptions}
          employeeOptions={employeeOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <AttendanceForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          orderOptions={orderOptions}
          orderAccess={orderAccess}
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
