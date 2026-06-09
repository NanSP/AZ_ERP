import type { ClientRecord } from "../../pages/Sd/ClientsPage";
import type { OrderRecord } from "../../pages/Sd/OrdersPage";

type OrderFormProps = {
  value: OrderRecord;
  editing: boolean;
  clients: ClientRecord[];
  canReadClients: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: OrderRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const statusOptions = [
  { value: "aberto", label: "Aberto" },
  { value: "em_andamento", label: "Em andamento" },
  { value: "faturado", label: "Faturado" },
  { value: "cancelado", label: "Cancelado" },
];

export default function OrderForm({
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
}: OrderFormProps) {
  const canSave =
    value.clienteId.trim() !== "" &&
    value.dataPedido.trim() !== "" &&
    value.status.trim() !== "" &&
    (!value.dataEntrega || value.dataEntrega >= value.dataPedido);

  function update<K extends keyof OrderRecord>(
    field: K,
    fieldValue: OrderRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="order-form">
      <div className="order-form__head">
        <div>
          <h3 className="order-form__title">
            {editing ? "Editar pedido" : "Novo pedido"}
          </h3>
          <p className="order-form__subtitle">
            Estruture o pedido comercial com cliente, datas, pagamento e status.
            Os totais serão consolidados pelos itens do pedido.
          </p>
          {editing && value.id ? (
            <p className="order-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="order-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="order-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="order-form__grid">
        <label className="order-form__field order-form__field--span-2">
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

        <label className="order-form__field">
          <span>Número do pedido</span>
          <input
            value={value.numeroPedido}
            onChange={(event) => update("numeroPedido", event.target.value)}
            placeholder="PED-2026-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="order-form__field">
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

        <label className="order-form__field">
          <span>Data do pedido</span>
          <input
            type="date"
            value={value.dataPedido}
            onChange={(event) => update("dataPedido", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="order-form__field">
          <span>Data de entrega</span>
          <input
            type="date"
            value={value.dataEntrega}
            onChange={(event) => update("dataEntrega", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="order-form__field">
          <span>Valor total</span>
          <input value={value.valorTotal} disabled />
        </label>

        <label className="order-form__field">
          <span>Desconto total</span>
          <input value={value.descontoTotal} disabled />
        </label>

        <label className="order-form__field order-form__field--span-2">
          <span>Condições de pagamento</span>
          <input
            value={value.condicoesPagamento}
            onChange={(event) =>
              update("condicoesPagamento", event.target.value)
            }
            placeholder="Boleto 28 dias, 3x sem juros, antecipado"
            disabled={!canEditFields}
          />
        </label>

        <label className="order-form__field order-form__field--span-2">
          <span>Observações</span>
          <textarea
            value={value.observacoes}
            onChange={(event) => update("observacoes", event.target.value)}
            placeholder="Instruções de entrega, premissas comerciais e observações internas"
            disabled={!canEditFields}
            rows={5}
          />
        </label>
      </div>

      <button
        type="button"
        className="order-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar pedido"}
      </button>
    </aside>
  );
}
