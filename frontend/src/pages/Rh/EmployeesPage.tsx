import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import EmployeeForm from "../../components/Rh/EmployeeForm";
import EmployeeTable from "../../components/Rh/EmployeeTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./employees-page.css";

export type Employee = {
  id?: number;
  codigo: string;
  nome: string;
  cpf: string;
  rg: string;
  dataNascimento: string;
  sexo: string;
  estadoCivil: string;
  nacionalidade: string;
  emailPessoal: string;
  emailCorporativo: string;
  telefone: string;
  celular: string;
  dataAdmissao: string;
  dataDemissao: string;
  cargo: string;
  departamento: string;
  salario: string;
  tipoContrato: string;
  jornadaSemanal: string;
  situacao: string;
  createdAt?: string;
};

type EmployeesPageProps = {
  embedded?: boolean;
};

const emptyEmployee: Employee = {
  codigo: "",
  nome: "",
  cpf: "",
  rg: "",
  dataNascimento: "",
  sexo: "",
  estadoCivil: "",
  nacionalidade: "",
  emailPessoal: "",
  emailCorporativo: "",
  telefone: "",
  celular: "",
  dataAdmissao: "",
  dataDemissao: "",
  cargo: "",
  departamento: "",
  salario: "",
  tipoContrato: "",
  jornadaSemanal: "",
  situacao: "ativo",
};

function normalizeEmployee(data: Record<string, unknown>): Employee {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    codigo: String(data.codigo ?? ""),
    nome: String(data.nome ?? ""),
    cpf: String(data.cpf ?? ""),
    rg: String(data.rg ?? ""),
    dataNascimento: String(data.dataNascimento ?? ""),
    sexo: String(data.sexo ?? ""),
    estadoCivil: String(data.estadoCivil ?? ""),
    nacionalidade: String(data.nacionalidade ?? ""),
    emailPessoal: String(data.emailPessoal ?? ""),
    emailCorporativo: String(data.emailCorporativo ?? ""),
    telefone: String(data.telefone ?? ""),
    celular: String(data.celular ?? ""),
    dataAdmissao: String(data.dataAdmissao ?? ""),
    dataDemissao: String(data.dataDemissao ?? ""),
    cargo: String(data.cargo ?? ""),
    departamento: String(data.departamento ?? ""),
    salario: data.salario == null ? "" : String(data.salario),
    tipoContrato: String(data.tipoContrato ?? ""),
    jornadaSemanal:
      data.jornadaSemanal == null ? "" : String(data.jornadaSemanal),
    situacao: String(data.situacao ?? "ativo"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(employee: Employee) {
  return {
    codigo: employee.codigo.trim() || null,
    nome: employee.nome.trim() || null,
    cpf: employee.cpf.replace(/\D/g, "") || null,
    rg: employee.rg.trim() || null,
    dataNascimento: employee.dataNascimento.trim() || null,
    sexo: employee.sexo.trim() || null,
    estadoCivil: employee.estadoCivil.trim() || null,
    nacionalidade: employee.nacionalidade.trim() || null,
    emailPessoal: employee.emailPessoal.trim() || null,
    emailCorporativo: employee.emailCorporativo.trim() || null,
    telefone: employee.telefone.trim() || null,
    celular: employee.celular.trim() || null,
    dataAdmissao: employee.dataAdmissao.trim() || null,
    dataDemissao: employee.dataDemissao.trim() || null,
    cargo: employee.cargo.trim() || null,
    departamento: employee.departamento.trim() || null,
    salario:
      employee.salario.trim() === ""
        ? null
        : Number(employee.salario.replace(",", ".")),
    tipoContrato: employee.tipoContrato.trim() || null,
    jornadaSemanal:
      employee.jornadaSemanal.trim() === ""
        ? null
        : Number(employee.jornadaSemanal),
    situacao: employee.situacao.trim() || null,
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

export default function EmployeesPage({
  embedded = false,
}: EmployeesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Employee | null>(null);
  const [draft, setDraft] = useState<Employee>(emptyEmployee);
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead = isMasterScope || permissionSet.has("rh:colaboradores:read");
  const canCreate =
    isMasterScope || permissionSet.has("rh:colaboradores:create");
  const canUpdate =
    isMasterScope || permissionSet.has("rh:colaboradores:update");
  const canDelete =
    isMasterScope || permissionSet.has("rh:colaboradores:delete");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadEmployees() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyEmployee });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("rh", "colaboradores");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeEmployee(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar os colaboradores."),
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadEmployees();
  }, [canRead]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.codigo,
        item.nome,
        item.cpf,
        item.emailCorporativo,
        item.cargo,
        item.departamento,
        item.situacao,
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
    setDraft({ ...emptyEmployee });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Employee) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Employee) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar colaboradores."
          : "Seu perfil nao possui permissao para criar colaboradores.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("rh", "colaboradores", selected.id, payload)
        : await createResource("rh", "colaboradores", payload);

      const saved = normalizeEmployee(response.data as Record<string, unknown>);
      await loadEmployees();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Colaborador atualizado com sucesso."
          : "Colaborador criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o colaborador."
            : "Nao foi possivel criar o colaborador.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Employee) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir colaboradores.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o colaborador para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o colaborador "${item.nome || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("rh", "colaboradores", item.id);
      await loadEmployees();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyEmployee });
      }

      setSuccess("Colaborador excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir o colaborador."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "employees-page employees-page--embedded" : "employees-page"
      }
    >
      {!embedded ? (
        <header className="employees-page__header">
          <div>
            <span className="employees-page__eyebrow">RH</span>
            <h2 className="employees-page__title">Colaboradores</h2>
            <p className="employees-page__subtitle">
              Gerencie o cadastro funcional de pessoas com dados pessoais,
              contratuais e situacao atual.
            </p>
          </div>

          <div className="employees-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por nome, CPF, cargo, departamento ou situacao"
              className="employees-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="employees-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo colaborador
            </button>
          </div>
        </header>
      ) : (
        <div className="employees-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por nome, CPF, cargo, departamento ou situacao"
            className="employees-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="employees-page__toolbar-actions">
            <button
              type="button"
              className="employees-page__ghost"
              onClick={() => void loadEmployees()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="employees-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo colaborador
            </button>
          </div>
        </div>
      )}

      {error ? <div className="employees-page__alert">{error}</div> : null}
      {success ? (
        <div className="employees-page__alert employees-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="employees-page__alert employees-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="employees-page__layout">
        <EmployeeTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <EmployeeForm
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
