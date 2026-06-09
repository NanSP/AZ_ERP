import type { User } from "../../pages/Sys/UsersPage";
import type { StockRecord } from "../../pages/Mm/StocksPage";
import type { MovementRecord } from "../../pages/Mm/MovementsPage";

type MovementFormProps = {
  value: MovementRecord;
  editing: boolean;
  stocks: StockRecord[];
  users: User[];
  canReadStocks: boolean;
  canReadUsers: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: MovementRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const tipoMovimentoOptions = [
  { value: "entrada", label: "Entrada" },
  { value: "saida", label: "Saida" },
  { value: "transferencia", label: "Transferencia" },
  { value: "ajuste", label: "Ajuste" },
  { value: "inventario", label: "Inventario" },
];

export default function MovementForm({
  value,
  editing,
  stocks,
  users,
  canReadStocks,
  canReadUsers,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: MovementFormProps) {
  const quantidade = Number((value.quantidade || "").replace(",", "."));
  const valorUnitario =
    value.valorUnitario.trim() === ""
      ? 0
      : Number(value.valorUnitario.replace(",", "."));
  const canSave =
    value.estoqueId.trim() !== "" &&
    value.usuarioId.trim() !== "" &&
    value.tipoMovimento.trim() !== "" &&
    !Number.isNaN(quantidade) &&
    quantidade > 0 &&
    !Number.isNaN(valorUnitario) &&
    valorUnitario >= 0;

  function update<K extends keyof MovementRecord>(
    field: K,
    fieldValue: MovementRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  function normalizeDecimalInput(nextValue: string) {
    return nextValue.replace(/[^0-9,.-]/g, "");
  }

  return (
    <aside className="movement-form">
      <div className="movement-form__head">
        <div>
          <h3 className="movement-form__title">
            {editing ? "Editar movimentacao" : "Nova movimentacao"}
          </h3>
          <p className="movement-form__subtitle">
            Escolha o estoque, tipo de movimento, quantidade e contexto
            operacional.
          </p>
          {editing && value.id ? (
            <p className="movement-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="movement-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="movement-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="movement-form__grid">
        <label className="movement-form__field">
          <span>Estoque</span>
          {canReadStocks ? (
            <select
              value={value.estoqueId}
              onChange={(event) => update("estoqueId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um estoque</option>
              {stocks.map((stock) => (
                <option
                  key={stock.id ?? `${stock.produtoId}-${stock.empresaId}`}
                  value={String(stock.id ?? "")}
                >
                  #{stock.id} - Produto {stock.produtoId} / Empresa{" "}
                  {stock.empresaId}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.estoqueId}
              onChange={(event) =>
                update("estoqueId", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do estoque"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="movement-form__field">
          <span>Usuário</span>
          {canReadUsers ? (
            <select
              value={value.usuarioId}
              onChange={(event) => update("usuarioId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um usuário</option>
              {users.map((user) => (
                <option
                  key={user.id ?? user.login}
                  value={String(user.id ?? "")}
                >
                  {user.nome || user.login}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.usuarioId}
              onChange={(event) =>
                update("usuarioId", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do usuário"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="movement-form__field">
          <span>Tipo de movimento</span>
          <select
            value={value.tipoMovimento}
            onChange={(event) => update("tipoMovimento", event.target.value)}
            disabled={!canEditFields}
          >
            {tipoMovimentoOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="movement-form__field">
          <span>Quantidade</span>
          <input
            value={value.quantidade}
            onChange={(event) =>
              update("quantidade", normalizeDecimalInput(event.target.value))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="movement-form__field">
          <span>Valor unitário</span>
          <input
            value={value.valorUnitario}
            onChange={(event) =>
              update("valorUnitario", normalizeDecimalInput(event.target.value))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="movement-form__field">
          <span>Valor total</span>
          <input value={value.valorTotal} disabled />
        </label>

        <label className="movement-form__field movement-form__field--span-2">
          <span>Documento de referência</span>
          <input
            value={value.documentoReferencia}
            onChange={(event) =>
              update("documentoReferencia", event.target.value)
            }
            placeholder="NF-1234 / OS-456 / AJ-789"
            disabled={!canEditFields}
          />
        </label>

        <label className="movement-form__field movement-form__field--span-2">
          <span>Motivo</span>
          <input
            value={value.motivo}
            onChange={(event) => update("motivo", event.target.value)}
            placeholder="Recebimento, ajuste de saldo, transferência interna"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="movement-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alterações"
            : "Criar movimentação"}
      </button>
    </aside>
  );
}
