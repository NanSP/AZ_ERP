import { AxiosError } from "axios";
import { useMemo, useState, type FormEvent } from "react";
import { useAuth } from "../../auth/useAuth";
import {
  provisionTenant,
  type TenantProvisioningPayload,
  type TenantProvisioningResponse,
} from "../../services/tenantProvisioningService";
import "./tenant-provisioning-form.css";

type FormState = {
  codigo: string;
  nome: string;
  nomeFantasia: string;
  documento: string;
  tipoDocumento: string;
  emailResponsavel: string;
  telefoneResponsavel: string;
  plano: string;
  databaseName: string;
  adminNome: string;
  adminEmail: string;
  adminLogin: string;
  adminSenha: string;
};

const initialState: FormState = {
  codigo: "",
  nome: "",
  nomeFantasia: "",
  documento: "",
  tipoDocumento: "",
  emailResponsavel: "",
  telefoneResponsavel: "",
  plano: "PROFESSIONAL",
  databaseName: "",
  adminNome: "",
  adminEmail: "",
  adminLogin: "",
  adminSenha: "",
};

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message = error.response?.data?.message;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  return fallback;
}

function normalizeOptional(value: string) {
  const normalized = value.trim();
  return normalized === "" ? null : normalized;
}

function buildDatabaseName(codigo: string) {
  const normalized = codigo
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "_")
    .replace(/^_+|_+$/g, "");

  return normalized ? `az_erp_tenant_${normalized}` : "";
}

export default function TenantProvisioningForm() {
  const { session } = useAuth();
  const [form, setForm] = useState<FormState>(initialState);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<TenantProvisioningResponse | null>(null);

  const systemUserId = session?.scope === "master" ? session.userId : 0;
  const generatedDatabaseName = useMemo(
    () => buildDatabaseName(form.codigo),
    [form.codigo],
  );

  function updateField<K extends keyof FormState>(field: K, value: FormState[K]) {
    setForm((current) => ({
      ...current,
      [field]: value,
      ...(field === "codigo" && current.databaseName.trim() === buildDatabaseName(current.codigo)
        ? { databaseName: buildDatabaseName(String(value)) }
        : {}),
    }));
  }

  function applySuggestedDatabaseName() {
    setForm((current) => ({
      ...current,
      databaseName: generatedDatabaseName,
    }));
  }

  function resetForm() {
    setForm(initialState);
    setError(null);
    setSuccess(null);
  }

  function validate() {
    if (!systemUserId) {
      return "Sessao master invalida para provisionamento.";
    }

    if (!form.codigo.trim()) {
      return "Informe o codigo do tenant.";
    }

    if (!form.nome.trim()) {
      return "Informe a razao social do tenant.";
    }

    if (!form.plano.trim()) {
      return "Selecione o plano do tenant.";
    }

    if (!form.databaseName.trim()) {
      return "Informe o nome fisico do banco do tenant.";
    }

    if (!/^[a-zA-Z0-9_]+$/.test(form.databaseName.trim())) {
      return "O nome do banco deve conter apenas letras, numeros e underscore.";
    }

    if (form.documento.trim() && !form.tipoDocumento.trim()) {
      return "Selecione o tipo de documento ao informar CPF ou CNPJ.";
    }

    if (form.tipoDocumento === "CPF" && form.documento.trim() && !/^\d{11}$/.test(form.documento.trim())) {
      return "CPF deve conter 11 digitos numericos.";
    }

    if (form.tipoDocumento === "CNPJ" && form.documento.trim() && !/^\d{14}$/.test(form.documento.trim())) {
      return "CNPJ deve conter 14 digitos numericos.";
    }

    if (!form.adminNome.trim()) {
      return "Informe o nome do administrador inicial.";
    }

    if (!form.adminEmail.trim()) {
      return "Informe o email do administrador inicial.";
    }

    if (!form.adminLogin.trim()) {
      return "Informe o login do administrador inicial.";
    }

    if (!form.adminSenha.trim()) {
      return "Informe a senha do administrador inicial.";
    }

    if (form.adminSenha.trim().length < 12) {
      return "A senha do administrador inicial deve ter pelo menos 12 caracteres.";
    }

    return null;
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setSuccess(null);

    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }

    const payload: TenantProvisioningPayload = {
      systemUserId,
      codigo: form.codigo.trim(),
      nome: form.nome.trim(),
      nomeFantasia: normalizeOptional(form.nomeFantasia),
      documento: normalizeOptional(form.documento),
      tipoDocumento: normalizeOptional(form.tipoDocumento),
      emailResponsavel: normalizeOptional(form.emailResponsavel),
      telefoneResponsavel: normalizeOptional(form.telefoneResponsavel),
      plano: form.plano.trim(),
      databaseName: form.databaseName.trim(),
      dbHost: "server-managed",
      dbPort: 5432,
      dbUsername: "server-managed",
      dbPassword: "server-managed",
      adminNome: form.adminNome.trim(),
      adminEmail: form.adminEmail.trim(),
      adminLogin: form.adminLogin.trim(),
      adminSenha: form.adminSenha,
    };

    setLoading(true);

    try {
      const response = await provisionTenant(payload);
      setSuccess(response.data);
      setForm(initialState);
    } catch (submitError) {
      setError(
        getErrorMessage(
          submitError,
          "Nao foi possivel concluir o provisionamento do tenant.",
        ),
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="tenant-provisioning">
      <header className="tenant-provisioning__header">
        <div>
          <span className="tenant-provisioning__eyebrow">Provisionamento</span>
          <h3 className="tenant-provisioning__title">Onboarding completo</h3>
          <p className="tenant-provisioning__subtitle">
            Este fluxo cria o tenant, registra a base, provisiona o banco fisico
            e inclui o administrador inicial.
          </p>
        </div>

        <div className="tenant-provisioning__meta-card">
          <span className="tenant-provisioning__meta-label">
            Usuario executor
          </span>
          <strong className="tenant-provisioning__meta-value">
            #{systemUserId || "-"}
          </strong>
        </div>
      </header>

      {error ? (
        <div className="tenant-provisioning__alert tenant-provisioning__alert--error">
          {error}
        </div>
      ) : null}

      {success ? (
        <div className="tenant-provisioning__alert tenant-provisioning__alert--success">
          <strong>Provisionamento concluido.</strong> Tenant{" "}
          <strong>{success.tenantCodigo}</strong>, base{" "}
          <strong>{success.databaseName}</strong> e admin{" "}
          <strong>{success.adminLogin}</strong> criados com sucesso.
        </div>
      ) : null}

      <form className="tenant-provisioning__form" onSubmit={handleSubmit}>
        <section className="tenant-provisioning__section">
          <div className="tenant-provisioning__section-head">
            <div>
              <h4 className="tenant-provisioning__section-title">Dados do tenant</h4>
              <p className="tenant-provisioning__section-text">
                Informacoes cadastrais principais da empresa que sera onboarded.
              </p>
            </div>
          </div>

          <div className="tenant-provisioning__grid">
            <label className="tenant-provisioning__field">
              <span>Codigo</span>
              <input
                value={form.codigo}
                onChange={(event) => updateField("codigo", event.target.value)}
                placeholder="Ex.: TEST3"
              />
            </label>

            <label className="tenant-provisioning__field">
              <span>Plano</span>
              <select
                value={form.plano}
                onChange={(event) => updateField("plano", event.target.value)}
              >
                <option value="STARTER">Starter</option>
                <option value="PROFESSIONAL">Professional</option>
                <option value="ENTERPRISE">Enterprise</option>
              </select>
            </label>

            <label className="tenant-provisioning__field tenant-provisioning__field--span-2">
              <span>Razao social</span>
              <input
                value={form.nome}
                onChange={(event) => updateField("nome", event.target.value)}
                placeholder="Ex.: Test 3 Ltda"
              />
            </label>

            <label className="tenant-provisioning__field tenant-provisioning__field--span-2">
              <span>Nome fantasia</span>
              <input
                value={form.nomeFantasia}
                onChange={(event) => updateField("nomeFantasia", event.target.value)}
                placeholder="Ex.: Test3"
              />
            </label>

            <label className="tenant-provisioning__field">
              <span>Tipo de documento</span>
              <select
                value={form.tipoDocumento}
                onChange={(event) => updateField("tipoDocumento", event.target.value)}
              >
                <option value="">Selecione</option>
                <option value="CNPJ">CNPJ</option>
                <option value="CPF">CPF</option>
              </select>
            </label>

            <label className="tenant-provisioning__field">
              <span>Documento</span>
              <input
                value={form.documento}
                onChange={(event) => updateField("documento", event.target.value)}
                placeholder="Somente numeros"
              />
            </label>

            <label className="tenant-provisioning__field">
              <span>Email do responsavel</span>
              <input
                type="email"
                value={form.emailResponsavel}
                onChange={(event) => updateField("emailResponsavel", event.target.value)}
                placeholder="contato@empresa.com"
              />
            </label>

            <label className="tenant-provisioning__field">
              <span>Telefone do responsavel</span>
              <input
                value={form.telefoneResponsavel}
                onChange={(event) => updateField("telefoneResponsavel", event.target.value)}
                placeholder="71999999999"
              />
            </label>
          </div>
        </section>

        <section className="tenant-provisioning__section">
          <div className="tenant-provisioning__section-head">
            <div>
              <h4 className="tenant-provisioning__section-title">Base fisica</h4>
              <p className="tenant-provisioning__section-text">
                O nome do banco e unico por tenant. Host, usuario e senha sao
                resolvidos automaticamente pelo backend.
              </p>
            </div>

            <button
              type="button"
              className="tenant-provisioning__ghost"
              onClick={applySuggestedDatabaseName}
              disabled={!generatedDatabaseName}
            >
              Sugerir nome
            </button>
          </div>

          <div className="tenant-provisioning__grid">
            <label className="tenant-provisioning__field tenant-provisioning__field--span-2">
              <span>Nome do banco</span>
              <input
                value={form.databaseName}
                onChange={(event) => updateField("databaseName", event.target.value)}
                placeholder="Ex.: az_erp_tenant_test3"
              />
              {generatedDatabaseName ? (
                <small>
                  Sugestao com base no codigo: <strong>{generatedDatabaseName}</strong>
                </small>
              ) : null}
            </label>
          </div>
        </section>

        <section className="tenant-provisioning__section">
          <div className="tenant-provisioning__section-head">
            <div>
              <h4 className="tenant-provisioning__section-title">Administrador inicial</h4>
              <p className="tenant-provisioning__section-text">
                Usuario criado na base do tenant e registrado na camada master.
              </p>
            </div>
          </div>

          <div className="tenant-provisioning__grid">
            <label className="tenant-provisioning__field tenant-provisioning__field--span-2">
              <span>Nome</span>
              <input
                value={form.adminNome}
                onChange={(event) => updateField("adminNome", event.target.value)}
                placeholder="Administrador Test3"
              />
            </label>

            <label className="tenant-provisioning__field">
              <span>Email</span>
              <input
                type="email"
                value={form.adminEmail}
                onChange={(event) => updateField("adminEmail", event.target.value)}
                placeholder="admin@test3.com"
              />
            </label>

            <label className="tenant-provisioning__field">
              <span>Login</span>
              <input
                value={form.adminLogin}
                onChange={(event) => updateField("adminLogin", event.target.value)}
                placeholder="admin.test3"
              />
            </label>

            <label className="tenant-provisioning__field tenant-provisioning__field--span-2">
              <span>Senha</span>
              <input
                type="password"
                value={form.adminSenha}
                onChange={(event) => updateField("adminSenha", event.target.value)}
                placeholder="Minimo de 12 caracteres"
              />
            </label>
          </div>
        </section>

        <div className="tenant-provisioning__actions">
          <button
            type="button"
            className="tenant-provisioning__ghost"
            onClick={resetForm}
            disabled={loading}
          >
            Limpar formulario
          </button>
          <button
            type="submit"
            className="tenant-provisioning__button"
            disabled={loading}
          >
            {loading ? "Provisionando..." : "Provisionar tenant"}
          </button>
        </div>
      </form>

    </div>
  );
}
