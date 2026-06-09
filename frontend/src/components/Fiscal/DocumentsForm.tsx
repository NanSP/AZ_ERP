import type {
  ClientAccess,
  ClientOption,
  DocumentEntry,
  OrderAccess,
} from "../../pages/Fiscal/DocumentsPage";

type DocumentsFormProps = {
  value: DocumentEntry;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  clients: ClientOption[];
  clientAccess: ClientAccess;
  orderAccess: OrderAccess;
  onChange: (value: DocumentEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

const typeOptions = [
  { value: "", label: "Selecione" },
  { value: "nfe", label: "NFe" },
  { value: "nfce", label: "NFCe" },
  { value: "cte", label: "CTe" },
  { value: "nfse", label: "NFSe" },
];

const statusOptions = [
  { value: "digitado", label: "Digitado" },
  { value: "emitido", label: "Emitido" },
  { value: "cancelado", label: "Cancelado" },
  { value: "inutilizado", label: "Inutilizado" },
];

function isElectronicDocument(value: string) {
  return value === "nfe" || value === "nfce" || value === "cte";
}

export default function DocumentsForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  clients,
  clientAccess,
  orderAccess,
  onChange,
  onSave,
  onReset,
}: DocumentsFormProps) {
  const electronicDocument = isElectronicDocument(value.tipoDocumento);
  const canSave =
    value.tipoDocumento.trim() !== "" &&
    value.numero.trim() !== "" &&
    value.dataEmissao.trim() !== "" &&
    (value.pedido.trim() !== "" || value.cliente.trim() !== "");

  function update<K extends keyof DocumentEntry>(
    field: K,
    fieldValue: DocumentEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="documents-form">
      <div className="documents-page__panel-head">
        <div>
          <h3 className="documents-page__panel-title">
            {editing ? "Editar documento" : "Novo documento"}
          </h3>
          <p className="documents-page__panel-subtitle">
            Registre documentos fiscais com cliente, pedido, chave de acesso e
            situação operacional.
          </p>
          {editing && value.id ? (
            <p className="documents-page__panel-meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="documents-page__panel-meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="documents-page__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="documents-page__form-grid">
        <label className="documents-page__field">
          <span>Tipo do documento</span>
          <select
            value={value.tipoDocumento}
            onChange={(event) => update("tipoDocumento", event.target.value)}
            disabled={!canEditFields}
          >
            {typeOptions.map((option) => (
              <option key={option.value || "empty"} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="documents-page__field">
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

        <label className="documents-page__field">
          <span>Número</span>
          <input
            value={value.numero}
            onChange={(event) => update("numero", event.target.value)}
            placeholder="Número do documento"
            disabled={!canEditFields}
          />
        </label>

        <label className="documents-page__field">
          <span>Série</span>
          <input
            value={value.serie}
            onChange={(event) => update("serie", event.target.value)}
            placeholder="Série"
            disabled={!canEditFields}
          />
        </label>

        <label className="documents-page__field documents-page__field--span-2">
          <span>Chave de acesso</span>
          <input
            value={value.chaveAcesso}
            onChange={(event) =>
              update("chaveAcesso", event.target.value.replace(/\D/g, ""))
            }
            placeholder={
              electronicDocument
                ? "44 digitos numericos"
                : "Opcional para este tipo"
            }
            disabled={!canEditFields}
          />
        </label>

        <label className="documents-page__field">
          <span>Data de emissão</span>
          <input
            type="date"
            value={value.dataEmissao}
            onChange={(event) => update("dataEmissao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="documents-page__field">
          <span>Valor total</span>
          <input
            value={value.valorTotal}
            onChange={(event) =>
              update("valorTotal", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="0,00"
            disabled={!canEditFields}
          />
        </label>

        <label className="documents-page__field">
          <span>Pedido</span>
          <input
            value={value.pedido}
            onChange={(event) =>
              update("pedido", event.target.value.replace(/\D/g, ""))
            }
            placeholder="ID do pedido"
            disabled={!canEditFields}
          />
          {orderAccess === "unavailable" ? (
            <small className="documents-page__hint">
              Informe o pedido manualmente quando necessario.
            </small>
          ) : null}
        </label>

        <label className="documents-page__field">
          <span>Cliente</span>
          {clients.length > 0 ? (
            <select
              value={value.cliente}
              onChange={(event) => update("cliente", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Não vincular</option>
              {clients.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.cliente}
              onChange={(event) =>
                update("cliente", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do cliente"
              disabled={!canEditFields}
            />
          )}
          {clientAccess === "unavailable" ? (
            <small className="documents-page__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="documents-page__field documents-page__field--span-2">
          <span>XML</span>
          <textarea
            value={value.xml_file}
            onChange={(event) => update("xml_file", event.target.value)}
            placeholder="Conteudo XML ou referencia do arquivo"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="documents-page__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar documento"}
      </button>
    </aside>
  );
}
