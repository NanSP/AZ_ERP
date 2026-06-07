import type { MrpItem, ProductAccess, ProductOption } from "../../pages/Pp/MrpPage";

type MrpFormProps = {
  value: MrpItem;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  productOptions: ProductOption[];
  productAccess: ProductAccess;
  onChange: (value: MrpItem) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function MrpForm({
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
}: MrpFormProps) {
  const demand =
    value.demandaPrevista.trim() === ""
      ? 0
      : Number(value.demandaPrevista.replace(",", "."));
  const currentStock =
    value.estoqueAtual.trim() === ""
      ? 0
      : Number(value.estoqueAtual.replace(",", "."));
  const safetyStock =
    value.estoqueSeguranca.trim() === ""
      ? 0
      : Number(value.estoqueSeguranca.replace(",", "."));

  const canSave =
    value.produto.trim() !== "" &&
    value.periodo.trim() !== "" &&
    !Number.isNaN(demand) &&
    demand >= 0 &&
    !Number.isNaN(currentStock) &&
    currentStock >= 0 &&
    !Number.isNaN(safetyStock) &&
    safetyStock >= 0 &&
    (!value.dataNecessidade || value.dataNecessidade >= value.periodo);

  function update<K extends keyof MrpItem>(field: K, fieldValue: MrpItem[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="mrp-form">
      <div className="mrp-form__head">
        <div>
          <h3 className="mrp-form__title">
            {editing ? "Editar MRP" : "Novo MRP"}
          </h3>
          <p className="mrp-form__subtitle">
            Informe o cenario de demanda e estoque. O backend calcula as
            necessidades automaticamente.
          </p>
          {editing && value.id ? (
            <p className="mrp-form__meta">Registro selecionado: #{value.id}</p>
          ) : !canEditFields ? (
            <p className="mrp-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="mrp-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="mrp-form__grid">
        <label className="mrp-form__field">
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
            <small className="mrp-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="mrp-form__field">
          <span>Periodo</span>
          <input
            type="date"
            value={value.periodo}
            onChange={(event) => update("periodo", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="mrp-form__field">
          <span>Demanda prevista</span>
          <input
            value={value.demandaPrevista}
            onChange={(event) =>
              update(
                "demandaPrevista",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="mrp-form__field">
          <span>Estoque atual</span>
          <input
            value={value.estoqueAtual}
            onChange={(event) =>
              update(
                "estoqueAtual",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="mrp-form__field">
          <span>Estoque de seguranca</span>
          <input
            value={value.estoqueSeguranca}
            onChange={(event) =>
              update(
                "estoqueSeguranca",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="mrp-form__field">
          <span>Data de necessidade</span>
          <input
            type="date"
            value={value.dataNecessidade}
            onChange={(event) => update("dataNecessidade", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <div className="mrp-form__summary mrp-form__summary--span-2">
          <div className="mrp-form__summary-card">
            <span>Necessidade de compra</span>
            <strong>{value.necessidadeCompra || "-"}</strong>
          </div>
          <div className="mrp-form__summary-card">
            <span>Necessidade de producao</span>
            <strong>{value.necessidadeProducao || "-"}</strong>
          </div>
        </div>
      </div>

      <button
        type="button"
        className="mrp-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar MRP"}
      </button>
    </aside>
  );
}
