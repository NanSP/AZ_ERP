# Logging Safety

## Objetivo

Garantir que logs de aplicacao e de operacao continuem uteis sem expor dados sensiveis.

## Nunca Registrar

- senhas
- tokens JWT
- hashes de senha completos
- payload bruto de login
- credenciais de banco
- segredos carregados por ambiente

## Pode Registrar

- status de operacao
- nome da etapa
- identificadores tecnicos nao sensiveis
- versao do template
- status de health e readiness

## Politica Atual Recomendada

- `root=INFO`
- `com.example.backend=INFO`
- `org.springframework.security=WARN`
- `org.hibernate.SQL=WARN`
- `org.hibernate.orm.jdbc.bind=WARN`
- `org.flywaydb=INFO`
- `com.zaxxer.hikari=INFO`

## Regras Operacionais

- usar `DEBUG` apenas temporariamente
- reduzir `DEBUG` apos troubleshooting
- revisar artefatos de CI antes de publicar
- nao subir respostas com token para artefatos
