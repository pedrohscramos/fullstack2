package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListGateway;

import java.util.List;
import java.util.UUID;

public class ListTaskListsUseCase {

    private final TaskListGateway taskListGateway;

    public ListTaskListsUseCase(TaskListGateway taskListGateway) {
        this.taskListGateway = taskListGateway;
    }

    public List<TaskList> execute(UUID userId) {
        return taskListGateway.findAllByUserId(userId);
    }
}
