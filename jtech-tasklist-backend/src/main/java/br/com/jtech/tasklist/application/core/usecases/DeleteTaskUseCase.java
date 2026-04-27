package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.ports.output.TaskGateway;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class DeleteTaskUseCase {

    private final TaskGateway taskGateway;

    public DeleteTaskUseCase(TaskGateway taskGateway) {
        this.taskGateway = taskGateway;
    }

    public void execute(UUID userId, UUID taskId) {
        if (taskGateway.findByIdAndUserId(taskId, userId).isEmpty()) {
            throw new NotFoundException("Task not found");
        }
        taskGateway.delete(taskId, userId);
    }
}
