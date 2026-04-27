package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListGateway;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class GetTaskListUseCase {

    private final TaskListGateway taskListGateway;

    public GetTaskListUseCase(TaskListGateway taskListGateway) {
        this.taskListGateway = taskListGateway;
    }

    public TaskList execute(UUID userId, UUID listId) {
        return taskListGateway.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new NotFoundException("Task list not found"));
    }
}
