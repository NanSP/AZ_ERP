import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import DeviceForm from "../../components/Portal/DeviceForm";
import DevicesTable from "../../components/Portal/DevicesTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./devices-page.css";

export type UserOption = {
  id: number;
  label: string;
};

export type UserAccess = "idle" | "loaded" | "unavailable";

export type Device = {
  id?: number;
  usuario: string;
  deviceId: string;
  deviceModel: string;
  devicePlatform: string;
  pushToken: string;
  ultimoAcesso: string;
  ativo: boolean;
  createdAt?: string;
};

type DevicesPageProps = {
  embedded?: boolean;
};

const devicesResource = {
  schema: "portal",
  entity: "dispositivos",
  label: "Dispositivos",
  description: "Dispositivos e acessos cadastrados.",
} as const;

const usersResource = {
  schema: "sys",
  entity: "usuarios",
  label: "Usuarios",
  description: "Usuarios do tenant.",
} as const;

const emptyDevice: Device = {
  usuario: "",
  deviceId: "",
  deviceModel: "",
  devicePlatform: "web",
  pushToken: "",
  ultimoAcesso: "",
  ativo: true,
};

function normalizeDateTime(value: unknown) {
  if (value == null) {
    return "";
  }

  const raw = String(value);
  return raw.length >= 16 ? raw.slice(0, 16) : raw;
}

function normalizeDevice(data: Record<string, unknown>): Device {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    usuario: data.usuario == null ? "" : String(data.usuario),
    deviceId: String(data.deviceId ?? ""),
    deviceModel: String(data.deviceModel ?? ""),
    devicePlatform: String(data.devicePlatform ?? "web"),
    pushToken: String(data.pushToken ?? ""),
    ultimoAcesso: normalizeDateTime(data.ultimoAcesso),
    ativo: data.ativo == null ? true : Boolean(data.ativo),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(device: Device) {
  return {
    usuario: device.usuario.trim() === "" ? null : Number(device.usuario),
    deviceId: device.deviceId.trim() || null,
    deviceModel: device.deviceModel.trim() || null,
    devicePlatform: device.devicePlatform.trim() || null,
    pushToken: device.pushToken.trim() || null,
    ultimoAcesso: device.ultimoAcesso.trim() || null,
    ativo: device.ativo,
  };
}

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message = error.response?.data?.message;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  return fallback;
}

function normalizeUserOption(data: Record<string, unknown>): UserOption | null {
  if (typeof data.id !== "number") {
    return null;
  }

  return {
    id: data.id,
    label: `${String(data.nome ?? data.login ?? "Usuario")} (#${String(data.id)})`,
  };
}

export default function DevicesPage({ embedded = false }: DevicesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<Device[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Device | null>(null);
  const [draft, setDraft] = useState<Device>(emptyDevice);
  const [userOptions, setUserOptions] = useState<UserOption[]>([]);
  const [userAccess, setUserAccess] = useState<UserAccess>("idle");
  const canRead = canAccessResourceAction(session, devicesResource, "read");
  const canCreate = canAccessResourceAction(session, devicesResource, "create");
  const canUpdate = canAccessResourceAction(session, devicesResource, "update");
  const canDelete = canAccessResourceAction(session, devicesResource, "delete");
  const canReadUsers = canAccessResourceAction(session, usersResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadDevices = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyDevice });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("portal", "dispositivos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeDevice(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possivel carregar os dispositivos."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadUsers = useCallback(async () => {
    if (!canReadUsers) {
      setUserOptions([]);
      setUserAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("sys", "usuarios");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => normalizeUserOption(item as Record<string, unknown>))
            .filter((item): item is UserOption => item !== null)
        : [];
      setUserOptions(nextItems);
      setUserAccess("loaded");
    } catch {
      setUserOptions([]);
      setUserAccess("unavailable");
    }
  }, [canReadUsers]);

  useEffect(() => {
    void loadDevices();
  }, [loadDevices]);

  useEffect(() => {
    void loadUsers();
  }, [loadUsers]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.deviceId, item.deviceModel, item.devicePlatform, item.pushToken]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyDevice });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Device) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Device) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar dispositivos."
          : "Seu perfil não possui permissão para criar dispositivos.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("portal", "dispositivos", selected.id, payload)
        : await createResource("portal", "dispositivos", payload);

      const saved = normalizeDevice(response.data as Record<string, unknown>);
      await loadDevices();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Dispositivo atualizado com sucesso."
          : "Dispositivo criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possivel atualizar o dispositivo."
            : "Não foi possivel criar o dispositivo.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Device) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir dispositivos.");
      return;
    }

    if (!item.id) {
      setError("Não foi possivel identificar o dispositivo para exclusão.");
      return;
    }

    if (item.ativo) {
      setError("Desative o dispositivo antes de tentar exclui-lo.");
      return;
    }

    if (item.ultimoAcesso.trim() !== "") {
      setError("Dispositivos com histórico de acesso não podem ser excluidos.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o dispositivo "${item.deviceId || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("portal", "dispositivos", item.id);
      await loadDevices();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyDevice });
      }

      setSuccess("Dispositivo excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possivel excluir o dispositivo."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "devices-page devices-page--embedded" : "devices-page"
      }
    >
      {!embedded ? (
        <header className="devices-page__header">
          <div>
            <span className="devices-page__eyebrow">PORTAL</span>
            <h2 className="devices-page__title">Dispositivos</h2>
            <p className="devices-page__subtitle">
              Gerencie device ID, plataforma, push token, ultimo acesso e estado
              do dispositivo.
            </p>
          </div>

          <div className="devices-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por device ID, modelo, plataforma ou push token"
              className="devices-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="devices-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo dispositivo
            </button>
          </div>
        </header>
      ) : (
        <div className="devices-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por device ID, modelo, plataforma ou push token"
            className="devices-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="devices-page__toolbar-actions">
            <button
              type="button"
              className="devices-page__ghost"
              onClick={() => void loadDevices()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="devices-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo dispositivo
            </button>
          </div>
        </div>
      )}

      {error ? <div className="devices-page__alert">{error}</div> : null}
      {success ? (
        <div className="devices-page__alert devices-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="devices-page__alert devices-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="devices-page__layout">
        <DevicesTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          canDeleteItem={(item) =>
            !item.ativo && item.ultimoAcesso.trim() === ""
          }
          userOptions={userOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <DeviceForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          userOptions={userOptions}
          userAccess={userAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
