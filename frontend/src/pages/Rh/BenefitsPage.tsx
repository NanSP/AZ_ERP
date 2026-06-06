import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import BenefitForm from "../../components/Rh/BenefitForm";
import BenefitTable from "../../components/Rh/BenefitTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./benefits-page.css";

export type EmployeeOption = {
  id: number;
  label: string;
};

export type EmployeeAccess = "idle" | "loaded" | "unavailable";

export type Benefit = {
  id?: number;
  colaborador: string;
  tipoBeneficio: string;
  valor: string;
  dataInicio: string;
  dataFim: string;
  ativo: boolean;
};

type BenefitsPageProps = {
  embedded?: boolean;
};

const emptyBenefit: Benefit = {
  colaborador: "",
  tipoBeneficio: "",
  valor: "",
  dataInicio: "",
  dataFim: "",
  ativo: true,
};

function normalizeBenefit(data: Record<string, unknown>): Benefit {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    colaborador: data.colaborador == null ? "" : String(data.colaborador),
    tipoBeneficio: String(data.tipoBeneficio ?? ""),
    valor: data.valor == null ? "" : String(data.valor),
    dataInicio: String(data.dataInicio ?? ""),
    dataFim: String(data.dataFim ?? ""),
    ativo: data.ativo == null ? true : Boolean(data.ativo),
  };
}

function toRequestPayload(benefit: Benefit) {
  return {
    colaborador:
      benefit.colaborador.trim() === "" ? null : Number(benefit.colaborador),
    tipoBeneficio: benefit.tipoBeneficio.trim() || null,
    valor:
      benefit.valor.trim() === ""
        ? null
        : Number(benefit.valor.replace(",", ".")),
    dataInicio: benefit.dataInicio.trim() || null,
    dataFim: benefit.dataFim.trim() || null,
    ativo: benefit.ativo,
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

export default function BenefitsPage({ embedded = false }: BenefitsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<Benefit[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Benefit | null>(null);
  const [draft, setDraft] = useState<Benefit>(emptyBenefit);
  const [employeeOptions, setEmployeeOptions] = useState<EmployeeOption[]>([]);
  const [employeeAccess, setEmployeeAccess] = useState<EmployeeAccess>("idle");
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("rh:beneficios:read");
  const canCreate = isMasterScope || permissionSet.has("rh:beneficios:create");
  const canUpdate = isMasterScope || permissionSet.has("rh:beneficios:update");
  const canDelete = isMasterScope || permissionSet.has("rh:beneficios:delete");
  const canReadEmployees =
    isMasterScope || permissionSet.has("rh:colaboradores:read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadBenefits() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyBenefit });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("rh", "beneficios");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeBenefit(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar os beneficios."));
    } finally {
      setLoading(false);
    }
  }

  async function loadEmployees() {
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
  }

  useEffect(() => {
    void loadBenefits();
  }, [canRead]);

  useEffect(() => {
    void loadEmployees();
  }, [canReadEmployees]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.colaborador, item.tipoBeneficio, item.valor, item.dataInicio, item.dataFim]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyBenefit });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Benefit) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Benefit) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar beneficios."
          : "Seu perfil nao possui permissao para criar beneficios.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("rh", "beneficios", selected.id, payload)
        : await createResource("rh", "beneficios", payload);

      const saved = normalizeBenefit(response.data as Record<string, unknown>);
      await loadBenefits();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Beneficio atualizado com sucesso."
          : "Beneficio criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o beneficio."
            : "Nao foi possivel criar o beneficio.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Benefit) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir beneficios.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o beneficio para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o beneficio "${item.tipoBeneficio || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("rh", "beneficios", item.id);
      await loadBenefits();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyBenefit });
      }

      setSuccess("Beneficio excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o beneficio."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "benefits-page benefits-page--embedded" : "benefits-page"
      }
    >
      {!embedded ? (
        <header className="benefits-page__header">
          <div>
            <span className="benefits-page__eyebrow">RH</span>
            <h2 className="benefits-page__title">Beneficios</h2>
            <p className="benefits-page__subtitle">
              Gerencie beneficios corporativos por colaborador com vigencia,
              valor e controle de status.
            </p>
          </div>

          <div className="benefits-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por colaborador, tipo, valor ou vigencia"
              className="benefits-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="benefits-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo beneficio
            </button>
          </div>
        </header>
      ) : (
        <div className="benefits-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por colaborador, tipo, valor ou vigencia"
            className="benefits-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="benefits-page__toolbar-actions">
            <button
              type="button"
              className="benefits-page__ghost"
              onClick={() => void loadBenefits()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="benefits-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo beneficio
            </button>
          </div>
        </div>
      )}

      {error ? <div className="benefits-page__alert">{error}</div> : null}
      {success ? (
        <div className="benefits-page__alert benefits-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="benefits-page__alert benefits-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="benefits-page__layout">
        <BenefitTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          employeeOptions={employeeOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <BenefitForm
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
