export type CrudFieldType =
  | "text"
  | "email"
  | "password"
  | "number"
  | "textarea"
  | "datetime-local"
  | "json";

export type CrudFieldOption = {
  label: string;
  value: string;
};

export type CrudFieldDefinition = {
  name: string;
  label: string;
  type: CrudFieldType;
  required?: boolean;
  placeholder?: string;
  options?: CrudFieldOption[];
  defaultValue?: string;
  description?: string;
  readOnly?: boolean;
  serverManaged?: boolean;
};

export type CrudFormSchema = {
  fields: CrudFieldDefinition[];
};

const statusOptions = [
  { label: "Ativo", value: "ATIVO" },
  { label: "Suspenso", value: "SUSPENSO" },
  { label: "Inativo", value: "INATIVO" },
];

const tenantStatusOptions = [
  { label: "Pendente", value: "PENDENTE" },
  { label: "Ativo", value: "ATIVO" },
  { label: "Suspenso", value: "SUSPENSO" },
  { label: "Inativo", value: "INATIVO" },
];

const roleOptions = [
  { label: "Master admin", value: "MASTER_ADMIN" },
  { label: "Support", value: "SUPPORT" },
  { label: "Operations", value: "OPERATIONS" },
];

const tenantAdminRoleOptions = [
  { label: "Master admin", value: "MASTER_ADMIN" },
  { label: "Tenant admin", value: "TENANT_ADMIN" },
  { label: "Support", value: "SUPPORT" },
];

const platformCrudSchemas: Record<string, CrudFormSchema> = {
  "grc.registrosTratamento": {
    fields: [
      { name: "modulo", label: "Modulo", type: "text", required: true },
      { name: "entidade", label: "Entidade", type: "text", required: true },
      {
        name: "finalidade",
        label: "Finalidade",
        type: "textarea",
        required: true,
      },
      {
        name: "baseLegal",
        label: "Base legal",
        type: "text",
        required: true,
        options: [
          { label: "Consentimento", value: "consentimento" },
          { label: "Execucao de contrato", value: "execucao_contrato" },
          { label: "Obrigacao legal", value: "obrigacao_legal" },
          { label: "Legitimo interesse", value: "legitimo_interesse" },
          { label: "Exercicio regular de direitos", value: "exercicio_regular_direitos" },
          { label: "Protecao ao credito", value: "protecao_credito" },
        ],
      },
      {
        name: "categoriaTitular",
        label: "Categoria do titular",
        type: "text",
        required: true,
        options: [
          { label: "Cliente", value: "cliente" },
          { label: "Colaborador", value: "colaborador" },
          { label: "Fornecedor", value: "fornecedor" },
          { label: "Usuario", value: "usuario" },
          { label: "Visitante", value: "visitante" },
          { label: "Outro", value: "outro" },
        ],
      },
      {
        name: "categoriaDados",
        label: "Categoria dos dados",
        type: "text",
        required: true,
        options: [
          { label: "Cadastral", value: "cadastral" },
          { label: "Contato", value: "contato" },
          { label: "Financeiro", value: "financeiro" },
          { label: "Acesso", value: "acesso" },
          { label: "Trabalhista", value: "trabalhista" },
          { label: "Fiscal", value: "fiscal" },
          { label: "Sensivel", value: "sensivel" },
          { label: "Operacional", value: "operacional" },
        ],
      },
      { name: "retencaoDias", label: "Retencao em dias", type: "number" },
      {
        name: "compartilhamento",
        label: "Compartilhamento",
        type: "text",
        placeholder: "Ex.: contador, integrador, banco parceiro",
      },
      {
        name: "requerConsentimento",
        label: "Requer consentimento",
        type: "text",
        required: true,
        defaultValue: "false",
        options: [
          { label: "Sim", value: "true" },
          { label: "Nao", value: "false" },
        ],
      },
      {
        name: "ativo",
        label: "Registro ativo",
        type: "text",
        required: true,
        defaultValue: "true",
        options: [
          { label: "Sim", value: "true" },
          { label: "Nao", value: "false" },
        ],
      },
      { name: "responsavel", label: "Responsavel ID", type: "number" },
      { name: "observacoes", label: "Observacoes", type: "textarea" },
    ],
  },
  "grc.solicitacoesTitular": {
    fields: [
      { name: "titularNome", label: "Nome do titular", type: "text", required: true },
      { name: "titularContato", label: "Contato do titular", type: "text", required: true },
      {
        name: "tipoTitular",
        label: "Tipo do titular",
        type: "text",
        required: true,
        options: [
          { label: "Cliente", value: "cliente" },
          { label: "Colaborador", value: "colaborador" },
          { label: "Fornecedor", value: "fornecedor" },
          { label: "Usuario", value: "usuario" },
          { label: "Visitante", value: "visitante" },
          { label: "Outro", value: "outro" },
        ],
      },
      {
        name: "direitoSolicitado",
        label: "Direito solicitado",
        type: "text",
        required: true,
        options: [
          { label: "Confirmacao", value: "confirmacao" },
          { label: "Acesso", value: "acesso" },
          { label: "Correcao", value: "correcao" },
          { label: "Anonimizacao", value: "anonimizacao" },
          { label: "Eliminacao", value: "eliminacao" },
          { label: "Portabilidade", value: "portabilidade" },
          { label: "Oposicao", value: "oposicao" },
          { label: "Revogacao de consentimento", value: "revogacao_consentimento" },
          { label: "Revisao", value: "revisao" },
        ],
      },
      { name: "modulo", label: "Modulo relacionado", type: "text" },
      { name: "entidade", label: "Entidade relacionada", type: "text" },
      {
        name: "status",
        label: "Status",
        type: "text",
        defaultValue: "aberta",
        options: [
          { label: "Aberta", value: "aberta" },
          { label: "Em analise", value: "em_analise" },
          { label: "Aguardando titular", value: "aguardando_titular" },
          { label: "Concluida", value: "concluida" },
          { label: "Indeferida", value: "indeferida" },
        ],
      },
      {
        name: "canalOrigem",
        label: "Canal de origem",
        type: "text",
        options: [
          { label: "Portal", value: "portal" },
          { label: "Email", value: "email" },
          { label: "Telefone", value: "telefone" },
          { label: "Presencial", value: "presencial" },
          { label: "Suporte", value: "suporte" },
        ],
      },
      { name: "detalhes", label: "Detalhes", type: "textarea" },
      { name: "prazoResposta", label: "Prazo de resposta", type: "datetime-local" },
      { name: "dataConclusao", label: "Data de conclusao", type: "datetime-local" },
      { name: "respostaResumo", label: "Resposta resumo", type: "textarea" },
      { name: "atendidoPor", label: "Atendido por ID", type: "number" },
    ],
  },
  "platform.systemUsers": {
    fields: [
      { name: "nome", label: "Nome", type: "text", required: true },
      { name: "email", label: "Email", type: "email", required: true },
      { name: "login", label: "Login", type: "text", required: true },
      {
        name: "senha",
        label: "Senha",
        type: "password",
        required: true,
        placeholder: "Preencha para criar ou redefinir",
      },
      {
        name: "role",
        label: "Perfil master",
        type: "text",
        required: true,
        options: roleOptions,
      },
      {
        name: "status",
        label: "Status",
        type: "text",
        required: true,
        options: statusOptions,
      },
    ],
  },
  "platform.tenants": {
    fields: [
      { name: "codigo", label: "Codigo", type: "text", required: true },
      { name: "nome", label: "Razao social", type: "text", required: true },
      { name: "nomeFantasia", label: "Nome fantasia", type: "text" },
      { name: "documento", label: "Documento", type: "text" },
      {
        name: "tipoDocumento",
        label: "Tipo de documento",
        type: "text",
        options: [
          { label: "CNPJ", value: "CNPJ" },
          { label: "CPF", value: "CPF" },
        ],
      },
      {
        name: "emailResponsavel",
        label: "Email do responsavel",
        type: "email",
      },
      {
        name: "telefoneResponsavel",
        label: "Telefone do responsavel",
        type: "text",
      },
      {
        name: "status",
        label: "Status",
        type: "text",
        options: tenantStatusOptions,
      },
      {
        name: "plano",
        label: "Plano",
        type: "text",
        options: [
          { label: "Starter", value: "STARTER" },
          { label: "Professional", value: "PROFESSIONAL" },
          { label: "Enterprise", value: "ENTERPRISE" },
        ],
      },
      {
        name: "schemaVersion",
        label: "Schema version",
        type: "text",
        placeholder: "Ex.: V1",
      },
      {
        name: "observacoes",
        label: "Observacoes",
        type: "textarea",
        placeholder: "Notas internas sobre o tenant",
      },
    ],
  },
  "platform.tenantDatabases": {
    fields: [
      { name: "tenantId", label: "Tenant ID", type: "number", required: true },
      {
        name: "databaseName",
        label: "Nome do banco",
        type: "text",
        required: true,
      },
      {
        name: "templateName",
        label: "Template",
        type: "text",
        required: true,
        defaultValue: "Gerenciado pelo servidor",
        description: "O template efetivo e definido pela configuracao do backend.",
        readOnly: true,
        serverManaged: true,
      },
      {
        name: "dbHost",
        label: "Host",
        type: "text",
        required: true,
        defaultValue: "Gerenciado pelo servidor",
        description: "O host de conexao e aplicado automaticamente no provisionamento.",
        readOnly: true,
        serverManaged: true,
      },
      {
        name: "dbPort",
        label: "Porta",
        type: "number",
        required: true,
        defaultValue: "5432",
        description: "A porta padrao e controlada pelo ambiente do backend.",
        readOnly: true,
        serverManaged: true,
      },
      {
        name: "dbUsername",
        label: "Usuario do banco",
        type: "text",
        required: true,
        defaultValue: "Gerenciado pelo servidor",
        description: "O usuario tecnico e resolvido pelo backend no momento do provisionamento.",
        readOnly: true,
        serverManaged: true,
      },
      {
        name: "dbPassword",
        label: "Senha do banco",
        type: "password",
        required: true,
        defaultValue: "********",
        description: "A credencial tecnica e mantida apenas no servidor.",
        readOnly: true,
        serverManaged: true,
      },
      {
        name: "provisionStatus",
        label: "Status de provisionamento",
        type: "text",
        defaultValue: "PENDENTE",
        description: "O status ATIVO e atualizado automaticamente pelo fluxo de provisionamento fisico.",
        readOnly: true,
        serverManaged: true,
        options: [
          { label: "Pendente", value: "PENDENTE" },
          { label: "Ativo", value: "ATIVO" },
          { label: "Erro", value: "ERRO" },
          { label: "Suspenso", value: "SUSPENSO" },
        ],
      },
      {
        name: "lastCheckAt",
        label: "Ultima verificacao",
        type: "datetime-local",
        description: "Preenchido automaticamente quando o backend verifica ou provisiona a base.",
        readOnly: true,
        serverManaged: true,
      },
    ],
  },
  "platform.tenantAdminUsers": {
    fields: [
      { name: "tenantId", label: "Tenant ID", type: "number", required: true },
      { name: "nome", label: "Nome", type: "text", required: true },
      { name: "email", label: "Email", type: "email", required: true },
      { name: "login", label: "Login", type: "text", required: true },
      { name: "senha", label: "Senha", type: "password", required: true },
      {
        name: "role",
        label: "Perfil do tenant",
        type: "text",
        required: true,
        options: tenantAdminRoleOptions,
      },
      {
        name: "status",
        label: "Status",
        type: "text",
        required: true,
        options: statusOptions,
      },
    ],
  },
  "platform.provisioningLogs": {
    fields: [
      { name: "tenantId", label: "Tenant ID", type: "number" },
      { name: "etapa", label: "Etapa", type: "text", required: true },
      {
        name: "status",
        label: "Status",
        type: "text",
        required: true,
        options: [
          { label: "Info", value: "INFO" },
          { label: "Sucesso", value: "SUCESSO" },
          { label: "Erro", value: "ERRO" },
        ],
      },
      { name: "mensagem", label: "Mensagem", type: "textarea", required: true },
      {
        name: "detalhes",
        label: "Detalhes estruturados",
        type: "json",
        placeholder: '{\n  "chave": "valor"\n}',
      },
      {
        name: "executadoPorId",
        label: "Executado por ID",
        type: "number",
        required: true,
      },
    ],
  },
};

export function getCrudFormSchema(schema: string, entity: string) {
  return platformCrudSchemas[`${schema}.${entity}`] ?? null;
}

export function createEmptyFormValues(schema: CrudFormSchema) {
  return Object.fromEntries(
    schema.fields.map((field) => [field.name, field.defaultValue ?? ""]),
  );
}

export function populateFormValues(
  schema: CrudFormSchema,
  item: Record<string, unknown> | null,
) {
  const empty = createEmptyFormValues(schema);

  if (!item) {
    return empty;
  }

  return Object.fromEntries(
    schema.fields.map((field) => {
      const raw = item[field.name];

      if (raw == null) {
        return [field.name, ""];
      }

      if (field.type === "json") {
        return [field.name, JSON.stringify(raw, null, 2)];
      }

      if (field.type === "datetime-local") {
        return [field.name, String(raw).slice(0, 16)];
      }

      return [field.name, String(raw)];
    }),
  );
}

export function buildPayloadFromForm(
  schema: CrudFormSchema,
  values: Record<string, string>,
) {
  const payload: Record<string, unknown> = {};

  for (const field of schema.fields) {
    if (field.serverManaged) {
      continue;
    }

    const rawValue = (values[field.name] ?? "").trim();

    if (rawValue === "") {
      payload[field.name] = null;
      continue;
    }

    if (field.type === "number") {
      payload[field.name] = Number(rawValue);
      continue;
    }

    if (rawValue === "true") {
      payload[field.name] = true;
      continue;
    }

    if (rawValue === "false") {
      payload[field.name] = false;
      continue;
    }

    if (field.type === "json") {
      payload[field.name] = JSON.parse(rawValue) as Record<string, unknown>;
      continue;
    }

    payload[field.name] = rawValue;
  }

  return payload;
}
