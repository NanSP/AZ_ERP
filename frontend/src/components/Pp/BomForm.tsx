import type { BomItem, ProductAccess, ProductOption } from "../../pages/Pp/BomPage";

type BomFormProps = {
  value: BomItem;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  productOptions: ProductOption[];
  productAccess: ProductAccess;
  onChange: (value: BomItem) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function BomForm({
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
}: BomFormProps) {
  const quantidade = Number(value.quantidade.replace(",", "."));
  const nivel = Number(value.nivel);
  const tempoPreparacao =
    value.tempoPreparacao.trim() === ""
      ? 0
      : Number(value.tempoPreparacao.replace(",", "."));
  const tempoProducao =
    value.tempoProducao.trim() === ""
      ? 0
      : Number(value.tempoProducao.replace(",", "."));

  const canSave =
    value.produtoPai.trim() !== "" &&
    value.componente.trim() !== "" &&
    value.produtoPai !== value.componente &&
    value.quantidade.trim() !== "" &&
    !Number.isNaN(quantidade) &&
    quantidade > 0 &&
    !Number.isNaN(nivel) &&
    nivel >= 0 &&
    !Number.isNaN(tempoPreparacao) &&
    tempoPreparacao >= 0 &&
    !Number.isNaN(tempoProducao) &&
    tempoProducao >= 0;

  function update<K extends keyof BomItem>(field: K, fieldValue: BomItem[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="bom-form">
      <div className="bom-form__head">
        <div>
          <h3 className="bom-form__title">
            {editing ? "Editar composicao" : "Nova composicao"}
          </h3>
          <p className="bom-form__subtitle">
            Defina produto pai, componente, quantidade, hierarquia e tempos.
          </p>
          {editing && value.id ? (
            <p className="bom-form__meta">Registro selecionado: #{value.id}</p>
          ) : !canEditFields ? (
            <p className="bom-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="bom-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="bom-form__grid">
        <label className="bom-form__field">
          <span>Produto pai</span>
          {productOptions.length > 0 ? (
            <select
              value={value.produtoPai}
              onChange={(event) => update("produtoPai", event.target.value)}
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
              value={value.produtoPai}
              onChange={(event) =>
                update("produtoPai", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do produto pai"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="bom-form__field">
          <span>Componente</span>
          {productOptions.length > 0 ? (
            <select
              value={value.componente}
              onChange={(event) => update("componente", event.target.value)}
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
              value={value.componente}
              onChange={(event) =>
                update("componente", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do componente"
              disabled={!canEditFields}
            />
          )}
        </label>

        {productAccess === "unavailable" ? (
          <p className="bom-form__hint bom-form__hint--span-2">
            Lista de produtos indisponivel. Informe os IDs manualmente.
          </p>
        ) : null}

        <label className="bom-form__field">
          <span>Quantidade</span>
          <input
            value={value.quantidade}
            onChange={(event) =>
              update("quantidade", event.target.value.replace(/[^0-9,.-]/g, ""))
            }
            placeholder="1"
            disabled={!canEditFields}
          />
        </label>

        <label className="bom-form__field">
          <span>Unidade de medida</span>
          <input
            value={value.unidadeMedida}
            onChange={(event) => update("unidadeMedida", event.target.value)}
            placeholder="UN, KG, M..."
            disabled={!canEditFields}
          />
        </label>

        <label className="bom-form__field">
          <span>Nivel</span>
          <input
            value={value.nivel}
            onChange={(event) =>
              update("nivel", event.target.value.replace(/[^\d]/g, ""))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="bom-form__field">
          <span>Roteiro</span>
          <input
            value={value.roteiro}
            onChange={(event) =>
              update("roteiro", event.target.value.replace(/[^\d]/g, ""))
            }
            placeholder="1"
            disabled={!canEditFields}
          />
        </label>

        <label className="bom-form__field">
          <span>Tempo de preparacao</span>
          <input
            value={value.tempoPreparacao}
            onChange={(event) =>
              update(
                "tempoPreparacao",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="bom-form__field">
          <span>Tempo de producao</span>
          <input
            value={value.tempoProducao}
            onChange={(event) =>
              update(
                "tempoProducao",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="bom-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar composicao"}
      </button>
    </aside>
  );
}
