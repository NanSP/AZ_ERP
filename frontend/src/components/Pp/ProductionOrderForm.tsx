import type {
  ProductAccess,
  ProductOption,
  ProductionOrder,
} from "../../pages/Pp/ProductionOrdersPage";

type ProductionOrderFormProps = {
  value: ProductionOrder;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  productOptions: ProductOption[];
  productAccess: ProductAccess;
  onChange: (value: ProductionOrder) => void;
  onSave: () => void;
  onReset: () => void;
};

const statusOptions = [
  { value: "planejada", label: "Planejada" },
  { value: "em_producao", label: "Em producao" },
  { value: "concluida", label: "Concluida" },
  { value: "cancelada", label: "Cancelada" },
];

const priorityOptions = [
  { value: "1", label: "1 - Baixa" },
  { value: "2", label: "2" },
  { value: "3", label: "3 - Media" },
  { value: "4", label: "4" },
  { value: "5", label: "5 - Critica" },
];

export default function ProductionOrderForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  productOptions,
  productAccess,
  onChange,
  onSave,
  onReset,
}: ProductionOrderFormProps) {
  const status = value.status.trim();
  const planned = Number(value.quantidadePlanejada.replace(",", "."));
  const produced = Number(value.quantidadeProduzida.replace(",", "."));
  const hasPlanned =
    value.quantidadePlanejada.trim() !== "" && !Number.isNaN(planned);
  const hasProduced =
    value.quantidadeProduzida.trim() !== "" && !Number.isNaN(produced);

  const canSave =
    value.produto.trim() !== "" &&
    value.dataEmissao.trim() !== "" &&
    hasPlanned &&
    planned > 0 &&
    (!hasProduced || produced >= 0) &&
    (!hasProduced || produced <= planned) &&
    (status !== "planejada" || !hasProduced || produced === 0) &&
    (status !== "em_producao" ||
      (hasProduced && produced > 0 && produced < planned)) &&
    (status !== "concluida" || (hasProduced && produced === planned));

  function update<K extends keyof ProductionOrder>(
    field: K,
    fieldValue: ProductionOrder[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  function handleStatusChange(nextStatus: string) {
    const nextValue: ProductionOrder = {
      ...value,
      status: nextStatus,
    };

    if (nextStatus === "planejada" && value.quantidadeProduzida.trim() !== "") {
      nextValue.quantidadeProduzida = "0";
    }

    onChange(nextValue);
  }

  return (
    <aside className="production-order-form">
      <div className="production-order-form__head">
        <div>
          <h3 className="production-order-form__title">
            {editing ? "Editar ordem" : "Nova ordem"}
          </h3>
          <p className="production-order-form__subtitle">
            Controle produto, quantidade, prazo e fase produtiva da ordem.
          </p>
          {editing && value.id ? (
            <p className="production-order-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="production-order-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="production-order-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="production-order-form__grid">
        <label className="production-order-form__field">
          <span>Numero OP</span>
          <input
            value={value.numeroOp}
            onChange={(event) => update("numeroOp", event.target.value)}
            placeholder="OP-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="production-order-form__field">
          <span>Produto</span>
          {productOptions.length > 0 ? (
            <select
              value={value.produto}
              onChange={(event) => update("produto", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
              {productOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.produto}
              onChange={(event) =>
                update("produto", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do produto"
              disabled={!canEditFields}
            />
          )}
          {productAccess === "unavailable" ? (
            <small className="production-order-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="production-order-form__field">
          <span>Quantidade planejada</span>
          <input
            value={value.quantidadePlanejada}
            onChange={(event) =>
              update(
                "quantidadePlanejada",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="100"
            disabled={!canEditFields}
          />
        </label>

        <label className="production-order-form__field">
          <span>Quantidade produzida</span>
          <input
            value={value.quantidadeProduzida}
            onChange={(event) =>
              update(
                "quantidadeProduzida",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields || value.status === "planejada"}
          />
        </label>

        <label className="production-order-form__field">
          <span>Data de emissão</span>
          <input
            type="date"
            value={value.dataEmissao}
            onChange={(event) => update("dataEmissao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="production-order-form__field">
          <span>Data prevista</span>
          <input
            type="date"
            value={value.dataPrevista}
            onChange={(event) => update("dataPrevista", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="production-order-form__field">
          <span>Inicio</span>
          <input
            type="date"
            value={value.dataInicio}
            onChange={(event) => update("dataInicio", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="production-order-form__field">
          <span>Fim</span>
          <input
            type="date"
            value={value.dataFim}
            onChange={(event) => update("dataFim", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="production-order-form__field">
          <span>Status</span>
          <select
            value={value.status}
            onChange={(event) => handleStatusChange(event.target.value)}
            disabled={!canEditFields}
          >
            {statusOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="production-order-form__field">
          <span>Prioridade</span>
          <select
            value={value.prioridade}
            onChange={(event) => update("prioridade", event.target.value)}
            disabled={!canEditFields}
          >
            {priorityOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="production-order-form__field production-order-form__field--span-2">
          <span>Observações</span>
          <textarea
            value={value.observacoes}
            onChange={(event) => update("observacoes", event.target.value)}
            placeholder="Contexto, setup, lote ou observacoes operacionais"
            disabled={!canEditFields}
            rows={4}
          />
        </label>
      </div>

      <button
        type="button"
        className="production-order-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving ? "Salvando..." : editing ? "Salvar alteracoes" : "Criar ordem"}
      </button>
    </aside>
  );
}
