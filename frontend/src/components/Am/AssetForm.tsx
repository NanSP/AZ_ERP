import type {
  Asset,
  EmployeeAccess,
  EmployeeOption,
  PartnerAccess,
  PartnerOption,
} from "../../pages/Am/AssetsPage";

type AssetFormProps = {
  value: Asset;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  partnerOptions: PartnerOption[];
  partnerAccess: PartnerAccess;
  employeeOptions: EmployeeOption[];
  employeeAccess: EmployeeAccess;
  onChange: (value: Asset) => void;
  onSave: () => void;
  onReset: () => void;
};

const statusOptions = [
  { value: "ativo", label: "Ativo" },
  { value: "inativo", label: "Inativo" },
  { value: "baixado", label: "Baixado" },
  { value: "manutenção", label: "Manutenção" },
];

export default function AssetForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  partnerOptions,
  partnerAccess,
  employeeOptions,
  employeeAccess,
  onChange,
  onSave,
  onReset,
}: AssetFormProps) {
  const canSave =
    value.codigoPatrimonio.trim() !== "" && value.nome.trim() !== "";

  function update<K extends keyof Asset>(field: K, fieldValue: Asset[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="asset-form">
      <div className="asset-form__head">
        <div>
          <h3 className="asset-form__title">
            {editing ? "Editar bem" : "Novo bem"}
          </h3>
          <p className="asset-form__subtitle">
            Cadastre o ativo com valores, depreciacão e vinculos operacionais.
          </p>
          {editing && value.id ? (
            <p className="asset-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="asset-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="asset-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="asset-form__grid">
        <label className="asset-form__field">
          <span>Codigo patrimonial</span>
          <input
            value={value.codigoPatrimonio}
            onChange={(event) => update("codigoPatrimonio", event.target.value)}
            placeholder="PAT-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="asset-form__field">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Nome do ativo"
            disabled={!canEditFields}
          />
        </label>

        <label className="asset-form__field asset-form__field--span-2">
          <span>Descrição</span>
          <input
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Descrição detalhada"
            disabled={!canEditFields}
          />
        </label>

        <label className="asset-form__field">
          <span>Tipo de ativo</span>
          <input
            value={value.tipoAtivo}
            onChange={(event) => update("tipoAtivo", event.target.value)}
            placeholder="Equipamento, veiculo..."
            disabled={!canEditFields}
          />
        </label>

        <label className="asset-form__field">
          <span>Localização</span>
          <input
            value={value.localizacao}
            onChange={(event) => update("localizacao", event.target.value)}
            placeholder="Filial, setor, sala..."
            disabled={!canEditFields}
          />
        </label>

        <label className="asset-form__field">
          <span>Data de aquisição</span>
          <input
            type="date"
            value={value.dataAquisicao}
            onChange={(event) => update("dataAquisicao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="asset-form__field">
          <span>Data de depreciacão</span>
          <input
            type="date"
            value={value.dataDepreciacao}
            onChange={(event) => update("dataDepreciacao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="asset-form__field">
          <span>Valor de aquisição</span>
          <input
            value={value.valorAquisicao}
            onChange={(event) =>
              update(
                "valorAquisicao",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="asset-form__field">
          <span>Valor atual</span>
          <input
            value={value.valorAtual}
            onChange={(event) =>
              update("valorAtual", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="asset-form__field">
          <span>Vida util</span>
          <input
            value={value.vidaUtilAnos}
            onChange={(event) =>
              update(
                "vidaUtilAnos",
                event.target.value.replace(/\D/g, "").slice(0, 3),
              )
            }
            placeholder="Anos"
            disabled={!canEditFields}
          />
        </label>

        <label className="asset-form__field">
          <span>Taxa de depreciação</span>
          <input
            value={value.taxaDepreciacao}
            onChange={(event) =>
              update(
                "taxaDepreciacao",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="asset-form__field">
          <span>Fornecedor</span>
          {partnerOptions.length > 0 ? (
            <select
              value={value.fornecedor}
              onChange={(event) => update("fornecedor", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Não vincular</option>
              {partnerOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.fornecedor}
              onChange={(event) =>
                update("fornecedor", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do fornecedor"
              disabled={!canEditFields}
            />
          )}
          {partnerAccess === "unavailable" ? (
            <small className="asset-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="asset-form__field">
          <span>Responsável</span>
          {employeeOptions.length > 0 ? (
            <select
              value={value.responsavel}
              onChange={(event) => update("responsavel", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Não vincular</option>
              {employeeOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.responsavel}
              onChange={(event) =>
                update("responsavel", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do responsavel"
              disabled={!canEditFields}
            />
          )}
          {employeeAccess === "unavailable" ? (
            <small className="asset-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="asset-form__field">
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
      </div>

      <button
        type="button"
        className="asset-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving ? "Salvando..." : editing ? "Salvar alteracoes" : "Criar bem"}
      </button>
    </aside>
  );
}
