import type { Company } from "../../pages/Core/CompaniesPage";

type CompanyFormProps = {
  value: Company;
  editing: boolean;
  saving: boolean;
  onChange: (value: Company) => void;
  onSave: () => void;
  onReset: () => void;
};

const regimeOptions = [
  { value: "", label: "Nao informado" },
  { value: "SIMPLES_NACIONAL", label: "Simples Nacional" },
  { value: "LUCRO_PRESUMIDO", label: "Lucro Presumido" },
  { value: "LUCRO_REAL", label: "Lucro Real" },
];

const situacaoOptions = [
  { value: "ativo", label: "Ativa" },
  { value: "inativo", label: "Inativa" },
  { value: "bloqueado", label: "Bloqueada" },
];

export default function CompanyForm({
  value,
  editing,
  saving,
  onChange,
  onSave,
  onReset,
}: CompanyFormProps) {
  function update<K extends keyof Company>(field: K, fieldValue: Company[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="company-form">
      <div className="company-form__head">
        <div>
          <h3 className="company-form__title">
            {editing ? "Editar empresa" : "Nova empresa"}
          </h3>
          <p className="company-form__subtitle">
            Preencha os dados institucionais principais.
          </p>
        </div>

        <button
          type="button"
          className="company-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="company-form__grid">
        <label className="company-form__field">
          <span>Codigo</span>
          <input
            value={value.codigo}
            onChange={(event) => update("codigo", event.target.value)}
            placeholder="Ex.: EMP-001"
          />
        </label>

        <label className="company-form__field">
          <span>CNPJ</span>
          <input
            value={value.cnpj}
            onChange={(event) => update("cnpj", event.target.value)}
            placeholder="Somente numeros"
          />
        </label>

        <label className="company-form__field company-form__field--span-2">
          <span>Razao social</span>
          <input
            value={value.razaoSocial}
            onChange={(event) => update("razaoSocial", event.target.value)}
            placeholder="Razao social da empresa"
          />
        </label>

        <label className="company-form__field company-form__field--span-2">
          <span>Nome fantasia</span>
          <input
            value={value.nomeFantasia}
            onChange={(event) => update("nomeFantasia", event.target.value)}
            placeholder="Nome comercial ou fantasia"
          />
        </label>

        <label className="company-form__field">
          <span>Inscricao estadual</span>
          <input
            value={value.inscricaoEstadual}
            onChange={(event) =>
              update("inscricaoEstadual", event.target.value)
            }
            placeholder="Inscricao estadual"
          />
        </label>

        <label className="company-form__field">
          <span>Inscricao municipal</span>
          <input
            value={value.inscricaoMunicipal}
            onChange={(event) =>
              update("inscricaoMunicipal", event.target.value)
            }
            placeholder="Inscricao municipal"
          />
        </label>

        <label className="company-form__field">
          <span>Regime tributario</span>
          <select
            value={value.regimeTributario}
            onChange={(event) =>
              update("regimeTributario", event.target.value)
            }
          >
            {regimeOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="company-form__field">
          <span>Data de fundacao</span>
          <input
            type="date"
            value={value.dataFundacao}
            onChange={(event) => update("dataFundacao", event.target.value)}
          />
        </label>

        <label className="company-form__field company-form__field--span-2">
          <span>Situacao</span>
          <select
            value={value.situacao}
            onChange={(event) => update("situacao", event.target.value)}
          >
            {situacaoOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>
      </div>

      <button
        type="button"
        className="company-form__button"
        onClick={onSave}
        disabled={saving}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar empresa"}
      </button>
    </aside>
  );
}
