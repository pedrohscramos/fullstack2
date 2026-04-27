package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.application.core.usecases.CreateTaskListUseCase;
import br.com.jtech.tasklist.application.core.usecases.DeleteTaskListUseCase;
import br.com.jtech.tasklist.application.core.usecases.GetTaskListUseCase;
import br.com.jtech.tasklist.application.core.usecases.ListTaskListsUseCase;
import br.com.jtech.tasklist.application.core.usecases.UpdateTaskListUseCase;
import br.com.jtech.tasklist.application.ports.output.TaskGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskListUseCaseConfig {

    @Bean
    public CreateTaskListUseCase createTaskListUseCase(TaskListGateway taskListGateway) {
        return new CreateTaskListUseCase(taskListGateway);
    }

    @Bean
    public ListTaskListsUseCase listTaskListsUseCase(TaskListGateway taskListGateway) {
        return new ListTaskListsUseCase(taskListGateway);
    }

    @Bean
    public GetTaskListUseCase getTaskListUseCase(TaskListGateway taskListGateway) {
        return new GetTaskListUseCase(taskListGateway);
    }

    @Bean
    public UpdateTaskListUseCase updateTaskListUseCase(TaskListGateway taskListGateway) {
        return new UpdateTaskListUseCase(taskListGateway);
    }

    @Bean
    public DeleteTaskListUseCase deleteTaskListUseCase(TaskListGateway taskListGateway, TaskGateway taskGateway) {
        return new DeleteTaskListUseCase(taskListGateway, taskGateway);
    }
}
