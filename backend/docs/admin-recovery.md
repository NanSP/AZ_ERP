# Admin Recovery

## Objetivo

Definir o fluxo de recuperacao do acesso administrativo do master de forma controlada.

## Casos Cobertos

- senha esquecida
- bloqueio operacional
- perda do usuario administrador principal

## Abordagem Recomendada

1. Confirmar que a recuperacao foi aprovada.
2. Identificar o usuario administrativo alvo.
3. Aplicar reset controlado de senha.
4. Forcar troca de senha no proximo login.
5. Registrar a acao para auditoria.

## Validacoes

- usuario permanece `ATIVO`
- senha anterior deixa de funcionar
- novo login responde com `passwordChangeRequired=true`
- troca obrigatoria e concluida pelo responsavel

## Cuidados

- nao criar usuarios paralelos sem necessidade
- evitar alterar diretamente dados sensiveis sem registro
- manter a recuperacao restrita a operadores autorizados
