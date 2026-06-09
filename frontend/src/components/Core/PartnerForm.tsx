import type { Partner } from "../../pages/Core/PartnersPage";

type PartnerFormProps = {
  value: Partner;
  editing: boolean;
  saving: boolean;
  onChange: (value: Partner) => void;
  onSave: () => void;
  onReset: () => void;
};

const tipoParceiroOptions = [
  { value: "cliente", label: "Cliente" },
  { value: "fornecedor", label: "Fornecedor" },
  { value: "transportadora", label: "Transportadora" },
  { value: "representante", label: "Representante" },
];

const tipoPessoaOptions = [
  { value: "", label: "Nao informado" },
  { value: "F", label: "Pessoa fisica" },
  { value: "J", label: "Pessoa juridica" },
];

const situacaoOptions = [
  { value: "ativo", label: "Ativo" },
  { value: "inativo", label: "Inativo" },
  { value: "bloqueado", label: "Bloqueado" },
];

export default function PartnerForm({
  value,
  editing,
  saving,
  onChange,
  onSave,
  onReset,
}: PartnerFormProps) {
  const canSave = value.nome.trim() !== "";

  function update<K extends keyof Partner>(field: K, fieldValue: Partner[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="partner-form">
      <div className="partner-form__head">
        <div>
          <h3 className="partner-form__title">
            {editing ? "Editar parceiro" : "Novo parceiro"}
          </h3>
          <p className="partner-form__subtitle">
            Preencha os dados centrais do cadastro.
          </p>
          {editing && value.id ? (
            <p className="partner-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="partner-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="partner-form__grid">
        <label className="partner-form__field">
          <span>Tipo de parceiro</span>
          <select
            value={value.tipoParceiro}
            onChange={(event) => update("tipoParceiro", event.target.value)}
          >
            {tipoParceiroOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="partner-form__field">
          <span>Codigo</span>
          <input
            value={value.codigo}
            onChange={(event) => update("codigo", event.target.value)}
            placeholder="Ex.: PAR-001"
          />
        </label>

        <label className="partner-form__field partner-form__field--span-2">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Nome principal do parceiro"
          />
        </label>

        <label className="partner-form__field partner-form__field--span-2">
          <span>Nome fantasia</span>
          <input
            value={value.nomeFantasia}
            onChange={(event) => update("nomeFantasia", event.target.value)}
            placeholder="Nome fantasia ou identificacao comercial"
          />
        </label>

        <label className="partner-form__field">
          <span>Documento</span>
          <input
            value={value.documento}
            onChange={(event) => update("documento", event.target.value)}
            placeholder="CPF ou CNPJ numerico"
          />
        </label>

        <label className="partner-form__field">
          <span>Tipo de pessoa</span>
          <select
            value={value.tipoPessoa}
            onChange={(event) => update("tipoPessoa", event.target.value)}
          >
            {tipoPessoaOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="partner-form__field">
          <span>Situação</span>
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

        <label className="partner-form__field">
          <span>Limite de crédito</span>
          <input
            value={value.limiteCredito}
            onChange={(event) => update("limiteCredito", event.target.value)}
            placeholder="0.00"
          />
        </label>

        <label className="partner-form__field">
          <span>Dias de prazo</span>
          <input
            value={value.diasPrazo}
            onChange={(event) => update("diasPrazo", event.target.value)}
            placeholder="30"
          />
        </label>

        <label className="partner-form__field partner-form__field--span-2">
          <span>Observações</span>
          <textarea
            value={value.observacoes}
            onChange={(event) => update("observacoes", event.target.value)}
            placeholder="Observacoes internas sobre o parceiro"
          />
        </label>
      </div>

      <button
        type="button"
        className="partner-form__button"
        onClick={onSave}
        disabled={saving || !canSave}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar parceiro"}
      </button>
    </aside>
  );
}
