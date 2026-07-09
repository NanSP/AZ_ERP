# AZ ERP

AZ ERP e uma plataforma ERP SaaS multi-tenant voltada para operacoes empresariais de medio e grande porte. O projeto combina uma camada **master** para administracao da plataforma com uma camada **tenant** para a operacao isolada de cada cliente, cobrindo processos de dados mestres, financeiro, RH, materiais, vendas, servicos, projetos, producao, qualidade, BI, fiscal, auditoria e governanca.

## Visao geral

- Arquitetura SaaS com isolamento por tenant em bancos dedicados
- Backend Java com Spring Boot e Flyway
- Frontend React + Vite com foco em experiencia enterprise
- Provisionamento automatizado de novos tenants
- Autenticacao separada para plataforma master e usuarios tenant
- Suporte a Docker, CI com GitHub Actions e testes E2E com Playwright

## Stack do projeto

### Linguagens

- Java 21
- TypeScript
- SQL
- CSS
- HTML
- YAML

### Backend

- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Flyway
- PostgreSQL
- JWT com `java-jwt`
- Spring Actuator

### Frontend

- React 19
- React Router DOM
- Vite
- Axios
- CSS modular por pagina/componente

### Qualidade e entrega

- JUnit / testes de integracao e servico no backend
- ESLint no frontend
- Playwright para testes E2E
- GitHub Actions para CI de frontend e backend
- Docker e Docker Compose para ambiente local e smoke tests

## Arquitetura do monorepo

```text
AZ_ERP/
|- backend/     API, seguranca, provisionamento e persistencia
|- frontend/    aplicacao React, landing page, login e workspaces
|- .github/     pipelines de CI
`- docker-compose.fullstack.yml
```

## Como o modelo multi-tenant funciona

O AZ ERP nao trabalha apenas com separacao logica por coluna ou schema. O projeto foi estruturado para operar com **banco dedicado por tenant**, o que aumenta o isolamento operacional e facilita governanca, backup, manutencao e evolucao controlada por cliente.

### Camadas principais

#### 1. Camada master

Responsavel pela administracao da plataforma:

- usuarios master
- cadastro de tenants
- bases dos tenants
- usuarios administradores dos tenants
- logs de provisionamento
- controle do template base

Essa camada usa o **master database**, onde ficam os metadados da plataforma e o cadastro central dos clientes.

#### 2. Template database

Existe um banco template usado como matriz estrutural do tenant. Ele recebe:

- migrations Flyway da camada tenant
- tabelas, schemas e permissoes padrao
- evolucoes de estrutura versionadas

Quando um novo tenant e provisionado, o backend cria um novo banco a partir desse template.

#### 3. Tenant databases

Cada tenant recebe sua propria base PostgreSQL. O backend resolve dinamicamente a conexao com base no `tenantCode` informado no login tenant, validando:

- existencia do tenant
- status do tenant
- status da base provisionada

Depois disso, a autenticacao e as operacoes passam a acontecer no banco dedicado daquele cliente.

### Fluxo resumido de provisionamento

1. Um usuario master autenticado inicia o onboarding.
2. O backend registra o tenant na camada master.
3. O backend cria a base fisica do tenant a partir do template.
4. O backend cria o usuario inicial da aplicacao dentro da base do tenant.
5. O backend registra o admin tenant na camada master.
6. O backend grava logs de provisionamento e atualiza os status para `ATIVO`.

## Modulos ERP presentes no projeto

- `SYS` - sistema, usuarios, perfis e permissoes
- `CORE` - parceiros, empresas, contatos, enderecos e produtos
- `FI` - contas a pagar, contas a receber, centros de custo, plano de contas, fluxo de caixa e movimentacoes bancarias
- `MM` - materiais, compras, estoque, inventarios e movimentacoes
- `RH` - colaboradores, dependentes, beneficios, ponto e folha
- `PS` - projetos, tarefas e recursos alocados
- `PP` - ordens de producao, apontamentos, BOM e MRP
- `QM` - inspecoes e nao conformidades
- `GRC` - riscos, controles, auditorias, consentimentos e blocos de privacidade/LGPD
- `PORTAL` - sessoes, dispositivos e notificacoes
- `AUDITORIA` - logs de acoes e logs de erros
- `FISCAL` - documentos, eSocial, EFD e ECD
- `SD` - clientes, oportunidades, contratos, pedidos, itens e faturas
- `SM` - atendimentos, ordens de servico e configuracoes de SLA
- `AM` - ativos e manutencoes
- `BI` - dashboards, metricas, historicos e relatorios

## Frontend

O frontend foi construido como uma SPA React com dois contextos principais de acesso:

- **master**: ve a area de plataforma e administra tenants
- **tenant**: ve o dashboard e os modulos operacionais do proprio cliente

Destaques do frontend:

- landing page institucional
- login master e login tenant separados
- troca obrigatoria de senha quando aplicavel
- shell autenticado com sidebar, topbar e dashboard
- workspaces dinamicos por modulo
- formularios CRUD para recursos tenant e recursos da plataforma
- suporte a proxy de `/api` em deploy via Vercel

## Backend

O backend concentra:

- autenticacao master e tenant
- emissao/validacao de sessao com cookie HttpOnly
- controle de acesso por escopo
- provisionamento de tenants
- resolucao dinamica de conexao por tenant
- migrations do banco master e do template tenant
- protecoes operacionais, validacoes e observabilidade

Tambem ha suporte a:

- `health`, `info`, `liveness` e `readiness` via Actuator
- bootstrap de usuario master inicial
- politicas de senha
- rotacao de segredos e procedimentos operacionais documentados em `backend/docs`

## Rodando localmente

### Opcao 1: stack completa com Docker

Na raiz do projeto:

```bash
docker compose -f docker-compose.fullstack.yml up --build
```

Servicos esperados:

- frontend: `http://localhost:4173`
- backend: `http://localhost:8080/api`
- postgres: `localhost:5432`

### Opcao 2: executar backend e frontend separados

#### Backend

```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Variaveis e configuracao

Os principais grupos de configuracao do backend sao:

- `MASTER_DB_*` para a base master
- `TEMPLATE_DB_*` para a base template
- `JWT_*` para autenticacao
- `BOOTSTRAP_MASTER_*` para criacao controlada do usuario master inicial
- `APP_SECURITY_ALLOWED_ORIGINS` para CORS

O frontend usa `VITE_API_BASE_URL` para definir a origem da API quando necessario.

## Testes

### Backend

```bash
cd backend
./mvnw test
```

### Frontend

```bash
cd frontend
npm install
npm run lint
npm run build
npm run test:e2e
```

## CI/CD

O repositorio possui workflows em `.github/workflows`:

- `backend-ci.yml`
  executa testes unitarios/integracao e smoke test com Docker, incluindo provisionamento real de tenant no ambiente de CI
- `frontend-ci.yml`
  executa lint, build e testes E2E com Playwright

## Deploy

O projeto esta preparado para um fluxo comum de deploy:

- **frontend** em Vercel, com `vercel.json` reescrevendo `/api` para o backend
- **backend** containerizado com `Dockerfile`, adequado para plataformas como Render
- **PostgreSQL** como base master, template e bancos de tenants

## Seguranca e governanca

Pontos importantes ja presentes na base:

- isolamento por banco por tenant
- autenticacao master e tenant separadas
- cookies HttpOnly para sessao
- migrations versionadas
- logs de provisionamento
- trilhas de auditoria
- base inicial para recursos de privacidade e LGPD em `GRC`

Documentacao operacional complementar:

- `backend/docs/production-first-boot.md`
- `backend/docs/production-checklist.md`
- `backend/docs/secret-rotation.md`
- `backend/docs/admin-recovery.md`
- `backend/docs/logging-safety.md`

## Objetivo do projeto

O AZ ERP foi desenhado para evoluir como uma plataforma ERP SaaS enterprise, com forte separacao entre governanca de plataforma e operacao do cliente final. O resultado e um repositorio que serve tanto como base de produto quanto como laboratorio de arquitetura multi-tenant com provisionamento automatizado, frontend modular e backend orientado a dominios.
