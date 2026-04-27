# Arquitetura Completa - Sistema TODO Multiusuario

## 1) Visao arquitetural
- Estilo geral: frontend SPA + backend API REST stateless + banco relacional.
- Frontend: Vue 3 (Composition API), Pinia, Vue Router.
- Backend: Spring Boot 3 com arquitetura hexagonal (Ports and Adapters).
- Seguranca: JWT para acesso a recursos protegidos; BCrypt para armazenamento de senha.
- Persistencia: PostgreSQL em producao; H2 para testes de backend.

## 2) Contexto do sistema (alto nivel)

```text
[Browser SPA Vue]
    |
    | HTTPS (JSON + Bearer JWT)
    v
[Spring Boot API]
    |
    | JPA/Hibernate
    v
[PostgreSQL]
```

- A SPA concentra UX, estado global e navegacao protegida.
- A API concentra regras de negocio, autorizacao e isolamento multiusuario.

## 3) Backend - arquitetura hexagonal

### 3.1 Camadas e responsabilidades
- **Adapters In (HTTP):** controllers, DTOs de request/response, mapeamento de erro HTTP.
- **Application (Use Cases):** orquestracao das regras de negocio por caso de uso.
- **Domain:** entidades, invariantes, validacoes de negocio puras.
- **Ports Out:** contratos para persistencia, token, hash e relogio/ids.
- **Adapters Out:** implementacoes Spring Data JPA, JWT provider, BCrypt encoder.

### 3.2 Estrutura proposta de pacotes

```text
backend/src/main/java/.../tasklist
  adapters/
    input/
      controllers/
      dtos/
    output/
      repositories/
      security/
  application/
    usecases/
      auth/
      lists/
      tasks/
    ports/
      input/
      output/
  domain/
    model/
    exceptions/
    services/
  config/
    security/
    web/
    beans/
```

### 3.3 Bounded contexts
- **Auth:** registro, login, refresh, validacao de credenciais.
- **Lists:** CRUD de listas com ownership por usuario.
- **Tasks:** CRUD de tarefas por lista com ownership e validacoes.

## 4) Modelo de dominio e dados

### 4.1 Entidades de dominio
- `User`: `id`, `name`, `email`, `passwordHash`, `createdAt`, `updatedAt`.
- `TaskList`: `id`, `userId`, `name`, `createdAt`, `updatedAt`.
- `Task`: `id`, `userId`, `listId`, `title`, `description`, `completed`, `createdAt`, `updatedAt`, `completedAt`.

### 4.2 Relacionamentos
- `User 1:N TaskList`.
- `TaskList 1:N Task`.
- `User 1:N Task` (ownership explicito para reforcar isolamento).

### 4.3 Regras de negocio obrigatorias
- Email unico por usuario.
- Senha sempre armazenada como hash BCrypt.
- Lista e tarefa sempre validadas contra o usuario autenticado.
- Nome de lista obrigatorio (nao vazio).
- Titulo de tarefa obrigatorio (nao vazio).
- Exclusao de lista deve validar dependencias de tarefas (conflito ou politica definida).

## 5) Contratos de API (macro)
- `POST /auth/register` cria usuario.
- `POST /auth/login` autentica e emite tokens.
- `POST /auth/refresh` renova access token.
- `POST /lists`, `GET /lists`, `PUT /lists/{id}`, `DELETE /lists/{id}`.
- `POST /tasks`, `GET /tasks`, `GET /tasks/{id}`, `PUT /tasks/{id}`, `DELETE /tasks/{id}`.

### Padrao de erro
- Adotar envelope padrao: `timestamp`, `status`, `code`, `message`, `path`, `details[]`.
- Mapeamento: `400` validacao, `401` autenticacao, `403/404` acesso/recurso, `409` conflito.

## 6) Seguranca e autorizacao
- `Spring Security Filter Chain` com validacao JWT em rotas protegidas.
- Subject do token identifica `userId` e/ou `email`.
- Controllers nunca aceitam `userId` do cliente para ownership; ownership vem do token.
- Refresh token com expiracao maior e rotacao recomendada.
- Senhas/segredos nunca retornam em resposta e nunca vao para logs.

## 7) Frontend - arquitetura

### 7.1 Estrutura funcional
- **Camada de apresentacao:** views e componentes por feature.
- **Camada de estado:** stores Pinia separadas por dominio (`auth`, `lists`, `tasks`).
- **Camada de dados:** cliente HTTP centralizado com interceptor de token.
- **Camada de roteamento:** Vue Router com guards publico/privado.

### 7.2 Estrutura sugerida

```text
frontend/src
  app/
    router/
    providers/
  shared/
    api/
    types/
    utils/
  features/
    auth/
      views/
      components/
      store/
      services/
    lists/
      components/
      store/
      services/
    tasks/
      components/
      store/
      services/
```

### 7.3 Fluxos principais
- **Login:** formulario valida campos -> chama `/auth/login` -> persiste sessao -> redireciona para area autenticada.
- **Bootstrap:** ao carregar app, reidrata sessao -> valida token/refresh -> monta stores de listas/tarefas.
- **Navegacao de listas:** seleciona lista ativa -> carrega tarefas da lista -> atualiza UI.
- **CRUD de tarefas:** atualizacao otimista opcional com rollback em caso de erro.

## 8) SOLID e desacoplamento aplicados
- **S:** um caso de uso por classe (ex.: `CreateTaskUseCase`).
- **O:** novas regras por composicao (policy/service) sem alterar fluxo central.
- **L:** contratos de porta com implementacoes intercambiaveis (JPA, mock, in-memory).
- **I:** portas pequenas por capacidade (ex.: `LoadUserByEmailPort`, `SaveTaskPort`).
- **D:** use cases dependem de interfaces (ports), nunca de frameworks.

## 9) Fluxos de sequencia (resumo)

### 9.1 Login
1. Frontend envia credenciais para `/auth/login`.
2. Controller delega para `AuthenticateUserUseCase`.
3. Use case valida senha com `PasswordHasherPort`.
4. Use case gera tokens com `TokenProviderPort`.
5. Frontend persiste sessao e libera rotas privadas.

### 9.2 Criacao de tarefa
1. Frontend envia `title` e `listId` com Bearer token.
2. Use case resolve usuario pelo token.
3. Use case valida ownership da lista.
4. Use case cria tarefa e persiste via `TaskRepositoryPort`.
5. Response retorna tarefa criada.

## 10) Qualidade e testes
- **Backend unitario:** use cases com mocks de ports (cenarios sucesso/falha).
- **Backend integracao:** controllers + security + banco de teste (H2).
- **Frontend unitario:** stores, guards e componentes criticos com Vitest.
- **Contrato API:** validar serializacao de erros e codigos HTTP por caso.

## 11) Decisoes arquiteturais obrigatorias
- Nao expor entidades JPA diretamente na API; usar DTOs.
- Nao acoplar controllers a repositorios; sempre via use case/port input.
- Nao confiar em `userId` no payload para controle de acesso.
- Nao permitir operacao de lista/tarefa fora do escopo do usuario autenticado.

## 12) Roadmap tecnico sugerido
- Paginacao e filtros avancados em `GET /tasks`.
- Auditoria (`createdBy`, `updatedBy`) e trilha de eventos.
- Observabilidade (metrics, tracing e logs estruturados).
- Feature flags para politicas de exclusao de lista (conflito vs cascata).
