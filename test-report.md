# Test Report

## O que foi testado

### Backend - Unitarios (JUnit 5 + Mockito)
- `RegisterUserUseCaseTest`
  - sucesso no registro
  - erro de email duplicado (`ConflictException`)
- `LoginUseCaseTest`
  - login valido com emissao de tokens
  - login invalido (`UnauthorizedException`)
- `CreateTaskUseCaseTest`
  - sucesso na criacao de tarefa
  - erro de ownership/lista inexistente (`NotFoundException`)
  - erro de duplicidade de titulo (`ConflictException`)

### Backend - Integracao (SpringBootTest + MockMvc)
- `AuthControllerIntegrationTest`
  - `POST /auth/register` sucesso
  - `POST /auth/login` sucesso
  - `POST /auth/login` invalido (401)
- `TaskControllerSecurityIntegrationTest`
  - rota protegida sem token (401)
  - token invalido (401)
  - criacao/listagem de tarefa autenticada
  - bloqueio de criacao em lista de outro usuario
  - isolamento de dados por usuario na listagem

### Frontend - Unitarios (Vitest)
- `auth.spec.ts`
  - login com persistencia de sessao
  - erro de login
  - bootstrap com sessao persistida
- `lists.spec.ts`
  - carregamento de listas
  - criacao de lista
  - tratamento de erro no carregamento
- `tasks.spec.ts`
  - carregamento de tarefas
  - criacao de tarefa
  - filtro de concluidas

## Cobertura alcancada
- Frontend: todos os testes criados executaram com sucesso (`10/10`).
- Backend: testes criados, mas execucao local bloqueada por incompatibilidade de runtime Java/Gradle no ambiente.

## Riscos nao cobertos
- Fluxo de refresh token com expiracao/rotacao em teste de integracao.
- Testes de validacao DTO detalhados (mensagens por campo).
- Testes de GlobalExceptionHandler para mapeamento completo de erros.
- Cobertura de concorrencia (ex.: criacao simultanea de tarefas/listas com mesmo nome).

## Sugestoes de melhoria
- Adicionar relatorio de cobertura (`jacocoTestReport`) e meta minima por modulo.
- Incluir testes de integracao para `/lists` (CRUD completo + conflito por dependencia).
- Incluir testes de seguranca para tentativa de update/delete cross-user em `/tasks/{id}`.
- Adicionar testes de componentes frontend para `LoginView` e `AppView` (render + interacoes de UI).
