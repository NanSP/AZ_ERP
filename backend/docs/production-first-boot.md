# Production First Boot

## Objetivo

Descrever a sequencia segura do primeiro boot do backend em um ambiente novo.

## Pre-requisitos

- Docker e Docker Compose instalados
- Postgres acessivel
- secrets obrigatorios definidos
- perfil de execucao escolhido

## Secrets Minimos

- `JWT_SECRET`
- `MASTER_DB_HOST`
- `MASTER_DB_PORT`
- `MASTER_DB_NAME`
- `MASTER_DB_USERNAME`
- `MASTER_DB_PASSWORD`
- `TEMPLATE_DB_HOST`
- `TEMPLATE_DB_PORT`
- `TEMPLATE_DB_NAME`
- `TEMPLATE_DB_USERNAME`
- `TEMPLATE_DB_PASSWORD`

## Sequencia Recomendada

1. Subir o Postgres.
2. Confirmar conectividade com o banco master.
3. Subir o backend com o profile correto.
4. Aguardar `GET /api/actuator/health/readiness` responder `UP`.
5. Confirmar no `GET /api/actuator/info` que o bloco `template` esta em `READY`.
6. Validar criacao do primeiro admin master.
7. Realizar o primeiro login e a troca obrigatoria de senha.
8. Registrar o bootstrap como concluido.

## Validacoes Operacionais

- `GET /api/actuator/health`
- `GET /api/actuator/health/readiness`
- `GET /api/actuator/info`
- consulta em `platform.template_registry`

## Observacoes

- o bootstrap do primeiro admin deve ser usado apenas quando necessario
- depois da criacao do admin inicial, o ideal e desabilitar o bootstrap
- nao reutilizar senhas temporarias entre ambientes
