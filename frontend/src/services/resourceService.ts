import { api } from "./api";

export type ResourceKey = {
  schema: string;
  entity: string;
  label: string;
};

export const modules: ResourceKey[] = [
  { schema: "core", entity: "empresas", label: "Empresas" },
  { schema: "core", entity: "parceiros", label: "Parceiros" },
  { schema: "fi", entity: "contasPagar", label: "Contas a Pagar" },
  { schema: "fi", entity: "contasReceber", label: "Contas a Receber" },
  { schema: "fi", entity: "planoContas", label: "Plano de Contas" },
  { schema: "mm", entity: "compras", label: "Compras" },
  { schema: "mm", entity: "compraItens", label: "Itens de Compra" },
  { schema: "mm", entity: "estoques", label: "Estoques" },
  { schema: "mm", entity: "movimentacoes", label: "Movimentações" },
  { schema: "rh", entity: "colaboradores", label: "Colaboradores" },
  { schema: "rh", entity: "folhaDePagamento", label: "Folha de Pagamento" },
  { schema: "ps", entity: "projetos", label: "Projetos" },
  { schema: "ps", entity: "tarefas", label: "Tarefas" },
  { schema: "pp", entity: "ordemProducao", label: "Ordens de Produção" },
  { schema: "qm", entity: "inspecoes", label: "Inspeções" },
  { schema: "grc", entity: "auditorias", label: "Auditorias" },
  { schema: "portal", entity: "notificacoes", label: "Notificações" },
  { schema: "auditoria", entity: "logAcoes", label: "Log Ações" },
  { schema: "auditoria", entity: "logErros", label: "Log Erros" },
  { schema: "fiscal", entity: "documentos", label: "Documentos Fiscais" },
  { schema: "bi", entity: "relatorios", label: "Relatórios" },
];

const resourceUrl = (schema: string, entity: string) => `/${schema}/${entity}`;

export const listResource = (schema: string, entity: string) =>
  api.get(resourceUrl(schema, entity));
export const getResource = (schema: string, entity: string, id: number) =>
  api.get(`${resourceUrl(schema, entity)}/${id}`);
export const createResource = (schema: string, entity: string, payload: any) =>
  api.post(resourceUrl(schema, entity), payload);
export const updateResource = (
  schema: string,
  entity: string,
  id: number,
  payload: any,
) => api.put(`${resourceUrl(schema, entity)}/${id}`, payload);
export const deleteResource = (schema: string, entity: string, id: number) =>
  api.delete(`${resourceUrl(schema, entity)}/${id}`);
