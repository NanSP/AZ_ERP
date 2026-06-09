import type { User } from "../../pages/Sys/UsersPage";

type UserFormProps = {
  value: User;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: User) => void;
  onSave: () => void;
  onReset: () => void;
};

const tipoUsuarioOptions = [
  { value: "admin", label: "Admin" },
  { value: "gestor", label: "Gestor" },
  { value: "operador", label: "Operador" },
  { value: "cliente", label: "Cliente" },
];

const statusOptions = [
  { value: "ativo", label: "Ativo" },
  { value: "inativo", label: "Inativo" },
  { value: "bloqueado", label: "Bloqueado" },
];

export default function UserForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: UserFormProps) {
  const canSave =
    value.nome.trim() !== "" &&
    value.email.trim() !== "" &&
    value.login.trim() !== "" &&
    (editing || value.senha.trim().length >= 6);

  function update<K extends keyof User>(field: K, fieldValue: User[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="user-form">
      <div className="user-form__head">
        <div>
          <h3 className="user-form__title">
            {editing ? "Editar usuario" : "Novo usuario"}
          </h3>
          <p className="user-form__subtitle">
            Configure identidade, credenciais e status operacional da conta.
          </p>
          {editing && value.id ? (
            <p className="user-form__meta">Registro selecionado: #{value.id}</p>
          ) : !canEditFields ? (
            <p className="user-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="user-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="user-form__grid">
        <label className="user-form__field user-form__field--span-2">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Nome completo do usuario"
            disabled={!canEditFields}
          />
        </label>

        <label className="user-form__field">
          <span>E-mail</span>
          <input
            type="email"
            value={value.email}
            onChange={(event) => update("email", event.target.value)}
            placeholder="usuario@empresa.com"
            disabled={!canEditFields}
          />
        </label>

        <label className="user-form__field">
          <span>Login</span>
          <input
            value={value.login}
            onChange={(event) => update("login", event.target.value)}
            placeholder="login.usuario"
            disabled={!canEditFields}
          />
        </label>

        <label className="user-form__field">
          <span>{editing ? "Nova senha" : "Senha"}</span>
          <input
            type="password"
            value={value.senha}
            onChange={(event) => update("senha", event.target.value)}
            placeholder={
              editing ? "Preencha apenas para trocar" : "Minimo 6 caracteres"
            }
            disabled={!canEditFields}
          />
        </label>

        <label className="user-form__field">
          <span>Documento</span>
          <input
            value={value.documento}
            onChange={(event) => update("documento", event.target.value)}
            placeholder="CPF ou identificador"
            disabled={!canEditFields}
          />
        </label>

        <label className="user-form__field">
          <span>Tipo de usuário</span>
          <select
            value={value.tipoUsuario}
            onChange={(event) => update("tipoUsuario", event.target.value)}
            disabled={!canEditFields}
          >
            {tipoUsuarioOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="user-form__field">
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

        <label className="user-form__field">
          <span>Expiração da senha</span>
          <input
            type="date"
            value={value.expiracaoSenha}
            onChange={(event) => update("expiracaoSenha", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="user-form__field">
          <span>Tentativas de login</span>
          <input
            value={value.tentativasLogin}
            onChange={(event) =>
              update("tentativasLogin", event.target.value.replace(/\D/g, ""))
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="user-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alterações"
            : "Criar usuário"}
      </button>
    </aside>
  );
}
