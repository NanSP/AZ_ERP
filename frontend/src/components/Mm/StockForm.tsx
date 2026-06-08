import type { Company } from "../../pages/Core/CompaniesPage";
import type { Product } from "../../pages/Core/ProductsPage";
import type { StockRecord } from "../../pages/Mm/StocksPage";

type StockFormProps = {
  value: StockRecord;
  editing: boolean;
  products: Product[];
  companies: Company[];
  canReadProducts: boolean;
  canReadCompanies: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: StockRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function StockForm({
  value,
  editing,
  products,
  companies,
  canReadProducts,
  canReadCompanies,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: StockFormProps) {
  const quantidade = Number(value.quantidade.replace(",", "."));
  const quantidadeMinima =
    value.quantidadeMinima.trim() === ""
      ? 0
      : Number(value.quantidadeMinima.replace(",", "."));
  const quantidadeMaxima =
    value.quantidadeMaxima.trim() === ""
      ? 0
      : Number(value.quantidadeMaxima.replace(",", "."));
  const valorUnitario =
    value.valorUnitario.trim() === ""
      ? 0
      : Number(value.valorUnitario.replace(",", "."));

  const canSave =
    value.produtoId.trim() !== "" &&
    value.empresaId.trim() !== "" &&
    !Number.isNaN(quantidade) &&
    quantidade >= 0 &&
    !Number.isNaN(quantidadeMinima) &&
    quantidadeMinima >= 0 &&
    !Number.isNaN(quantidadeMaxima) &&
    quantidadeMaxima >= 0 &&
    !Number.isNaN(valorUnitario) &&
    valorUnitario >= 0 &&
    (quantidadeMaxima === 0 || quantidadeMinima <= quantidadeMaxima);

  function update<K extends keyof StockRecord>(
    field: K,
    fieldValue: StockRecord[K],
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
    <aside className="stock-form">
      <div className="stock-form__head">
        <div>
          <h3 className="stock-form__title">
            {editing ? "Editar estoque" : "Novo estoque"}
          </h3>
          <p className="stock-form__subtitle">
            Defina identificacao, saldo, limites operacionais e valor unitario da posicao.
          </p>
          {editing && value.id ? (
            <p className="stock-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="stock-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="stock-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="stock-form__grid">
        <label className="stock-form__field">
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
                  {product.codigo || "Sem codigo"} - {product.nome || product.descricao || "Sem descricao"}
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

        <label className="stock-form__field">
          <span>Empresa</span>
          {canReadCompanies ? (
            <select
              value={value.empresaId}
              onChange={(event) => update("empresaId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione uma empresa</option>
              {companies.map((company) => (
                <option
                  key={company.id ?? company.codigo}
                  value={String(company.id ?? "")}
                >
                  {company.codigo || "Sem codigo"} - {company.razaoSocial || "Sem nome"}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.empresaId}
              onChange={(event) =>
                update("empresaId", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID da empresa"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="stock-form__field">
          <span>Localizacao</span>
          <input
            value={value.localizacao}
            onChange={(event) => update("localizacao", event.target.value)}
            placeholder="Deposito A / Rua 1 / Prateleira 3"
            disabled={!canEditFields}
          />
        </label>

        <label className="stock-form__field">
          <span>Lote</span>
          <input
            value={value.lote}
            onChange={(event) => update("lote", event.target.value)}
            placeholder="Lote-2026-01"
            disabled={!canEditFields}
          />
        </label>

        <label className="stock-form__field">
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

        <label className="stock-form__field">
          <span>Quantidade minima</span>
          <input
            value={value.quantidadeMinima}
            onChange={(event) =>
              update("quantidadeMinima", normalizeDecimalInput(event.target.value))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="stock-form__field">
          <span>Quantidade maxima</span>
          <input
            value={value.quantidadeMaxima}
            onChange={(event) =>
              update("quantidadeMaxima", normalizeDecimalInput(event.target.value))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="stock-form__field">
          <span>Valor unitario</span>
          <input
            value={value.valorUnitario}
            onChange={(event) =>
              update("valorUnitario", normalizeDecimalInput(event.target.value))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="stock-form__field stock-form__field--span-2">
          <span>Data de validade</span>
          <input
            type="date"
            value={value.dataValidade}
            onChange={(event) => update("dataValidade", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        {quantidadeMaxima > 0 && quantidadeMinima > quantidadeMaxima ? (
          <p className="stock-form__hint stock-form__hint--span-2">
            Quantidade minima nao pode ser maior que a quantidade maxima.
          </p>
        ) : null}
      </div>

      <button
        type="button"
        className="stock-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar estoque"}
      </button>
    </aside>
  );
}
