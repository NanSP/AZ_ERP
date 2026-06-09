import type {
  Contact,
  RelatedEntityOption,
} from "../../pages/Core/ContactsPage";

type ContactFormProps = {
  value: Contact;
  editing: boolean;
  saving: boolean;
  relatedEntityOptions: RelatedEntityOption[];
  onChange: (value: Contact) => void;
  onSave: () => void;
  onReset: () => void;
};

const entidadeOptions = [
  { value: "empresa", label: "Empresa" },
  { value: "parceiro", label: "Parceiro" },
  { value: "colaborador", label: "Colaborador" },
];

const tipoContatoOptions = [
  { value: "email", label: "E-mail" },
  { value: "telefone", label: "Telefone" },
  { value: "whatsapp", label: "WhatsApp" },
  { value: "site", label: "Site" },
];

export default function ContactForm({
  value,
  editing,
  saving,
  relatedEntityOptions,
  onChange,
  onSave,
  onReset,
}: ContactFormProps) {
  const canSave =
    value.entidadeTipo.trim() !== "" &&
    value.entidadeId.trim() !== "" &&
    value.tipoContato.trim() !== "" &&
    value.valor.trim() !== "";

  function update<K extends keyof Contact>(field: K, fieldValue: Contact[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  function updateEntidadeTipo(nextValue: string) {
    onChange({
      ...value,
      entidadeTipo: nextValue,
      entidadeId: "",
    });
  }

  const showEntitySelect =
    value.entidadeTipo === "empresa" || value.entidadeTipo === "parceiro";
  const useEntitySelect = showEntitySelect && relatedEntityOptions.length > 0;

  return (
    <aside className="contact-form">
      <div className="contact-form__head">
        <div>
          <h3 className="contact-form__title">
            {editing ? "Editar contato" : "Novo contato"}
          </h3>
          <p className="contact-form__subtitle">
            Defina o vinculo da entidade e o canal principal de contato.
          </p>
          {editing && value.id ? (
            <p className="contact-form__meta">
              Registro selecionado: #{value.id}. O vinculo da entidade fica
              travado na edição.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="contact-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="contact-form__grid">
        <label className="contact-form__field">
          <span>Tipo de entidade</span>
          <select
            value={value.entidadeTipo}
            onChange={(event) => updateEntidadeTipo(event.target.value)}
            disabled={editing}
          >
            {entidadeOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        {useEntitySelect ? (
          <label className="contact-form__field">
            <span>Entidade vinculada</span>
            <select
              value={value.entidadeId}
              onChange={(event) => update("entidadeId", event.target.value)}
              disabled={editing}
            >
              <option value="">Selecione</option>
              {relatedEntityOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </label>
        ) : (
          <label className="contact-form__field">
            <span>
              {showEntitySelect ? "ID da entidade" : "ID da entidade"}
            </span>
            <input
              value={value.entidadeId}
              onChange={(event) => update("entidadeId", event.target.value)}
              placeholder={
                value.entidadeTipo === "colaborador"
                  ? "Informe o ID do colaborador"
                  : "Informe o ID da entidade"
              }
              disabled={editing}
            />
          </label>
        )}

        <label className="contact-form__field">
          <span>Tipo de contato</span>
          <select
            value={value.tipoContato}
            onChange={(event) => update("tipoContato", event.target.value)}
          >
            {tipoContatoOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="contact-form__field">
          <span>Valor</span>
          <input
            value={value.valor}
            onChange={(event) => update("valor", event.target.value)}
            placeholder="contato@empresa.com ou https://site.com"
          />
        </label>

        <label className="contact-form__field contact-form__field--span-2">
          <span>Observação</span>
          <input
            value={value.observacao}
            onChange={(event) => update("observacao", event.target.value)}
            placeholder="Ex.: contato financeiro, comercial ou suporte"
          />
        </label>

        <label className="contact-form__checkbox contact-form__field--span-2">
          <input
            type="checkbox"
            checked={value.principal}
            onChange={(event) => update("principal", event.target.checked)}
          />
          <span>Marcar como contato principal</span>
        </label>
      </div>

      <button
        type="button"
        className="contact-form__button"
        onClick={onSave}
        disabled={saving || !canSave}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alterações"
            : "Criar contato"}
      </button>
    </aside>
  );
}
