# Spec de Produto - Autenticacao

## Objetivo do dominio
Permitir onboarding e acesso seguro ao sistema TODO multiusuario, com persistencia de sessao no frontend e controle de acesso por JWT no backend.

## Escopo funcional
- Frontend: login com validacao de campos obrigatorios e redirecionamento para a area autenticada.
- Backend: registro de usuarios, login JWT e refresh token.
- Seguranca: senha com hash BCrypt, email unico e protecao de rotas privadas.

## User stories

### US-AUTH-001 - Login no frontend
Como usuario, quero informar credenciais na tela de login para acessar a aplicacao.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Login com campos preenchidos
  Given que estou na tela de login
  When preencho usuario e senha com valores nao vazios
  And submeto o formulario
  Then devo ser redirecionado para a aplicacao principal

Scenario: Login com campos obrigatorios vazios
  Given que estou na tela de login
  When submeto o formulario com algum campo vazio
  Then devo visualizar mensagem de validacao
  And nao devo ser autenticado
```

### US-AUTH-002 - Registro de usuario
Como visitante, quero criar uma conta para acessar meus dados privados.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Registro com dados validos
  Given que nao possuo conta
  When envio nome, email valido e senha valida para POST /auth/register
  Then o sistema deve criar o usuario
  And retornar status 201

Scenario: Registro com email duplicado
  Given que ja existe um usuario com o email informado
  When tento registrar novamente o mesmo email
  Then o sistema deve rejeitar com erro de conflito
```

### US-AUTH-003 - Login JWT no backend
Como usuario registrado, quero autenticar com email e senha para receber token e usar rotas privadas.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Login com credenciais validas
  Given que existe usuario com email e senha validos
  When envio POST /auth/login com credenciais corretas
  Then devo receber access token JWT
  And status 200

Scenario: Login com credenciais invalidas
  Given que informo credenciais incorretas
  When envio POST /auth/login
  Then devo receber erro de autenticacao
  And nenhum token deve ser emitido
```

### US-AUTH-004 - Renovacao de sessao
Como usuario autenticado, quero renovar token expirado sem novo login completo.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Refresh token valido
  Given que possuo refresh token valido
  When solicito renovacao de token
  Then devo receber novo access token

Scenario: Refresh token invalido
  Given que possuo refresh token invalido ou expirado
  When solicito renovacao de token
  Then devo receber erro de autenticacao
```

## Regras de negocio
- Email deve ser unico por usuario.
- Senha deve ser armazenada apenas com hash BCrypt.
- Access token JWT deve proteger endpoints privados.
- Dados sensiveis (senha/hash) nunca devem ser expostos em payload de resposta.
- Sessao deve ser persistida no estado global do frontend.

## Contratos de API

### POST /auth/register
**Request**
```json
{
  "name": "string",
  "email": "user@example.com",
  "password": "string"
}
```

**Response 201**
```json
{
  "id": "uuid",
  "name": "string",
  "email": "user@example.com"
}
```

### POST /auth/login
**Request**
```json
{
  "email": "user@example.com",
  "password": "string"
}
```

**Response 200**
```json
{
  "accessToken": "jwt",
  "refreshToken": "string",
  "tokenType": "Bearer"
}
```

### POST /auth/refresh
**Request**
```json
{
  "refreshToken": "string"
}
```

**Response 200**
```json
{
  "accessToken": "jwt",
  "tokenType": "Bearer"
}
```

## Erros esperados
- `400` validacao de entrada.
- `401` credenciais/token invalidos.
- `409` email duplicado.
