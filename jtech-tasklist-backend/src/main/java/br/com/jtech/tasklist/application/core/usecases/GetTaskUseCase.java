package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.output.TaskGateway;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class GetTaskUseCase {

    private final TaskGateway taskGateway;

    public GetTaskUseCase(TaskGateway taskGateway) {
        this.taskGateway = taskGateway;
    }

    public Task execute(UUID userId, UUID taskId) {
        return taskGateway.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new NotFoundException("Task not found"));
    }
}
