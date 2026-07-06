import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import { canAccessResourceAction } from "../../services/accessControl";
import { api } from "../../services/api";
import {
  createResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./subject-requests-page.css";

type SubjectRequestRecord = {
  id?: number;
  protocolo: string;
  titularNome: string;
  titularContato: string;
  tipoTitular: string;
  direitoSolicitado: string;
  modulo: string;
  entidade: string;
  status: string;
  canalOrigem: string;
  detalhes: string;
  prazoResposta: string;
  dataSolicitacao: string;
  dataConclusao: string;
  respostaResumo: string;
  atendidoPor: string;
  registroTratamentoId: string;
  consentimentoId: string;
};

type SubjectRequestSummary = {
  abertas: number;
  emAnalise: number;
  aguardandoTitular: number;
  concluidas: number;
  indeferidas: number;
  vencidas: number;
  vencendoEmBreve: number;
};

type SubjectRequestEvent = {
  id: number;
  solicitacaoId: number;
  tipoEvento: string;
  titulo: string;
  descricao: string | null;
  detalhesJson: Record<string, unknown> | null;
  criadoPorId: number | null;
  createdAt: string;
};

type SubjectRequestsPageProps = {
  embedded?: boolean;
};

const requestsResource = {
  schema: "grc",
  entity: "solicitacoesTitular",
  label: "Solicitacoes do Titular",
  description: "Pedidos LGPD, SLA de resposta e trilha operacional.",
} as const;

const emptySummary: SubjectRequestSummary = {
  abertas: 0,
  emAnalise: 0,
  aguardandoTitular: 0,
  concluidas: 0,
  indeferidas: 0,
  vencidas: 0,
  vencendoEmBreve: 0,
};

const emptyRequest: SubjectRequestRecord = {
  protocolo: "",
  titularNome: "",
  titularContato: "",
  tipoTitular: "cliente",
  direitoSolicitado: "acesso",
  modulo: "",
  entidade: "",
  status: "aberta",
  canalOrigem: "portal",
  detalhes: "",
  prazoResposta: "",
  dataSolicitacao: "",
  dataConclusao: "",
  respostaResumo: "",
  atendidoPor: "",
  registroTratamentoId: "",
  consentimentoId: "",
};

function normalizeDateTimeLocal(value: unknown) {
  if (typeof value !== "string" || value.trim() === "") {
    return "";
  }

  const normalized = value.trim();
  return normalized.length >= 16 ? normalized.slice(0, 16) : normalized;
}

function normalizeRequest(data: Record<string, unknown>): SubjectRequestRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    protocolo: String(data.protocolo ?? ""),
    titularNome: String(data.titularNome ?? ""),
    titularContato: String(data.titularContato ?? ""),
    tipoTitular: String(data.tipoTitular ?? "cliente"),
    direitoSolicitado: String(data.direitoSolicitado ?? "acesso"),
    modulo: String(data.modulo ?? ""),
    entidade: String(data.entidade ?? ""),
    status: String(data.status ?? "aberta"),
    canalOrigem: String(data.canalOrigem ?? "portal"),
    detalhes: String(data.detalhes ?? ""),
    prazoResposta: normalizeDateTimeLocal(data.prazoResposta),
    dataSolicitacao: normalizeDateTimeLocal(data.dataSolicitacao),
    dataConclusao: normalizeDateTimeLocal(data.dataConclusao),
    respostaResumo: String(data.respostaResumo ?? ""),
    atendidoPor: data.atendidoPor == null ? "" : String(data.atendidoPor),
    registroTratamentoId:
      data.registroTratamentoId == null ? "" : String(data.registroTratamentoId),
    consentimentoId:
      data.consentimentoId == null ? "" : String(data.consentimentoId),
  };
}

function toPayload(item: SubjectRequestRecord) {
  return {
    titularNome: item.titularNome.trim() || null,
    titularContato: item.titularContato.trim() || null,
    tipoTitular: item.tipoTitular.trim() || null,
    direitoSolicitado: item.direitoSolicitado.trim() || null,
    modulo: item.modulo.trim() || null,
    entidade: item.entidade.trim() || null,
    status: item.status.trim() || null,
    canalOrigem: item.canalOrigem.trim() || null,
    detalhes: item.detalhes.trim() || null,
    prazoResposta: item.prazoResposta.trim() || null,
    dataConclusao: item.dataConclusao.trim() || null,
    respostaResumo: item.respostaResumo.trim() || null,
    atendidoPor: item.atendidoPor.trim() ? Number(item.atendidoPor) : null,
    registroTratamentoId: item.registroTratamentoId.trim()
      ? Number(item.registroTratamentoId)
      : null,
    consentimentoId: item.consentimentoId.trim()
      ? Number(item.consentimentoId)
      : null,
  };
}

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message =
      error.response?.data?.message ?? error.response?.data?.error;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  return fallback;
}

function calculateSlaState(item: SubjectRequestRecord) {
  if (
    !item.prazoResposta ||
    item.status === "concluida" ||
    item.status === "indeferida"
  ) {
    return "neutro";
  }

  const dueTime = new Date(item.prazoResposta).getTime();
  const now = Date.now();
  const diffDays = (dueTime - now) / (1000 * 60 * 60 * 24);

  if (diffDays < 0) {
    return "vencido";
  }

  if (diffDays <= 3) {
    return "alerta";
  }

  return "ok";
}

export default function SubjectRequestsPage({
  embedded = false,
}: SubjectRequestsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<SubjectRequestRecord[]>([]);
  const [summary, setSummary] = useState<SubjectRequestSummary>(emptySummary);
  const [selected, setSelected] = useState<SubjectRequestRecord | null>(null);
  const [events, setEvents] = useState<SubjectRequestEvent[]>([]);
  const [draft, setDraft] = useState<SubjectRequestRecord>(emptyRequest);
  const [query, setQuery] = useState("");
  const [noteTitle, setNoteTitle] = useState("");
  const [noteDescription, setNoteDescription] = useState("");
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const canRead = canAccessResourceAction(session, requestsResource, "read");
  const canCreate = canAccessResourceAction(session, requestsResource, "create");
  const canUpdate = canAccessResourceAction(session, requestsResource, "update");
  const canSubmit = selected ? canUpdate : canCreate;

  const loadSummary = useCallback(async () => {
    if (!canRead) {
      setSummary(emptySummary);
      return;
    }

    const response = await api.get("/grc/solicitacoesTitular/summary");
    setSummary(response.data as SubjectRequestSummary);
  }, [canRead]);

  const loadItems = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      return;
    }

    const response = await listResource("grc", "solicitacoesTitular");
    const nextItems = Array.isArray(response.data)
      ? response.data.map((item) =>
          normalizeRequest(item as Record<string, unknown>),
        )
      : [];
    setItems(nextItems);
  }, [canRead]);

  const loadEvents = useCallback(
    async (id?: number) => {
      if (!canRead || !id) {
        setEvents([]);
        return;
      }

      const response = await api.get(`/grc/solicitacoesTitular/${id}/eventos`);
      setEvents(
        Array.isArray(response.data) ? (response.data as SubjectRequestEvent[]) : [],
      );
    },
    [canRead],
  );

  const loadAll = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      await Promise.all([loadSummary(), loadItems()]);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar as solicitacoes LGPD."),
      );
    } finally {
      setLoading(false);
    }
  }, [loadItems, loadSummary]);

  useEffect(() => {
    void loadAll();
  }, [loadAll]);

  useEffect(() => {
    void loadEvents(selected?.id);
  }, [loadEvents, selected?.id]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.protocolo,
        item.titularNome,
        item.titularContato,
        item.direitoSolicitado,
        item.status,
      ]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyRequest });
    setEvents([]);
    setError(null);
    setSuccess(null);
  }

  function handleSelect(item: SubjectRequestRecord) {
    setSelected(item);
    setDraft({ ...item });
    setError(null);
    setSuccess(null);
  }

  async function handleSave() {
    if (!canSubmit) {
      setError("Seu perfil nao possui permissao para gravar solicitacoes.");
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const response = selected?.id
        ? await updateResource(
            "grc",
            "solicitacoesTitular",
            selected.id,
            toPayload(draft),
          )
        : await createResource("grc", "solicitacoesTitular", toPayload(draft));

      const saved = normalizeRequest(response.data as Record<string, unknown>);
      await loadAll();
      setSelected(saved);
      setDraft({ ...saved });
      await loadEvents(saved.id);
      setSuccess(
        selected?.id
          ? "Solicitacao atualizada com sucesso."
          : "Solicitacao registrada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel salvar a solicitacao do titular."),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleAddEvidence() {
    if (!selected?.id) {
      setError("Selecione uma solicitacao para registrar evidencia.");
      return;
    }

    if (!noteTitle.trim()) {
      setError("Informe um titulo curto para a evidencia.");
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await api.post(`/grc/solicitacoesTitular/${selected.id}/eventos`, {
        tipoEvento: "evidencia",
        titulo: noteTitle.trim(),
        descricao: noteDescription.trim() || null,
        detalhesJson: session?.userId
          ? { criadoNoFrontendPor: session.userId }
          : null,
      });
      await loadEvents(selected.id);
      setNoteTitle("");
      setNoteDescription("");
      setSuccess("Evidencia registrada com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel registrar a evidencia."),
      );
    } finally {
      setSaving(false);
    }
  }

  function updateField<K extends keyof SubjectRequestRecord>(
    field: K,
    value: SubjectRequestRecord[K],
  ) {
    setDraft((current) => ({ ...current, [field]: value }));
  }

  return (
    <div
      className={
        embedded
          ? "subject-requests-page subject-requests-page--embedded"
          : "subject-requests-page"
      }
    >
      <header className="subject-requests-page__hero">
        <div>
          <span className="subject-requests-page__eyebrow">LGPD</span>
          <h2 className="subject-requests-page__title">
            Solicitacoes do titular
          </h2>
          <p className="subject-requests-page__subtitle">
            Operacao de direitos do titular com acompanhamento de SLA, vinculo
            com consentimento e registro de evidencias.
          </p>
        </div>

        <div className="subject-requests-page__actions">
          <input
            type="search"
            className="subject-requests-page__search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por protocolo, titular ou status"
            disabled={loading || !canRead}
          />
          <button
            type="button"
            className="subject-requests-page__button subject-requests-page__button--ghost"
            onClick={() => void loadAll()}
            disabled={loading}
          >
            Recarregar
          </button>
          <button
            type="button"
            className="subject-requests-page__button"
            onClick={handleNew}
            disabled={!canCreate}
          >
            Nova solicitacao
          </button>
        </div>
      </header>

      <section className="subject-requests-page__summary">
        <div className="subject-requests-page__summary-card">
          <span>Abertas</span>
          <strong>{summary.abertas}</strong>
        </div>
        <div className="subject-requests-page__summary-card">
          <span>Em analise</span>
          <strong>{summary.emAnalise}</strong>
        </div>
        <div className="subject-requests-page__summary-card">
          <span>Aguardando titular</span>
          <strong>{summary.aguardandoTitular}</strong>
        </div>
        <div className="subject-requests-page__summary-card">
          <span>Vencidas</span>
          <strong>{summary.vencidas}</strong>
        </div>
        <div className="subject-requests-page__summary-card">
          <span>Vencendo em breve</span>
          <strong>{summary.vencendoEmBreve}</strong>
        </div>
        <div className="subject-requests-page__summary-card">
          <span>Encerradas</span>
          <strong>{summary.concluidas + summary.indeferidas}</strong>
        </div>
      </section>

      {error ? <div className="subject-requests-page__alert">{error}</div> : null}
      {success ? (
        <div className="subject-requests-page__alert subject-requests-page__alert--success">
          {success}
        </div>
      ) : null}

      <div className="subject-requests-page__layout">
        <section className="subject-requests-page__list">
          <div className="subject-requests-page__panel-head">
            <h3>Fila operacional</h3>
            <span>{filteredItems.length} registros</span>
          </div>

          <div className="subject-requests-page__table-wrap">
            <table className="subject-requests-page__table">
              <thead>
                <tr>
                  <th>Protocolo</th>
                  <th>Titular</th>
                  <th>Status</th>
                  <th>SLA</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr>
                    <td colSpan={4} className="subject-requests-page__empty">
                      Carregando solicitacoes...
                    </td>
                  </tr>
                ) : filteredItems.length === 0 ? (
                  <tr>
                    <td colSpan={4} className="subject-requests-page__empty">
                      Nenhuma solicitacao encontrada.
                    </td>
                  </tr>
                ) : (
                  filteredItems.map((item) => (
                    <tr
                      key={item.id ?? item.protocolo}
                      data-selected={item.id === selected?.id ? "true" : "false"}
                      onClick={() => handleSelect(item)}
                    >
                      <td>
                        <strong>{item.protocolo || "-"}</strong>
                      </td>
                      <td>
                        <div className="subject-requests-page__cell-stack">
                          <strong>{item.titularNome}</strong>
                          <span>{item.titularContato}</span>
                        </div>
                      </td>
                      <td>
                        <span className="subject-requests-page__badge">
                          {item.status || "-"}
                        </span>
                      </td>
                      <td>
                        <span
                          className={`subject-requests-page__sla subject-requests-page__sla--${calculateSlaState(item)}`}
                        >
                          {item.prazoResposta || "Sem prazo"}
                        </span>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </section>

        <section className="subject-requests-page__form-panel">
          <div className="subject-requests-page__panel-head">
            <div>
              <h3>{selected ? "Editar solicitacao" : "Nova solicitacao"}</h3>
              <p>
                Relacione a demanda com o consentimento e o registro de
                tratamento para manter a rastreabilidade LGPD.
              </p>
            </div>
            <span>{selected?.protocolo || "Novo protocolo"}</span>
          </div>

          <div className="subject-requests-page__form-grid">
            <label>
              <span>Nome do titular</span>
              <input
                value={draft.titularNome}
                onChange={(event) => updateField("titularNome", event.target.value)}
                disabled={!canSubmit}
              />
            </label>
            <label>
              <span>Contato</span>
              <input
                value={draft.titularContato}
                onChange={(event) =>
                  updateField("titularContato", event.target.value)
                }
                disabled={!canSubmit}
              />
            </label>
            <label>
              <span>Tipo do titular</span>
              <select
                value={draft.tipoTitular}
                onChange={(event) => updateField("tipoTitular", event.target.value)}
                disabled={!canSubmit}
              >
                <option value="cliente">Cliente</option>
                <option value="colaborador">Colaborador</option>
                <option value="fornecedor">Fornecedor</option>
                <option value="usuario">Usuario</option>
                <option value="visitante">Visitante</option>
                <option value="outro">Outro</option>
              </select>
            </label>
            <label>
              <span>Direito solicitado</span>
              <select
                value={draft.direitoSolicitado}
                onChange={(event) =>
                  updateField("direitoSolicitado", event.target.value)
                }
                disabled={!canSubmit}
              >
                <option value="confirmacao">Confirmacao</option>
                <option value="acesso">Acesso</option>
                <option value="correcao">Correcao</option>
                <option value="anonimizacao">Anonimizacao</option>
                <option value="eliminacao">Eliminacao</option>
                <option value="portabilidade">Portabilidade</option>
                <option value="oposicao">Oposicao</option>
                <option value="revogacao_consentimento">
                  Revogacao de consentimento
                </option>
                <option value="revisao">Revisao</option>
              </select>
            </label>
            <label>
              <span>Status</span>
              <select
                value={draft.status}
                onChange={(event) => updateField("status", event.target.value)}
                disabled={!canSubmit}
              >
                <option value="aberta">Aberta</option>
                <option value="em_analise">Em analise</option>
                <option value="aguardando_titular">Aguardando titular</option>
                <option value="concluida">Concluida</option>
                <option value="indeferida">Indeferida</option>
              </select>
            </label>
            <label>
              <span>Canal de origem</span>
              <select
                value={draft.canalOrigem}
                onChange={(event) => updateField("canalOrigem", event.target.value)}
                disabled={!canSubmit}
              >
                <option value="portal">Portal</option>
                <option value="email">Email</option>
                <option value="telefone">Telefone</option>
                <option value="presencial">Presencial</option>
                <option value="suporte">Suporte</option>
              </select>
            </label>
            <label>
              <span>Modulo relacionado</span>
              <input
                value={draft.modulo}
                onChange={(event) => updateField("modulo", event.target.value)}
                disabled={!canSubmit}
              />
            </label>
            <label>
              <span>Entidade relacionada</span>
              <input
                value={draft.entidade}
                onChange={(event) => updateField("entidade", event.target.value)}
                disabled={!canSubmit}
              />
            </label>
            <label>
              <span>Registro de tratamento ID</span>
              <input
                value={draft.registroTratamentoId}
                onChange={(event) =>
                  updateField(
                    "registroTratamentoId",
                    event.target.value.replace(/\D/g, ""),
                  )
                }
                disabled={!canSubmit}
              />
            </label>
            <label>
              <span>Consentimento ID</span>
              <input
                value={draft.consentimentoId}
                onChange={(event) =>
                  updateField(
                    "consentimentoId",
                    event.target.value.replace(/\D/g, ""),
                  )
                }
                disabled={!canSubmit}
              />
            </label>
            <label>
              <span>Prazo de resposta</span>
              <input
                type="datetime-local"
                value={draft.prazoResposta}
                onChange={(event) =>
                  updateField("prazoResposta", event.target.value)
                }
                disabled={!canSubmit}
              />
            </label>
            <label>
              <span>Atendido por ID</span>
              <input
                value={draft.atendidoPor}
                onChange={(event) =>
                  updateField("atendidoPor", event.target.value.replace(/\D/g, ""))
                }
                disabled={!canSubmit}
              />
            </label>
            <label className="subject-requests-page__field subject-requests-page__field--wide">
              <span>Detalhes</span>
              <textarea
                value={draft.detalhes}
                onChange={(event) => updateField("detalhes", event.target.value)}
                disabled={!canSubmit}
              />
            </label>
            <label className="subject-requests-page__field subject-requests-page__field--wide">
              <span>Resposta resumo</span>
              <textarea
                value={draft.respostaResumo}
                onChange={(event) =>
                  updateField("respostaResumo", event.target.value)
                }
                disabled={!canSubmit}
              />
            </label>
            <label>
              <span>Data de conclusao</span>
              <input
                type="datetime-local"
                value={draft.dataConclusao}
                onChange={(event) =>
                  updateField("dataConclusao", event.target.value)
                }
                disabled={!canSubmit}
              />
            </label>
          </div>

          <div className="subject-requests-page__form-actions">
            <button
              type="button"
              className="subject-requests-page__button subject-requests-page__button--ghost"
              onClick={handleNew}
              disabled={saving}
            >
              Limpar formulario
            </button>
            <button
              type="button"
              className="subject-requests-page__button"
              onClick={() => void handleSave()}
              disabled={saving || !canSubmit}
            >
              {saving
                ? "Salvando..."
                : selected
                  ? "Salvar alteracoes"
                  : "Criar solicitacao"}
            </button>
          </div>
        </section>
      </div>

      <section className="subject-requests-page__events">
        <div className="subject-requests-page__panel-head">
          <div>
            <h3>Trilha de auditoria e evidencias</h3>
            <p>
              Cada movimento relevante da solicitacao fica registrado para
              sustentar governanca e prestacao de contas.
            </p>
          </div>
          <span>{events.length} eventos</span>
        </div>

        <div className="subject-requests-page__evidence-form">
          <input
            value={noteTitle}
            onChange={(event) => setNoteTitle(event.target.value)}
            placeholder="Titulo da evidencia"
            disabled={!selected || saving}
          />
          <textarea
            value={noteDescription}
            onChange={(event) => setNoteDescription(event.target.value)}
            placeholder="Descreva a acao executada, comprovante recebido ou contato com o titular"
            disabled={!selected || saving}
          />
          <button
            type="button"
            className="subject-requests-page__button"
            onClick={() => void handleAddEvidence()}
            disabled={!selected || saving}
          >
            Registrar evidencia
          </button>
        </div>

        <div className="subject-requests-page__timeline">
          {events.length === 0 ? (
            <div className="subject-requests-page__empty">
              Nenhum evento registrado para a solicitacao selecionada.
            </div>
          ) : (
            events.map((event) => (
              <article key={event.id} className="subject-requests-page__event">
                <div className="subject-requests-page__event-head">
                  <strong>{event.titulo}</strong>
                  <span>{event.createdAt.slice(0, 16).replace("T", " ")}</span>
                </div>
                <span className="subject-requests-page__badge">
                  {event.tipoEvento}
                </span>
                {event.descricao ? <p>{event.descricao}</p> : null}
                {event.criadoPorId ? (
                  <small>Criado por usuario #{event.criadoPorId}</small>
                ) : null}
                {event.detalhesJson ? (
                  <pre>{JSON.stringify(event.detalhesJson, null, 2)}</pre>
                ) : null}
              </article>
            ))
          )}
        </div>
      </section>
    </div>
  );
}
