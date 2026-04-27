package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.ports.output.TaskGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class DeleteTaskListUseCase {

    private final TaskListGateway taskListGateway;
    private final TaskGateway taskGateway;

    public DeleteTaskListUseCase(TaskListGateway taskListGateway, TaskGateway taskGateway) {
        this.taskListGateway = taskListGateway;
        this.taskGateway = taskGateway;
    }

    public void execute(UUID userId, UUID listId) {
        if (taskListGateway.findByIdAndUserId(listId, userId).isEmpty()) {
            throw new NotFoundException("Task list not found");
        }

        if (taskGateway.existsByListId(listId, userId)) {
            throw new ConflictException("Task list has linked tasks");
        }

        taskListGateway.delete(listId, userId);
    }
}
