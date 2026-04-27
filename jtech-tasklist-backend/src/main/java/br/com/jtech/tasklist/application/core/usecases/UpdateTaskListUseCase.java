package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class UpdateTaskListUseCase {

    private final TaskListGateway taskListGateway;

    public UpdateTaskListUseCase(TaskListGateway taskListGateway) {
        this.taskListGateway = taskListGateway;
    }

    public TaskList execute(UUID userId, UUID listId, String name) {
        if (taskListGateway.findByIdAndUserId(listId, userId).isEmpty()) {
            throw new NotFoundException("Task list not found");
        }

        String normalized = name.trim();
        if (taskListGateway.existsByUserAndNameAndIdNot(userId, normalized, listId)) {
            throw new ConflictException("Task list name already exists");
        }

        return taskListGateway.updateName(listId, userId, normalized);
    }
}
