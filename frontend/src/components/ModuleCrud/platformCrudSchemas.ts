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
};

export type CrudFormSchema = {
  fields: CrudFieldDefinition[];
};

const statusOptions = [
  { label: "Ativo", value: "ATIVO" },
  { label: "Suspenso", value: "SUSPENSO" },
  { label: "Inativo", value: "INATIVO" },
];

const roleOptions = [
  { label: "Admin sistema", value: "ADMIN_SISTEMA" },
  { label: "Suporte", value: "SUPORTE" },
];

const tenantAdminRoleOptions = [
  { label: "Admin tenant", value: "ADMIN" },
  { label: "Gestor", value: "GESTOR" },
  { label: "Operador", value: "OPERADOR" },
];

const platformCrudSchemas: Record<string, CrudFormSchema> = {
  "platform.systemUsers": {
    fields: [
      { name: "nome", label: "Nome", type: "text", required: true },
      { name: "email", label: "Email", type: "email", required: true },
      { name: "login", label: "Login", type: "text", required: true },
      {
        name: "senha",
        label: "Senha",
        type: "password",
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
        options: statusOptions,
      },
      {
        name: "plano",
        label: "Plano",
        type: "text",
        options: [
          { label: "Starter", value: "STARTER" },
          { label: "Business", value: "BUSINESS" },
          { label: "Enterprise", value: "ENTERPRISE" },
        ],
      },
      {
        name: "schemaVersion",
        label: "Schema version",
        type: "text",
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
      { name: "templateName", label: "Template", type: "text" },
      { name: "dbHost", label: "Host", type: "text", required: true },
      { name: "dbPort", label: "Porta", type: "number", required: true },
      { name: "dbUsername", label: "Usuario do banco", type: "text" },
      { name: "dbPassword", label: "Senha do banco", type: "password" },
      {
        name: "provisionStatus",
        label: "Status de provisionamento",
        type: "text",
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
      },
    ],
  },
};

export function getCrudFormSchema(schema: string, entity: string) {
  return platformCrudSchemas[`${schema}.${entity}`] ?? null;
}

export function createEmptyFormValues(schema: CrudFormSchema) {
  return Object.fromEntries(schema.fields.map((field) => [field.name, ""]));
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
    const rawValue = (values[field.name] ?? "").trim();

    if (rawValue === "") {
      payload[field.name] = null;
      continue;
    }

    if (field.type === "number") {
      payload[field.name] = Number(rawValue);
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
