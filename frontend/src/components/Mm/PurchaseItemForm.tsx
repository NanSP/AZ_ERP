import type { Product } from "../../pages/Core/ProductsPage";
import type { PurchaseRecord } from "../../pages/Mm/PurchasesPage";
import type { PurchaseItemRecord } from "../../pages/Mm/PurchaseItemsPage";

type PurchaseItemFormProps = {
  value: PurchaseItemRecord;
  editing: boolean;
  purchases: PurchaseRecord[];
  products: Product[];
  canReadPurchases: boolean;
  canReadProducts: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: PurchaseItemRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function PurchaseItemForm({
  value,
  editing,
  purchases,
  products,
  canReadPurchases,
  canReadProducts,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: PurchaseItemFormProps) {
  const quantidade = Number((value.quantidade || "").replace(",", "."));
  const valorUnitario =
    value.valorUnitario.trim() === ""
      ? 0
      : Number(value.valorUnitario.replace(",", "."));
  const quantidadeRecebida =
    value.quantidadeRecebida.trim() === ""
      ? 0
      : Number(value.quantidadeRecebida.replace(",", "."));
  const canSave =
    value.compraId.trim() !== "" &&
    value.produtoId.trim() !== "" &&
    !Number.isNaN(quantidade) &&
    quantidade > 0 &&
    !Number.isNaN(valorUnitario) &&
    valorUnitario >= 0 &&
    !Number.isNaN(quantidadeRecebida) &&
    quantidadeRecebida >= 0 &&
    quantidadeRecebida <= quantidade;

  function update<K extends keyof PurchaseItemRecord>(
    field: K,
    fieldValue: PurchaseItemRecord[K],
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
    <aside className="purchase-item-form">
      <div className="purchase-item-form__head">
        <div>
          <h3 className="purchase-item-form__title">
            {editing ? "Editar item de compra" : "Novo item de compra"}
          </h3>
          <p className="purchase-item-form__subtitle">
            Associe compra e produto, informe quantidade comprada e controle o
            recebimento parcial ou total.
          </p>
          {editing && value.id ? (
            <p className="purchase-item-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="purchase-item-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="purchase-item-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="purchase-item-form__grid">
        <label className="purchase-item-form__field purchase-item-form__field--span-2">
          <span>Compra</span>
          {canReadPurchases ? (
            <select
              value={value.compraId}
              onChange={(event) => update("compraId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione uma compra</option>
              {purchases.map((purchase) => (
                <option
                  key={
                    purchase.id ??
                    `${purchase.fornecedorId}-${purchase.dataPedido}`
                  }
                  value={String(purchase.id ?? "")}
                >
                  #{purchase.id} - Fornecedor {purchase.fornecedorId || "-"} /{" "}
                  {purchase.status || "aberto"}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.compraId}
              onChange={(event) =>
                update("compraId", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID da compra"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="purchase-item-form__field purchase-item-form__field--span-2">
          <span>Produto</span>
          {canReadProducts ? (
            <select
              value={value.produtoId}
              onChange={(event) => update("produtoId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um produto</option>
              {products.map((product) => (
                <option
                  key={product.id ?? product.codigo}
                  value={String(product.id ?? "")}
                >
                  {product.codigo || "Sem codigo"} -{" "}
                  {product.nome || "Sem nome"}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.produtoId}
              onChange={(event) =>
                update("produtoId", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do produto"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="purchase-item-form__field">
          <span>Quantidade comprada</span>
          <input
            value={value.quantidade}
            onChange={(event) =>
              update("quantidade", normalizeDecimalInput(event.target.value))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="purchase-item-form__field">
          <span>Quantidade recebida</span>
          <input
            value={value.quantidadeRecebida}
            onChange={(event) =>
              update(
                "quantidadeRecebida",
                normalizeDecimalInput(event.target.value),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="purchase-item-form__field">
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

        <label className="purchase-item-form__field">
          <span>Valor total</span>
          <input value={value.valorTotal} disabled />
        </label>
      </div>

      <button
        type="button"
        className="purchase-item-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar item de compra"}
      </button>
    </aside>
  );
}
