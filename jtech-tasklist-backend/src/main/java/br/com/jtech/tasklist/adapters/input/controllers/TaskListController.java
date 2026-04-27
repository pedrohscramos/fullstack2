package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.lists.CreateTasklistRequest;
import br.com.jtech.tasklist.adapters.input.protocols.lists.TasklistResponse;
import br.com.jtech.tasklist.adapters.input.protocols.lists.UpdateTasklistRequest;
import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.core.usecases.CreateTaskListUseCase;
import br.com.jtech.tasklist.application.core.usecases.DeleteTaskListUseCase;
import br.com.jtech.tasklist.application.core.usecases.GetTaskListUseCase;
import br.com.jtech.tasklist.application.core.usecases.ListTaskListsUseCase;
import br.com.jtech.tasklist.application.core.usecases.UpdateTaskListUseCase;
import br.com.jtech.tasklist.config.security.CurrentUserProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lists")
public class TaskListController {

    private final CurrentUserProvider currentUserProvider;
    private final CreateTaskListUseCase createTaskListUseCase;
    private final ListTaskListsUseCase listTaskListsUseCase;
    private final GetTaskListUseCase getTaskListUseCase;
    private final UpdateTaskListUseCase updateTaskListUseCase;
    private final DeleteTaskListUseCase deleteTaskListUseCase;

    public TaskListController(CurrentUserProvider currentUserProvider,
                              CreateTaskListUseCase createTaskListUseCase,
                              ListTaskListsUseCase listTaskListsUseCase,
                              GetTaskListUseCase getTaskListUseCase,
                              UpdateTaskListUseCase updateTaskListUseCase,
                              DeleteTaskListUseCase deleteTaskListUseCase) {
        this.currentUserProvider = currentUserProvider;
        this.createTaskListUseCase = createTaskListUseCase;
        this.listTaskListsUseCase = listTaskListsUseCase;
        this.getTaskListUseCase = getTaskListUseCase;
        this.updateTaskListUseCase = updateTaskListUseCase;
        this.deleteTaskListUseCase = deleteTaskListUseCase;
    }

    @PostMapping
    public ResponseEntity<TasklistResponse> create(@Valid @RequestBody CreateTasklistRequest request) {
        UUID userId = currentUserProvider.currentUserId();
        TaskList created = createTaskListUseCase.execute(userId, request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<TasklistResponse>> list() {
        UUID userId = currentUserProvider.currentUserId();
        List<TasklistResponse> response = listTaskListsUseCase.execute(userId).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TasklistResponse> getById(@PathVariable UUID id) {
        UUID userId = currentUserProvider.currentUserId();
        TaskList taskList = getTaskListUseCase.execute(userId, id);
        return ResponseEntity.ok(toResponse(taskList));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TasklistResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateTasklistRequest request) {
        UUID userId = currentUserProvider.currentUserId();
        TaskList updated = updateTaskListUseCase.execute(userId, id, request.name());
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID userId = currentUserProvider.currentUserId();
        deleteTaskListUseCase.execute(userId, id);
        return ResponseEntity.noContent().build();
    }

    private TasklistResponse toResponse(TaskList taskList) {
        return new TasklistResponse(
                taskList.id(),
                taskList.userId(),
                taskList.name(),
                taskList.createdAt(),
                taskList.updatedAt()
        );
    }
}
