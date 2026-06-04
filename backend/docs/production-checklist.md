# Production Checklist

## Objetivo

Checklist operacional para validar o backend antes de considerar um deploy apto para producao.

## Configuracao e Secrets

- [ ] `JWT_SECRET` definido fora do repositorio
- [ ] credenciais `MASTER_DB_*` definidas por ambiente
- [ ] credenciais `TEMPLATE_DB_*` definidas por ambiente
- [ ] `.env` local fora de versionamento
- [ ] `SPRING_PROFILES_ACTIVE` coerente com o ambiente

## Bootstrap e Acesso Inicial

- [ ] bootstrap do primeiro admin master habilitado apenas quando necessario
- [ ] primeiro login do admin master exige troca de senha
- [ ] procedimento de entrega da senha inicial documentado
- [ ] fluxo de troca de senha testado

## Banco e Migrations

- [ ] banco `az_erp` acessivel
- [ ] banco `az_erp_template` criado e registrado
- [ ] `platform.template_registry` em estado `READY`
- [ ] migrations do master aplicadas
- [ ] migrations do template aplicadas
- [ ] upgrade incremental de tenant validado

## Observabilidade

- [ ] `GET /api/actuator/health` responde `UP`
- [ ] `GET /api/actuator/health/liveness` responde `UP`
- [ ] `GET /api/actuator/health/readiness` responde `UP`
- [ ] `GET /api/actuator/info` expoe `app`, `build`, `template` e `environment`
- [ ] logs nao exibem segredos, tokens ou senhas

## Seguranca

- [ ] sem usuarios default no caminho de producao
- [ ] sem senhas conhecidas em migrations de producao
- [ ] rotas publicas restritas ao necessario
- [ ] `spring.jpa.open-in-view=false`
- [ ] warning de `generated security password` ausente

## Pipeline

- [ ] `mvn test` verde
- [ ] workflow do GitHub Actions verde
- [ ] smoke Docker verde
- [ ] smoke autenticado de provisionamento verde
- [ ] primeiro login do tenant com troca obrigatoria validado

## Fechamento

- [ ] runbook de primeiro boot revisado
- [ ] runbook de rotacao de segredos revisado
- [ ] runbook de recuperacao administrativa revisado
- [ ] aprovacao final de deploy registrada
