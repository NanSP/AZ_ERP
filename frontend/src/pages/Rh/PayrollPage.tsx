import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import PayrollForm from "../../components/Rh/PayrollForm";
import PayrollTable from "../../components/Rh/PayrollTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./payroll-page.css";

export type EmployeeOption = {
  id: number;
  label: string;
};

export type EmployeeAccess = "idle" | "loaded" | "unavailable";

export type PayrollEntry = {
  id?: number;
  colaborador: string;
  competencia: string;
  salarioBase: string;
  horasNormais: string;
  horasExtras: string;
  adicionais: string;
  descontos: string;
  valorHora: string;
  valorHorasNormais: string;
  valorHorasExtras: string;
  valorBruto: string;
  valorLiquido: string;
  dataPagamento: string;
  status: string;
  createdAt?: string;
};

type PayrollPageProps = {
  embedded?: boolean;
};

const emptyPayroll: PayrollEntry = {
  colaborador: "",
  competencia: "",
  salarioBase: "",
  horasNormais: "",
  horasExtras: "",
  adicionais: "",
  descontos: "",
  valorHora: "",
  valorHorasNormais: "",
  valorHorasExtras: "",
  valorBruto: "",
  valorLiquido: "",
  dataPagamento: "",
  status: "calculado",
};

function normalizePayroll(data: Record<string, unknown>): PayrollEntry {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    colaborador: data.colaborador == null ? "" : String(data.colaborador),
    competencia: String(data.competencia ?? ""),
    salarioBase: data.salarioBase == null ? "" : String(data.salarioBase),
    horasNormais: data.horasNormais == null ? "" : String(data.horasNormais),
    horasExtras: data.horasExtras == null ? "" : String(data.horasExtras),
    adicionais: data.adicionais == null ? "" : String(data.adicionais),
    descontos: data.descontos == null ? "" : String(data.descontos),
    valorHora: data.valorHora == null ? "" : String(data.valorHora),
    valorHorasNormais:
      data.valorHorasNormais == null ? "" : String(data.valorHorasNormais),
    valorHorasExtras:
      data.valorHorasExtras == null ? "" : String(data.valorHorasExtras),
    valorBruto: data.valorBruto == null ? "" : String(data.valorBruto),
    valorLiquido: data.valorLiquido == null ? "" : String(data.valorLiquido),
    dataPagamento: String(data.dataPagamento ?? ""),
    status: String(data.status ?? "calculado"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value.replace(",", "."));
}

function toRequestPayload(payroll: PayrollEntry) {
  return {
    colaborador:
      payroll.colaborador.trim() === "" ? null : Number(payroll.colaborador),
    competencia: payroll.competencia.trim() || null,
    salarioBase: toNullableNumber(payroll.salarioBase),
    horasNormais: toNullableNumber(payroll.horasNormais),
    horasExtras: toNullableNumber(payroll.horasExtras),
    adicionais: toNullableNumber(payroll.adicionais),
    descontos: toNullableNumber(payroll.descontos),
    dataPagamento: payroll.dataPagamento.trim() || null,
    status: payroll.status.trim() || null,
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

export default function PayrollPage({ embedded = false }: PayrollPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<PayrollEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<PayrollEntry | null>(null);
  const [draft, setDraft] = useState<PayrollEntry>(emptyPayroll);
  const [employeeOptions, setEmployeeOptions] = useState<EmployeeOption[]>([]);
  const [employeeAccess, setEmployeeAccess] = useState<EmployeeAccess>("idle");
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead =
    isMasterScope || permissionSet.has("rh:folha_de_pagamento:read");
  const canCreate =
    isMasterScope || permissionSet.has("rh:folha_de_pagamento:create");
  const canUpdate =
    isMasterScope || permissionSet.has("rh:folha_de_pagamento:update");
  const canDelete =
    isMasterScope || permissionSet.has("rh:folha_de_pagamento:delete");
  const canReadEmployees =
    isMasterScope || permissionSet.has("rh:colaboradores:read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadPayroll = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyPayroll });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("rh", "folhaDePagamento");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizePayroll(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível carregar as folhas."));
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
    void loadPayroll();
  }, [loadPayroll]);

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
        item.competencia,
        item.status,
        item.valorBruto,
        item.valorLiquido,
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
    setDraft({ ...emptyPayroll });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: PayrollEntry) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: PayrollEntry) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar folhas."
          : "Seu perfil não possui permissão para criar folhas.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("rh", "folhaDePagamento", selected.id, payload)
        : await createResource("rh", "folhaDePagamento", payload);

      const saved = normalizePayroll(response.data as Record<string, unknown>);
      await loadPayroll();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Folha atualizada com sucesso."
          : "Folha criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar a folha."
            : "Não foi possível criar a folha.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: PayrollEntry) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir folhas.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar a folha para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a folha da competência "${item.competencia || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("rh", "folhaDePagamento", item.id);
      await loadPayroll();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyPayroll });
      }

      setSuccess("Folha excluida com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível excluir a folha."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "payroll-page payroll-page--embedded" : "payroll-page"
      }
    >
      {!embedded ? (
        <header className="payroll-page__header">
          <div>
            <span className="payroll-page__eyebrow">RH</span>
            <h2 className="payroll-page__title">Folha de Pagamento</h2>
            <p className="payroll-page__subtitle">
              Gere e acompanhe a folha do colaborador com cálculo automático de
              horas, bruto e líquido.
            </p>
          </div>

          <div className="payroll-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por colaborador, competência, status ou valores"
              className="payroll-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="payroll-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova folha
            </button>
          </div>
        </header>
      ) : (
        <div className="payroll-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por colaborador, competência, status ou valores"
            className="payroll-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="payroll-page__toolbar-actions">
            <button
              type="button"
              className="payroll-page__ghost"
              onClick={() => void loadPayroll()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="payroll-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova folha
            </button>
          </div>
        </div>
      )}

      {error ? <div className="payroll-page__alert">{error}</div> : null}
      {success ? (
        <div className="payroll-page__alert payroll-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="payroll-page__alert payroll-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="payroll-page__layout">
        <PayrollTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          employeeOptions={employeeOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <PayrollForm
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
