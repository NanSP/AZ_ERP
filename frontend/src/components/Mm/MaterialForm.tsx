import type { Product } from "../../pages/Core/ProductsPage";
import type { MaterialRecord } from "../../pages/Mm/MaterialsPage";

type MaterialFormProps = {
  value: MaterialRecord;
  editing: boolean;
  products: Product[];
  canReadProducts: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: MaterialRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const tipoMaterialOptions = [
  { value: "materia_prima", label: "Materia-prima" },
  { value: "embalagem", label: "Embalagem" },
  { value: "consumo", label: "Consumo" },
  { value: "componente", label: "Componente" },
  { value: "outro", label: "Outro" },
];

export default function MaterialForm({
  value,
  editing,
  products,
  canReadProducts,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: MaterialFormProps) {
  const canSave =
    value.produtoId.trim() !== "" && value.tipoMaterial.trim() !== "";

  function update<K extends keyof MaterialRecord>(
    field: K,
    fieldValue: MaterialRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="material-form">
      <div className="material-form__head">
        <div>
          <h3 className="material-form__title">
            {editing ? "Editar material" : "Novo material"}
          </h3>
          <p className="material-form__subtitle">
            Vincule o produto e detalhe classificacao, especificacao tecnica e
            requisitos de armazenamento.
          </p>
          {editing && value.id ? (
            <p className="material-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="material-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="material-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="material-form__grid">
        <label className="material-form__field material-form__field--span-2">
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
                  {product.codigo || "Sem codigo"} - {product.descricao || "Sem descricao"}
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
          {!canReadProducts ? (
            <small className="material-form__hint">
              Sem leitura de produtos: informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="material-form__field">
          <span>Tipo de material</span>
          <select
            value={value.tipoMaterial}
            onChange={(event) => update("tipoMaterial", event.target.value)}
            disabled={!canEditFields}
          >
            {tipoMaterialOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="material-form__field">
          <span>Categoria</span>
          <input
            value={value.categoria}
            onChange={(event) => update("categoria", event.target.value)}
            placeholder="Metal, quimico, embalagem"
            disabled={!canEditFields}
          />
        </label>

        <label className="material-form__field">
          <span>Subcategoria</span>
          <input
            value={value.subcategoria}
            onChange={(event) => update("subcategoria", event.target.value)}
            placeholder="Chapa, solvente, caixa"
            disabled={!canEditFields}
          />
        </label>

        <label className="material-form__field">
          <span>Marca</span>
          <input
            value={value.marca}
            onChange={(event) => update("marca", event.target.value)}
            placeholder="Marca do insumo"
            disabled={!canEditFields}
          />
        </label>

        <label className="material-form__field">
          <span>Modelo</span>
          <input
            value={value.modelo}
            onChange={(event) => update("modelo", event.target.value)}
            placeholder="Modelo ou referencia"
            disabled={!canEditFields}
          />
        </label>

        <label className="material-form__field">
          <span>Condicao de armazenamento</span>
          <input
            value={value.condicaoArmazenamento}
            onChange={(event) =>
              update("condicaoArmazenamento", event.target.value)
            }
            placeholder="Local seco, refrigerado, protegido"
            disabled={!canEditFields}
          />
        </label>

        <label className="material-form__field">
          <span>Classe de perigo</span>
          <input
            value={value.classePerigo}
            onChange={(event) => update("classePerigo", event.target.value)}
            placeholder="Inflamavel, corrosivo, nao aplicavel"
            disabled={!canEditFields}
          />
        </label>

        <label className="material-form__field material-form__field--span-2">
          <span>Especificacoes tecnicas</span>
          <textarea
            value={value.especificacoesTecnicas}
            onChange={(event) =>
              update("especificacoesTecnicas", event.target.value)
            }
            placeholder="Dimensoes, composicao, caracteristicas e observacoes tecnicas"
            disabled={!canEditFields}
            rows={5}
          />
        </label>
      </div>

      <button
        type="button"
        className="material-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar material"}
      </button>
    </aside>
  );
}
