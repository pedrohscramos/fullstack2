# Spec de Produto - Tarefas

## Objetivo do dominio
Permitir gerenciamento completo de tarefas por usuario e por lista, com validacoes de propriedade e consistencia.

## Escopo funcional
- CRUD completo de tarefas.
- Tarefas sempre associadas ao usuario autenticado.
- Consulta de tarefas restrita ao proprietario.
- Marcacao de concluida e edicao de conteudo.

## User stories

### US-TASK-001 - Criar tarefa
Como usuario autenticado, quero criar tarefa em uma lista para registrar algo a fazer.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Criacao de tarefa valida
  Given que estou autenticado
  And possuo uma lista valida
  When envio titulo valido para criar tarefa
  Then a tarefa deve ser criada vinculada ao meu usuario

Scenario: Criacao sem titulo
  Given que estou autenticado
  When envio tarefa sem titulo
  Then devo receber erro de validacao
```

### US-TASK-002 - Listar tarefas do usuario
Como usuario autenticado, quero listar minhas tarefas para acompanhar meu backlog.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Listagem isolada por usuario
  Given que existem tarefas de multiplos usuarios
  When solicito GET /tasks autenticado
  Then devo receber apenas tarefas do meu usuario
```

### US-TASK-003 - Atualizar tarefa
Como usuario autenticado, quero editar tarefa para manter informacoes atualizadas.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Atualizacao de tarefa propria
  Given que possuo uma tarefa
  When envio atualizacao valida para PUT /tasks/{id}
  Then a tarefa deve ser atualizada

Scenario: Atualizacao de tarefa de terceiro
  Given que existe tarefa de outro usuario
  When tento atualizar essa tarefa
  Then devo receber erro de acesso
```

### US-TASK-004 - Concluir tarefa
Como usuario autenticado, quero marcar tarefa como concluida para rastrear progresso.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Marcar tarefa como concluida
  Given que possuo tarefa pendente
  When altero status para concluida
  Then a tarefa deve ser retornada como concluida
```

### US-TASK-005 - Remover tarefa
Como usuario autenticado, quero excluir tarefa para manter lista limpa.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Exclusao de tarefa propria
  Given que possuo uma tarefa
  When envio DELETE /tasks/{id}
  Then a tarefa deve ser removida

Scenario: Exclusao de tarefa de terceiro
  Given que existe tarefa de outro usuario
  When tento excluir essa tarefa
  Then devo receber erro de acesso
```

## Regras de negocio
- Titulo da tarefa e obrigatorio.
- Recomenda-se evitar duplicatas de titulo na mesma lista.
- Toda tarefa deve estar vinculada a uma lista valida do mesmo usuario.
- Todas as operacoes exigem JWT valido.
- Consulta por ID sempre valida propriedade do recurso.

## Contratos de API

### POST /tasks
**Request**
```json
{
  "listId": "uuid",
  "title": "Pagar conta",
  "description": "Opcional",
  "completed": false
}
```

### GET /tasks
- Retorna tarefas apenas do usuario autenticado.

### GET /tasks/{id}
- Retorna tarefa se pertencer ao usuario autenticado.

### PUT /tasks/{id}
**Request**
```json
{
  "title": "Pagar conta de luz",
  "description": "Atualizada",
  "completed": true
}
```

### DELETE /tasks/{id}
- Remove tarefa se pertencer ao usuario autenticado.

## Erros esperados
- `400` dados invalidos.
- `401` token ausente/invalido.
- `403/404` acesso negado ou recurso inexistente.
- `409` conflito de regra (ex.: duplicata).
