# Tasklist Fullstack - Vue 3 + Spring Boot

Sistema TODO multiusuario com autenticacao JWT, listas de tarefas por usuario e UI SPA moderna.

## 1) Visao geral da arquitetura

### Contexto
- O frontend (`jtech-tasklist-frontend`) e responsavel por UX, navegacao, estado e integracao HTTP.
- O backend (`jtech-tasklist-backend`) e responsavel por regras de negocio, seguranca e persistencia.
- O PostgreSQL e o banco principal para dados de usuarios, listas, tarefas e refresh tokens.

### Fluxo geral
1. Usuario registra/loga no frontend.
2. Backend retorna `accessToken` + `refreshToken`.
3. Frontend persiste sessao e envia Bearer token nas rotas privadas.
4. Backend valida JWT via filtro de seguranca e aplica ownership no acesso aos dados.
5. Operacoes de listas/tarefas retornam apenas recursos do usuario autenticado.

### Arquitetura escolhida
- **Monolito modular** (backend unico + frontend unico): menor complexidade operacional, melhor custo de manutencao para o escopo atual.
- **Hexagonal no backend**: separacao explicita entre dominio/aplicacao e adaptadores de infraestrutura.
- **SPA por features no frontend**: separacao por dominios (`auth`, `lists`, `tasks`) com stores Pinia.

## 2) Decisoes tecnicas

### Por que Spring Boot
- Produtividade alta para API REST com ecossistema maduro.
- Integracao nativa com Spring Security, JPA e validacao (`@Valid`).
- Facil observabilidade com Actuator e suporte robusto a testes de integracao.

### Por que Vue 3 + Pinia
- Composition API oferece melhor organizacao para features e reuso de logica.
- Pinia simplifica estado global e testabilidade das regras de UI.
- Vue Router com guards resolve bem separacao publico/privado.

### Por que JWT + refresh token
- API stateless com escalabilidade horizontal simplificada.
- Controle de expiracao curto no access token e renovacao segura via refresh token.
- Reduz acoplamento a sessoes server-side.

## 3) Arquitetura backend

### Camadas (hexagonal)
- `adapters/input`: controllers e DTOs HTTP.
- `application/core/usecases`: casos de uso (orquestracao de regras).
- `application/core/domains`: modelos de dominio.
- `application/ports/output`: contratos para persistencia/seguranca.
- `adapters/output`: implementacoes JPA, JWT, BCrypt.
- `config`: seguranca, beans e configuracoes.

### Padroes e principios aplicados
- **SOLID** nos use cases e gateways (dependencia por interface).
- **DTOs** para nao expor entidades JPA na API.
- **Ownership enforcement**: `userId` vem do token, nao do payload do cliente.
- **Exception handling centralizado** com `GlobalExceptionHandler`.

### Endpoints principais
- Auth: `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`
- Lists: `POST /lists`, `GET /lists`, `GET /lists/{id}`, `PUT /lists/{id}`, `DELETE /lists/{id}`
- Tasks: `POST /tasks`, `GET /tasks`, `GET /tasks/{id}`, `PUT /tasks/{id}`, `DELETE /tasks/{id}`

## 4) Arquitetura frontend

### Organizacao
- `src/views`: telas principais (`LoginView`, `AppView`).
- `src/stores`: estado por dominio (`auth`, `lists`, `tasks`).
- `src/services`: cliente HTTP e chamadas de API.
- `src/types`: contratos TypeScript de payloads/respostas.
- `src/plugins`: inicializacao de Vuetify e Pinia.

### Estado e navegacao
- `authStore` persiste sessao em `localStorage` e realiza bootstrap com refresh.
- Guards no router impedem acesso a rotas privadas sem autenticacao.
- Store de listas controla lista ativa; store de tarefas sincroniza o conteudo da lista ativa.

## 5) Seguranca

- Senhas com BCrypt no backend.
- Rotas privadas protegidas por JWT (`SecurityFilterChain` + `JwtAuthenticationFilter`).
- `401` para token ausente/invalido.
- Acesso cross-user bloqueado por validacao de ownership no backend.
- CORS habilitado para `http://localhost:5173`.

## 6) Como rodar o projeto

### Opcao recomendada: Docker Compose

Pre-requisitos:
- Docker + Docker Compose

Passos:
1. Copie `.env.example` para `.env` e ajuste valores se necessario.
2. Suba os servicos:

```bash
docker compose up --build
```

Servicos:
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/doc/tasklist/v1/api.html`
- Postgres: `localhost:5432`

### Opcao local (sem Docker)

Pre-requisitos:
- Node.js `^20.19.0 || >=22.12.0`
- Java 21
- PostgreSQL 16+

Backend:

```bash
cd jtech-tasklist-backend
./gradlew bootRun
```

Frontend:

```bash
cd jtech-tasklist-frontend
npm install
npm run dev
```

Configure variaveis de ambiente do backend conforme `application.yml` (`DS_URL`, `DS_PORT`, `DS_DATABASE`, `DS_USER`, `DS_PASS`, `JWT_SECRET`).

## 7) Testes

### Backend

```bash
cd jtech-tasklist-backend
./gradlew test
```

Cobertura atual inclui:
- Unitarios de use cases (sucesso, erro, regras de negocio).
- Integracao com MockMvc para auth e seguranca de tasks.

### Frontend

```bash
cd jtech-tasklist-frontend
npm run test:unit -- --run
```

Cobertura atual inclui:
- Stores (`auth`, `lists`, `tasks`) com cenarios de sucesso e erro.
- Persistencia de sessao e filtros de tarefas.

Relatorio de testes disponivel em `test-report.md`.

## 8) Estrutura de pastas

```text
.
├── docker-compose.yml
├── .github/workflows/ci.yml
├── jtech-tasklist-backend/
│   ├── Dockerfile
│   ├── build.gradle
│   └── src/
│       ├── main/java/br/com/jtech/tasklist/
│       │   ├── adapters/
│       │   ├── application/
│       │   └── config/
│       └── test/java/br/com/jtech/tasklist/
└── jtech-tasklist-frontend/
    ├── Dockerfile
    ├── package.json
    └── src/
        ├── views/
        ├── stores/
        ├── services/
        ├── types/
        └── router/
```

## CI/CD

Pipeline em `.github/workflows/ci.yml`:
- Job backend: test + build com Java 21.
- Job frontend: lint + type-check + tests + build com Node 22.
- Falha rapida em qualquer etapa critica.

---
