import type { ClientRecord } from "../../pages/Sd/ClientsPage";
import type { ContractRecord } from "../../pages/Sd/ContractsPage";

type ContractFormProps = {
  value: ContractRecord;
  editing: boolean;
  clients: ClientRecord[];
  canReadClients: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: ContractRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const statusOptions = [
  { value: "vigente", label: "Vigente" },
  { value: "encerrado", label: "Encerrado" },
  { value: "cancelado", label: "Cancelado" },
];

export default function ContractForm({
  value,
  editing,
  clients,
  canReadClients,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: ContractFormProps) {
  const valorTotal =
    value.valorTotal.trim() === ""
      ? 0
      : Number(value.valorTotal.replace(",", "."));
  const needsEndDate =
    value.status === "encerrado" || value.status === "cancelado";
  const canSave =
    value.clienteId.trim() !== "" &&
    value.numeroContrato.trim() !== "" &&
    value.status.trim() !== "" &&
    !Number.isNaN(valorTotal) &&
    valorTotal >= 0 &&
    (value.status !== "vigente" || value.dataInicio.trim() !== "") &&
    (!needsEndDate || value.dataFim.trim() !== "") &&
    (!value.dataInicio || !value.dataFim || value.dataFim >= value.dataInicio) &&
    (value.status !== "vigente" || value.dataFim.trim() === "");

  function update<K extends keyof ContractRecord>(
    field: K,
    fieldValue: ContractRecord[K],
  ) {
    const nextValue = {
      ...value,
      [field]: fieldValue,
    };

    if (field === "status" && fieldValue === "vigente") {
      nextValue.dataFim = "";
    }

    onChange(nextValue);
  }

  return (
    <aside className="contract-form">
      <div className="contract-form__head">
        <div>
          <h3 className="contract-form__title">
            {editing ? "Editar contrato" : "Novo contrato"}
          </h3>
          <p className="contract-form__subtitle">
            Vincule o contrato a um cliente comercial, defina vigencia,
            numeracao formal e status do acordo.
          </p>
          {editing && value.id ? (
            <p className="contract-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="contract-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="contract-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="contract-form__grid">
        <label className="contract-form__field contract-form__field--span-2">
          <span>Cliente CRM</span>
          {canReadClients ? (
            <select
              value={value.clienteId}
              onChange={(event) => update("clienteId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um cliente</option>
              {clients.map((client) => (
                <option
                  key={client.id ?? client.parceiroId}
                  value={String(client.parceiroId)}
                >
                  Cliente #{client.id} - Parceiro {client.parceiroId}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.clienteId}
              onChange={(event) =>
                update("clienteId", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do parceiro do cliente"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="contract-form__field">
          <span>Numero do contrato</span>
          <input
            value={value.numeroContrato}
            onChange={(event) => update("numeroContrato", event.target.value)}
            placeholder="CTR-2026-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="contract-form__field">
          <span>Status</span>
          <select
            value={value.status}
            onChange={(event) => update("status", event.target.value)}
            disabled={!canEditFields}
          >
            {statusOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="contract-form__field">
          <span>Data de inicio</span>
          <input
            type="date"
            value={value.dataInicio}
            onChange={(event) => update("dataInicio", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="contract-form__field">
          <span>Data de fim</span>
          <input
            type="date"
            value={value.dataFim}
            onChange={(event) => update("dataFim", event.target.value)}
            disabled={!canEditFields || value.status === "vigente"}
          />
        </label>

        <label className="contract-form__field">
          <span>Valor total</span>
          <input
            value={value.valorTotal}
            onChange={(event) =>
              update("valorTotal", event.target.value.replace(/[^0-9,.-]/g, ""))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="contract-form__field contract-form__field--span-2">
          <span>Objeto</span>
          <textarea
            value={value.objeto}
            onChange={(event) => update("objeto", event.target.value)}
            placeholder="Escopo do contrato, modulos, serviços e condições principais"
            disabled={!canEditFields}
            rows={5}
          />
        </label>
      </div>

      <button
        type="button"
        className="contract-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar contrato"}
      </button>
    </aside>
  );
}
