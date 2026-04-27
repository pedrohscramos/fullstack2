package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.tasks.CreateTaskRequest;
import br.com.jtech.tasklist.adapters.input.protocols.tasks.TaskResponse;
import br.com.jtech.tasklist.adapters.input.protocols.tasks.UpdateTaskRequest;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.usecases.CreateTaskUseCase;
import br.com.jtech.tasklist.application.core.usecases.DeleteTaskUseCase;
import br.com.jtech.tasklist.application.core.usecases.GetTaskUseCase;
import br.com.jtech.tasklist.application.core.usecases.ListTasksUseCase;
import br.com.jtech.tasklist.application.core.usecases.UpdateTaskUseCase;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final CurrentUserProvider currentUserProvider;
    private final CreateTaskUseCase createTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final GetTaskUseCase getTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;

    public TaskController(CurrentUserProvider currentUserProvider,
                          CreateTaskUseCase createTaskUseCase,
                          ListTasksUseCase listTasksUseCase,
                          GetTaskUseCase getTaskUseCase,
                          UpdateTaskUseCase updateTaskUseCase,
                          DeleteTaskUseCase deleteTaskUseCase) {
        this.currentUserProvider = currentUserProvider;
        this.createTaskUseCase = createTaskUseCase;
        this.listTasksUseCase = listTasksUseCase;
        this.getTaskUseCase = getTaskUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
        UUID userId = currentUserProvider.currentUserId();
        Task created = createTaskUseCase.execute(userId, request.listId(), request.title(), request.description(), request.completed());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list(@RequestParam(required = false) UUID listId) {
        UUID userId = currentUserProvider.currentUserId();
        List<TaskResponse> response = listTasksUseCase.execute(userId, listId).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable UUID id) {
        UUID userId = currentUserProvider.currentUserId();
        Task task = getTaskUseCase.execute(userId, id);
        return ResponseEntity.ok(toResponse(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateTaskRequest request) {
        UUID userId = currentUserProvider.currentUserId();
        Task task = updateTaskUseCase.execute(userId, id, request.title(), request.description(), request.completed());
        return ResponseEntity.ok(toResponse(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID userId = currentUserProvider.currentUserId();
        deleteTaskUseCase.execute(userId, id);
        return ResponseEntity.noContent().build();
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.id(),
                task.userId(),
                task.listId(),
                task.title(),
                task.description(),
                task.completed(),
                task.completedAt(),
                task.createdAt(),
                task.updatedAt()
        );
    }
}
