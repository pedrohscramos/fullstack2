# Spec de Produto - Listas

## Objetivo do dominio
Permitir que cada usuario organize tarefas em multiplas listas categorizadas com CRUD completo e navegacao entre contextos.

## Escopo funcional
- Criar, renomear, listar, detalhar e excluir listas.
- Cada lista pertence exclusivamente a um usuario autenticado.
- Excluir lista exige confirmacao e verificacao de dependencias (tarefas vinculadas).

## User stories

### US-LIST-001 - Criar lista
Como usuario autenticado, quero criar listas para organizar minhas tarefas por contexto.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Criacao de lista valida
  Given que estou autenticado
  When envio uma nova lista com nome valido
  Then a lista deve ser criada para meu usuario
  And devo receber status de sucesso

Scenario: Criacao com nome vazio
  Given que estou autenticado
  When tento criar lista com nome vazio
  Then devo receber erro de validacao
```

### US-LIST-002 - Renomear lista
Como usuario autenticado, quero renomear uma lista existente para refletir melhor seu conteudo.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Renomeacao valida
  Given que possuo uma lista existente
  When envio novo nome valido para a lista
  Then o nome da lista deve ser atualizado

Scenario: Renomeacao de lista nao pertencente
  Given que existe lista de outro usuario
  When tento renomear essa lista
  Then devo receber erro de autorizacao/acesso
```

### US-LIST-003 - Excluir lista com dependencia
Como usuario autenticado, quero excluir listas com seguranca para evitar perda acidental de tarefas.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Exclusao com confirmacao
  Given que possuo uma lista
  And confirmo a exclusao no frontend
  When solicito exclusao da lista
  Then a lista deve ser removida

Scenario: Exclusao com tarefas dependentes sem politica de cascata
  Given que a lista possui tarefas vinculadas
  When tento excluir a lista sem tratar dependencias
  Then devo receber erro de conflito
  And devo ser orientado a resolver dependencias
```

### US-LIST-004 - Navegar entre listas
Como usuario autenticado, quero alternar entre listas para visualizar tarefas de cada contexto.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Troca de lista ativa
  Given que possuo multiplas listas
  When seleciono uma lista especifica na navegacao
  Then devo visualizar apenas tarefas da lista selecionada
```

## Regras de negocio
- Nome da lista obrigatorio e nao pode ser apenas espacos.
- Recomenda-se unicidade de nome por usuario para evitar ambiguidades.
- Todas as operacoes de lista exigem autenticacao.
- Usuario nao pode ler/alterar/excluir listas de terceiros.

## Contratos de API

### POST /lists
**Request**
```json
{
  "name": "Trabalho"
}
```

**Response 201**
```json
{
  "id": "uuid",
  "name": "Trabalho",
  "userId": "uuid"
}
```

### GET /lists
**Response 200**
```json
[
  {
    "id": "uuid",
    "name": "Trabalho"
  }
]
```

### PUT /lists/{id}
**Request**
```json
{
  "name": "Estudos"
}
```

### DELETE /lists/{id}
- Remove lista do usuario autenticado respeitando regra de dependencia.

## Erros esperados
- `400` payload invalido.
- `401` nao autenticado.
- `403/404` lista sem permissao ou inexistente.
- `409` conflito de dependencia ou nome duplicado.
