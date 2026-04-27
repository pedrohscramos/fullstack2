package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.output.TaskGateway;

import java.util.List;
import java.util.UUID;

public class ListTasksUseCase {

    private final TaskGateway taskGateway;

    public ListTasksUseCase(TaskGateway taskGateway) {
        this.taskGateway = taskGateway;
    }

    public List<Task> execute(UUID userId, UUID listId) {
        if (listId == null) {
            return taskGateway.findAllByUserId(userId);
        }
        return taskGateway.findAllByUserIdAndListId(userId, listId);
    }
}
