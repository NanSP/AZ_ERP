import type { Product } from "../../pages/Core/ProductsPage";

type ProductFormProps = {
  value: Product;
  editing: boolean;
  saving: boolean;
  onChange: (value: Product) => void;
  onSave: () => void;
  onReset: () => void;
};

const tipoItemOptions = [
  { value: "produto", label: "Produto" },
  { value: "servico", label: "Servico" },
  { value: "insumo", label: "Insumo" },
  { value: "embalagem", label: "Embalagem" },
];

const unidadeOptions = [
  { value: "", label: "Nao informada" },
  { value: "UN", label: "UN" },
  { value: "KG", label: "KG" },
  { value: "LT", label: "LT" },
  { value: "CX", label: "CX" },
];

const origemOptions = [
  { value: "0", label: "0 - Nacional" },
  { value: "1", label: "1 - Estrangeira importacao direta" },
  { value: "2", label: "2 - Estrangeira adquirida no mercado interno" },
  { value: "3", label: "3 - Nacional conteudo importacao > 40%" },
  { value: "4", label: "4 - Nacional processos basicos" },
  { value: "5", label: "5 - Nacional conteudo importacao <= 40%" },
  { value: "6", label: "6 - Estrangeira importacao direta sem similar" },
  { value: "7", label: "7 - Estrangeira mercado interno sem similar" },
  { value: "8", label: "8 - Nacional conteudo importacao > 70%" },
];

const situacaoOptions = [
  { value: "ativo", label: "Ativo" },
  { value: "inativo", label: "Inativo" },
  { value: "bloqueado", label: "Bloqueado" },
];

export default function ProductForm({
  value,
  editing,
  saving,
  onChange,
  onSave,
  onReset,
}: ProductFormProps) {
  function update<K extends keyof Product>(field: K, fieldValue: Product[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="product-form">
      <div className="product-form__head">
        <div>
          <h3 className="product-form__title">
            {editing ? "Editar produto" : "Novo produto"}
          </h3>
          <p className="product-form__subtitle">
            Preencha os dados centrais do catalogo do item.
          </p>
        </div>

        <button
          type="button"
          className="product-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="product-form__grid">
        <label className="product-form__field">
          <span>Codigo</span>
          <input
            value={value.codigo}
            onChange={(event) => update("codigo", event.target.value)}
            placeholder="Ex.: PROD-001"
          />
        </label>

        <label className="product-form__field">
          <span>Codigo de barras</span>
          <input
            value={value.codigoBarras}
            onChange={(event) => update("codigoBarras", event.target.value)}
            placeholder="EAN ou GTIN"
          />
        </label>

        <label className="product-form__field product-form__field--span-2">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Nome do produto"
          />
        </label>

        <label className="product-form__field product-form__field--span-2">
          <span>Descricao</span>
          <input
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Descricao comercial ou tecnica"
          />
        </label>

        <label className="product-form__field">
          <span>Tipo de item</span>
          <select
            value={value.tipoItem}
            onChange={(event) => update("tipoItem", event.target.value)}
          >
            {tipoItemOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="product-form__field">
          <span>Unidade de medida</span>
          <select
            value={value.unidadeMedida}
            onChange={(event) => update("unidadeMedida", event.target.value)}
          >
            {unidadeOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="product-form__field">
          <span>NCM</span>
          <input
            value={value.ncm}
            onChange={(event) => update("ncm", event.target.value)}
            placeholder="8 digitos"
          />
        </label>

        <label className="product-form__field">
          <span>CEST</span>
          <input
            value={value.cest}
            onChange={(event) => update("cest", event.target.value)}
            placeholder="7 digitos"
          />
        </label>

        <label className="product-form__field">
          <span>Peso bruto</span>
          <input
            value={value.pesoBruto}
            onChange={(event) => update("pesoBruto", event.target.value)}
            placeholder="0.000"
          />
        </label>

        <label className="product-form__field">
          <span>Peso liquido</span>
          <input
            value={value.pesoLiquido}
            onChange={(event) => update("pesoLiquido", event.target.value)}
            placeholder="0.000"
          />
        </label>

        <label className="product-form__field">
          <span>Origem</span>
          <select
            value={value.origem}
            onChange={(event) => update("origem", event.target.value)}
          >
            {origemOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="product-form__field">
          <span>Situacao</span>
          <select
            value={value.situacao}
            onChange={(event) => update("situacao", event.target.value)}
          >
            {situacaoOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>
      </div>

      <button
        type="button"
        className="product-form__button"
        onClick={onSave}
        disabled={saving}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar produto"}
      </button>
    </aside>
  );
}
