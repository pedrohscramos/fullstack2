package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.output.TaskGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class UpdateTaskUseCase {

    private final TaskGateway taskGateway;

    public UpdateTaskUseCase(TaskGateway taskGateway) {
        this.taskGateway = taskGateway;
    }

    public Task execute(UUID userId, UUID taskId, String title, String description, boolean completed) {
        Task current = taskGateway.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        String normalized = title.trim();
        if (taskGateway.existsByUserAndListAndTitleAndIdNot(userId, current.listId(), normalized, taskId)) {
            throw new ConflictException("Task title already exists in list");
        }

        return taskGateway.update(taskId, userId, normalized, description, completed);
    }
}
