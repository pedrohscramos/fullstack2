package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.output.TaskGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class CreateTaskUseCase {

    private final TaskGateway taskGateway;
    private final TaskListGateway taskListGateway;

    public CreateTaskUseCase(TaskGateway taskGateway, TaskListGateway taskListGateway) {
        this.taskGateway = taskGateway;
        this.taskListGateway = taskListGateway;
    }

    public Task execute(UUID userId, UUID listId, String title, String description, boolean completed) {
        if (taskListGateway.findByIdAndUserId(listId, userId).isEmpty()) {
            throw new NotFoundException("Task list not found");
        }

        String normalized = title.trim();
        if (taskGateway.existsByUserAndListAndTitle(userId, listId, normalized)) {
            throw new ConflictException("Task title already exists in list");
        }

        return taskGateway.save(userId, listId, normalized, description, completed);
    }
}
