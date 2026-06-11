import type { AuthSession } from "./authTypes";

let inMemorySession: AuthSession | null = null;

export function loadSession(): AuthSession | null {
  return inMemorySession;
}

export function saveSession(session: AuthSession): void {
  inMemorySession = session;
}

export function clearSession(): void {
  inMemorySession = null;
}
