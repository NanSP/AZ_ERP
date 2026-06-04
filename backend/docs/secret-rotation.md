# Secret Rotation

## Objetivo

Padronizar a rotacao de segredos do backend sem perda de controle operacional.

## Segredos Abrangidos

- `JWT_SECRET`
- credenciais do banco master
- credenciais do banco template
- credenciais temporarias de bootstrap

## Quando Rotacionar

- troca de ambiente ou responsavel
- suspeita de vazamento
- incidente de seguranca
- janela periodica definida pela operacao

## Processo

1. Registrar a janela de manutencao.
2. Preparar o novo segredo fora do repositorio.
3. Atualizar o segredo no ambiente de execucao.
4. Atualizar o segredo na CI, se aplicavel.
5. Reiniciar os componentes que dependem do segredo.
6. Validar `health`, `readiness` e autenticacao.
7. Revogar o segredo antigo.

## Pos-rotacao

- validar login master
- validar login tenant
- validar provisionamento
- validar workflow do GitHub Actions

## Cuidados

- nunca registrar o valor do segredo em logs
- nunca commitar segredos em `.env`
- evitar rotacao simultanea de multiplos segredos criticos sem plano de rollback
