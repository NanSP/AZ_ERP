import type { AuthSession } from "./authTypes";

// Nota: após migração para cookie HttpOnly, não persistiremos tokens em
// armazenamento do navegador acessível por scripts. Mantemos uma API mínima
// em memória para compatibilidade interna durante a transição.
// interna durante a transição.

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
