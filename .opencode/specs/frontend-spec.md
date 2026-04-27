# Spec de Produto - Frontend

## Objetivo do dominio
Implementar SPA em Vue 3 com navegacao protegida, estado global persistente e experiencia de uso focada em produtividade para listas e tarefas.

## Escopo funcional
- Fluxo de autenticacao (login e sessao persistida).
- Estrutura principal com navegacao entre listas.
- CRUD de tarefas por lista.
- Guards de rota para proteger areas autenticadas.

## User stories

### US-FE-001 - Acessar login
Como visitante, quero ver tela de login para entrar no sistema.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Exibicao da tela de login
  Given que nao estou autenticado
  When acesso a aplicacao
  Then devo visualizar tela de login
```

### US-FE-002 - Persistir sessao
Como usuario autenticado, quero manter sessao ativa ao recarregar pagina.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Rehidratacao da sessao
  Given que realizei login com sucesso
  When recarrego a pagina
  Then devo permanecer autenticado
  And meus dados de estado devem ser restaurados
```

### US-FE-003 - Navegacao protegida
Como usuario nao autenticado, nao devo acessar rotas privadas.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Bloqueio de rota privada
  Given que nao estou autenticado
  When tento acessar rota da aplicacao principal
  Then devo ser redirecionado para login
```

### US-FE-004 - Gerenciar listas no UI
Como usuario autenticado, quero criar e alternar listas para organizar visualmente minhas tarefas.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: Criar e selecionar lista
  Given que estou na area autenticada
  When crio uma nova lista valida
  And seleciono essa lista
  Then devo visualizar contexto da lista selecionada
```

### US-FE-005 - Gerenciar tarefas no UI
Como usuario autenticado, quero adicionar, editar, concluir e remover tarefas dentro da lista ativa.

**Criterios de aceitacao (Gherkin):**
```gherkin
Scenario: CRUD completo de tarefa na lista ativa
  Given que estou em uma lista ativa
  When adiciono, edito, concluo e removo uma tarefa
  Then cada operacao deve refletir imediatamente na interface
```

## Requisitos de arquitetura frontend
- Framework: Vue 3 com Composition API.
- Estado global: Pinia para usuario, listas e tarefas.
- Roteamento: Vue Router com separacao entre area publica e privada.
- Persistencia: estado relevante (sessao e dados) persistido localmente.

## Estrutura de stores sugerida
- `authStore`: usuario autenticado, token, status de sessao, logout.
- `listsStore`: colecao de listas, lista ativa, operacoes CRUD.
- `tasksStore`: tarefas por lista, filtros, operacoes CRUD e toggle de conclusao.

## Regras de UX e validacao
- Campos obrigatorios devem ser validados no cliente antes da chamada de API.
- Exibicao de erros de negocio retornados pelo backend de forma amigavel.
- Acoes destrutivas (exclusoes) exigem confirmacao explicita.
- Navegacao entre listas deve atualizar imediatamente o contexto de tarefas.

## Testabilidade
- Testes unitarios com Vitest para componentes criticos e stores.
- Cobrir cenarios de guard de rota, autenticacao e sincronizacao de estado.
- Mock de API para validar fluxos de sucesso e erro.
