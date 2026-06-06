import type {
  Address,
  AddressRelatedEntityOption,
} from "../../pages/Core/AddressesPage";

type AddressFormProps = {
  value: Address;
  editing: boolean;
  saving: boolean;
  relatedEntityOptions: AddressRelatedEntityOption[];
  onChange: (value: Address) => void;
  onSave: () => void;
  onReset: () => void;
};

const entidadeOptions = [
  { value: "empresa", label: "Empresa" },
  { value: "parceiro", label: "Parceiro" },
  { value: "colaborador", label: "Colaborador" },
];

const tipoEnderecoOptions = [
  { value: "comercial", label: "Comercial" },
  { value: "cobranca", label: "Cobranca" },
  { value: "entrega", label: "Entrega" },
  { value: "residencial", label: "Residencial" },
];

export default function AddressForm({
  value,
  editing,
  saving,
  relatedEntityOptions,
  onChange,
  onSave,
  onReset,
}: AddressFormProps) {
  const canSave =
    value.entidadeTipo.trim() !== "" &&
    value.entidadeId.trim() !== "" &&
    (value.uf.trim() === "" || value.uf.trim().length === 2) &&
    (value.cep.trim() === "" || value.cep.replace(/\D/g, "").length === 8);

  function update<K extends keyof Address>(field: K, fieldValue: Address[K]) {
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
    <aside className="address-form">
      <div className="address-form__head">
        <div>
          <h3 className="address-form__title">
            {editing ? "Editar endereco" : "Novo endereco"}
          </h3>
          <p className="address-form__subtitle">
            Defina o vinculo da entidade e os dados principais do endereco.
          </p>
          {editing && value.id ? (
            <p className="address-form__meta">
              Registro selecionado: #{value.id}. O vinculo da entidade fica travado na edicao.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="address-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="address-form__grid">
        <label className="address-form__field">
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
          <label className="address-form__field">
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
          <label className="address-form__field">
            <span>ID da entidade</span>
            <input
              value={value.entidadeId}
              onChange={(event) => update("entidadeId", event.target.value)}
              placeholder="Informe o ID do colaborador"
              disabled={editing}
            />
          </label>
        )}

        <label className="address-form__field">
          <span>Tipo de endereco</span>
          <select
            value={value.tipoEndereco}
            onChange={(event) => update("tipoEndereco", event.target.value)}
          >
            {tipoEnderecoOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="address-form__field">
          <span>CEP</span>
          <input
            value={value.cep}
            onChange={(event) =>
              update("cep", event.target.value.replace(/\D/g, ""))
            }
            placeholder="Somente numeros"
          />
        </label>

        <label className="address-form__field address-form__field--span-2">
          <span>Logradouro</span>
          <input
            value={value.logradouro}
            onChange={(event) => update("logradouro", event.target.value)}
            placeholder="Rua, avenida, alameda..."
          />
        </label>

        <label className="address-form__field">
          <span>Numero</span>
          <input
            value={value.numero}
            onChange={(event) => update("numero", event.target.value)}
            placeholder="Numero"
          />
        </label>

        <label className="address-form__field">
          <span>Complemento</span>
          <input
            value={value.complemento}
            onChange={(event) => update("complemento", event.target.value)}
            placeholder="Sala, andar, bloco..."
          />
        </label>

        <label className="address-form__field">
          <span>Bairro</span>
          <input
            value={value.bairro}
            onChange={(event) => update("bairro", event.target.value)}
            placeholder="Bairro"
          />
        </label>

        <label className="address-form__field">
          <span>Cidade</span>
          <input
            value={value.cidade}
            onChange={(event) => update("cidade", event.target.value)}
            placeholder="Cidade"
          />
        </label>

        <label className="address-form__field">
          <span>UF</span>
          <input
            value={value.uf}
            onChange={(event) => update("uf", event.target.value.toUpperCase())}
            placeholder="UF"
            maxLength={2}
          />
        </label>

        <label className="address-form__field">
          <span>Pais</span>
          <input
            value={value.pais}
            onChange={(event) => update("pais", event.target.value.toUpperCase())}
            placeholder="BRASIL"
          />
        </label>

        <label className="address-form__checkbox address-form__field--span-2">
          <input
            type="checkbox"
            checked={value.principal}
            onChange={(event) => update("principal", event.target.checked)}
          />
          <span>Marcar como endereco principal</span>
        </label>
      </div>

      <button
        type="button"
        className="address-form__button"
        onClick={onSave}
        disabled={saving || !canSave}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar endereco"}
      </button>
    </aside>
  );
}
