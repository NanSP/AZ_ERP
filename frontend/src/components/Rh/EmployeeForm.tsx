import type { Employee } from "../../pages/Rh/EmployeesPage";

type EmployeeFormProps = {
  value: Employee;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: Employee) => void;
  onSave: () => void;
  onReset: () => void;
};

const genderOptions = [
  { value: "", label: "Nao informar" },
  { value: "m", label: "Masculino" },
  { value: "f", label: "Feminino" },
];

const statusOptions = ["ativo", "inativo", "desligado"];

export default function EmployeeForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: EmployeeFormProps) {
  const canSave = value.nome.trim() !== "" && value.cpf.replace(/\D/g, "").length === 11;

  function update<K extends keyof Employee>(field: K, fieldValue: Employee[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  function renderField(
    label: string,
    field: keyof Employee,
    currentValue: string,
    placeholder: string,
    type = "text",
  ) {
    return (
      <label className="employee-form__field">
        <span>{label}</span>
        <input
          type={type}
          value={currentValue}
          onChange={(event) => update(field, event.target.value)}
          placeholder={placeholder}
          disabled={!canEditFields}
        />
      </label>
    );
  }

  return (
    <aside className="employee-form">
      <div className="employee-form__head">
        <div>
          <h3 className="employee-form__title">
            {editing ? "Editar colaborador" : "Novo colaborador"}
          </h3>
          <p className="employee-form__subtitle">
            Preencha os dados pessoais, funcionais e contratuais do cadastro.
          </p>
          {editing && value.id ? (
            <p className="employee-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="employee-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="employee-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="employee-form__grid">
        {renderField("Codigo", "codigo", value.codigo, "COL-001")}
        {renderField("Nome", "nome", value.nome, "Nome completo")}

        <label className="employee-form__field">
          <span>CPF</span>
          <input
            value={value.cpf}
            onChange={(event) =>
              update("cpf", event.target.value.replace(/\D/g, "").slice(0, 11))
            }
            placeholder="Somente numeros"
            disabled={!canEditFields}
          />
        </label>

        {renderField("RG", "rg", value.rg, "Documento de identidade")}
        {renderField(
          "Data de nascimento",
          "dataNascimento",
          value.dataNascimento,
          "",
          "date",
        )}

        <label className="employee-form__field">
          <span>Sexo</span>
          <select
            value={value.sexo}
            onChange={(event) => update("sexo", event.target.value)}
            disabled={!canEditFields}
          >
            {genderOptions.map((option) => (
              <option key={option.value || "empty"} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        {renderField(
          "Estado civil",
          "estadoCivil",
          value.estadoCivil,
          "Solteiro, casado..."
        )}
        {renderField(
          "Nacionalidade",
          "nacionalidade",
          value.nacionalidade,
          "Brasileira"
        )}
        {renderField(
          "Email pessoal",
          "emailPessoal",
          value.emailPessoal,
          "email@pessoal.com",
          "email",
        )}
        {renderField(
          "Email corporativo",
          "emailCorporativo",
          value.emailCorporativo,
          "email@empresa.com",
          "email",
        )}

        <label className="employee-form__field">
          <span>Telefone</span>
          <input
            value={value.telefone}
            onChange={(event) =>
              update("telefone", event.target.value.replace(/[^\d()+\- ]/g, ""))
            }
            placeholder="Telefone"
            disabled={!canEditFields}
          />
        </label>

        <label className="employee-form__field">
          <span>Celular</span>
          <input
            value={value.celular}
            onChange={(event) =>
              update("celular", event.target.value.replace(/[^\d()+\- ]/g, ""))
            }
            placeholder="Celular"
            disabled={!canEditFields}
          />
        </label>

        {renderField(
          "Data de admissao",
          "dataAdmissao",
          value.dataAdmissao,
          "",
          "date",
        )}
        {renderField(
          "Data de demissao",
          "dataDemissao",
          value.dataDemissao,
          "",
          "date",
        )}
        {renderField("Cargo", "cargo", value.cargo, "Cargo atual")}
        {renderField(
          "Departamento",
          "departamento",
          value.departamento,
          "Departamento"
        )}

        <label className="employee-form__field">
          <span>Salario</span>
          <input
            value={value.salario}
            onChange={(event) =>
              update("salario", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        {renderField(
          "Tipo de contrato",
          "tipoContrato",
          value.tipoContrato,
          "CLT, PJ, estagio..."
        )}

        <label className="employee-form__field">
          <span>Jornada semanal</span>
          <input
            value={value.jornadaSemanal}
            onChange={(event) =>
              update(
                "jornadaSemanal",
                event.target.value.replace(/\D/g, "").slice(0, 3),
              )
            }
            placeholder="44"
            disabled={!canEditFields}
          />
        </label>

        <label className="employee-form__field">
          <span>Situacao</span>
          <select
            value={value.situacao}
            onChange={(event) => update("situacao", event.target.value)}
            disabled={!canEditFields}
          >
            {statusOptions.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
        </label>
      </div>

      <button
        type="button"
        className="employee-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar colaborador"}
      </button>
    </aside>
  );
}
