import type { Partner } from "../../pages/Core/PartnersPage";
import type { ClientRecord } from "../../pages/Sd/ClientsPage";

type ClientFormProps = {
  value: ClientRecord;
  editing: boolean;
  partners: Partner[];
  canReadPartners: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: ClientRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const classificationOptions = [
  { value: "lead", label: "Lead" },
  { value: "prospect", label: "Prospect" },
  { value: "cliente", label: "Cliente" },
];

export default function ClientForm({
  value,
  editing,
  partners,
  canReadPartners,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: ClientFormProps) {
  const faturamentoAnual =
    value.faturamentoAnual.trim() === ""
      ? 0
      : Number(value.faturamentoAnual.replace(",", "."));
  const numeroFuncionarios =
    value.numeroFuncionarios.trim() === ""
      ? 0
      : Number(value.numeroFuncionarios);
  const canSave =
    value.parceiroId.trim() !== "" &&
    value.classificacao.trim() !== "" &&
    !Number.isNaN(faturamentoAnual) &&
    faturamentoAnual >= 0 &&
    !Number.isNaN(numeroFuncionarios) &&
    numeroFuncionarios >= 0;

  function update<K extends keyof ClientRecord>(
    field: K,
    fieldValue: ClientRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="client-form">
      <div className="client-form__head">
        <div>
          <h3 className="client-form__title">
            {editing ? "Editar cliente" : "Novo cliente"}
          </h3>
          <p className="client-form__subtitle">
            Converta parceiros em registros comerciais e mantenha a
            classificação do relacionamento com contexto de origem e porte da
            conta.
          </p>
          {editing && value.id ? (
            <p className="client-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="client-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="client-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="client-form__grid">
        <label className="client-form__field client-form__field--span-2">
          <span>Parceiro</span>
          {canReadPartners ? (
            <select
              value={value.parceiroId}
              onChange={(event) => update("parceiroId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um parceiro</option>
              {partners.map((partner) => (
                <option
                  key={partner.id ?? partner.codigo}
                  value={String(partner.id ?? "")}
                >
                  {partner.codigo || "Sem codigo"} -{" "}
                  {partner.nome || "Sem nome"}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.parceiroId}
              onChange={(event) =>
                update("parceiroId", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do parceiro"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="client-form__field">
          <span>Classificação</span>
          <select
            value={value.classificacao}
            onChange={(event) => update("classificacao", event.target.value)}
            disabled={!canEditFields}
          >
            {classificationOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="client-form__field">
          <span>Origem</span>
          <input
            value={value.origem}
            onChange={(event) => update("origem", event.target.value)}
            placeholder="Inbound, indicacao, evento, outbound"
            disabled={!canEditFields}
          />
        </label>

        <label className="client-form__field client-form__field--span-2">
          <span>Website</span>
          <input
            value={value.website}
            onChange={(event) => update("website", event.target.value)}
            placeholder="https://empresa.com.br"
            disabled={!canEditFields}
          />
        </label>

        <label className="client-form__field">
          <span>Faturamento anual</span>
          <input
            value={value.faturamentoAnual}
            onChange={(event) =>
              update(
                "faturamentoAnual",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="client-form__field">
          <span>Número de funcionários</span>
          <input
            value={value.numeroFuncionarios}
            onChange={(event) =>
              update(
                "numeroFuncionarios",
                event.target.value.replace(/\D/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="client-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alterações"
            : "Criar cliente"}
      </button>
    </aside>
  );
}
