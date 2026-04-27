package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.application.core.usecases.CreateTaskUseCase;
import br.com.jtech.tasklist.application.core.usecases.DeleteTaskUseCase;
import br.com.jtech.tasklist.application.core.usecases.GetTaskUseCase;
import br.com.jtech.tasklist.application.core.usecases.ListTasksUseCase;
import br.com.jtech.tasklist.application.core.usecases.UpdateTaskUseCase;
import br.com.jtech.tasklist.application.ports.output.TaskGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskUseCaseConfig {

    @Bean
    public CreateTaskUseCase createTaskUseCase(TaskGateway taskGateway, TaskListGateway taskListGateway) {
        return new CreateTaskUseCase(taskGateway, taskListGateway);
    }

    @Bean
    public ListTasksUseCase listTasksUseCase(TaskGateway taskGateway) {
        return new ListTasksUseCase(taskGateway);
    }

    @Bean
    public GetTaskUseCase getTaskUseCase(TaskGateway taskGateway) {
        return new GetTaskUseCase(taskGateway);
    }

    @Bean
    public UpdateTaskUseCase updateTaskUseCase(TaskGateway taskGateway) {
        return new UpdateTaskUseCase(taskGateway);
    }

    @Bean
    public DeleteTaskUseCase deleteTaskUseCase(TaskGateway taskGateway) {
        return new DeleteTaskUseCase(taskGateway);
    }
}
