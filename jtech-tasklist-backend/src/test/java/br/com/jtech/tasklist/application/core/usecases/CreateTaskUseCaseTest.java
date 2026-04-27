package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaskUseCaseTest {

    @Mock
    private TaskGateway taskGateway;

    @Mock
    private TaskListGateway taskListGateway;

    @InjectMocks
    private CreateTaskUseCase createTaskUseCase;

    private UUID userId;
    private UUID listId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        listId = UUID.randomUUID();
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        when(taskListGateway.findByIdAndUserId(listId, userId)).thenReturn(Optional.of(
                new TaskList(listId, userId, "Work", Instant.now(), Instant.now())
        ));
        when(taskGateway.existsByUserAndListAndTitle(userId, listId, "Task A")).thenReturn(false);

        Task created = new Task(UUID.randomUUID(), userId, listId, "Task A", "desc", false, null, Instant.now(), Instant.now());
        when(taskGateway.save(userId, listId, "Task A", "desc", false)).thenReturn(created);

        Task result = createTaskUseCase.execute(userId, listId, "Task A", "desc", false);

        assertEquals("Task A", result.title());
    }

    @Test
    void shouldThrowNotFoundWhenListDoesNotBelongToUser() {
        when(taskListGateway.findByIdAndUserId(listId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> createTaskUseCase.execute(userId, listId, "Task A", "desc", false));
    }

    @Test
    void shouldThrowConflictWhenTitleIsDuplicatedInList() {
        when(taskListGateway.findByIdAndUserId(listId, userId)).thenReturn(Optional.of(
                new TaskList(listId, userId, "Work", Instant.now(), Instant.now())
        ));
        when(taskGateway.existsByUserAndListAndTitle(userId, listId, "Task A")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> createTaskUseCase.execute(userId, listId, "Task A", "desc", false));
    }
}
