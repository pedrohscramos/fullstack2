package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;

import java.util.UUID;

public class CreateTaskListUseCase {

    private final TaskListGateway taskListGateway;

    public CreateTaskListUseCase(TaskListGateway taskListGateway) {
        this.taskListGateway = taskListGateway;
    }

    public TaskList execute(UUID userId, String name) {
        String normalized = name.trim();
        if (taskListGateway.existsByUserAndName(userId, normalized)) {
            throw new ConflictException("Task list name already exists");
        }
        return taskListGateway.save(userId, normalized);
    }
}
