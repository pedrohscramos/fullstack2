package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.adapters.output.repositories.TaskRepository;
import br.com.jtech.tasklist.adapters.output.repositories.TasklistRepository;
import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TasklistEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.output.TaskGateway;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TaskJpaGateway implements TaskGateway {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TasklistRepository tasklistRepository;

    public TaskJpaGateway(TaskRepository taskRepository,
                          UserRepository userRepository,
                          TasklistRepository tasklistRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.tasklistRepository = tasklistRepository;
    }

    @Override
    public Task save(UUID userId, UUID listId, String title, String description, boolean completed) {
        UserEntity user = loadUser(userId);
        TasklistEntity list = loadList(listId, userId);
        TaskEntity entity = new TaskEntity();
        entity.setUser(user);
        entity.setTasklist(list);
        entity.setTitle(title);
        entity.setDescription(description);
        entity.setCompleted(completed);
        if (completed) {
            entity.setCompletedAt(Instant.now());
        }
        return toDomain(taskRepository.save(entity));
    }

    @Override
    public List<Task> findAllByUserId(UUID userId) {
        return taskRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Task> findAllByUserIdAndListId(UUID userId, UUID listId) {
        return taskRepository.findAllByUserIdAndTasklistIdOrderByCreatedAtDesc(userId, listId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Task> findByIdAndUserId(UUID id, UUID userId) {
        return taskRepository.findByIdAndUserId(id, userId).map(this::toDomain);
    }

    @Override
    public Task update(UUID id, UUID userId, String title, String description, boolean completed) {
        TaskEntity entity = taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        entity.setTitle(title);
        entity.setDescription(description);
        entity.setCompleted(completed);
        entity.setCompletedAt(completed ? Instant.now() : null);

        return toDomain(taskRepository.save(entity));
    }

    @Override
    public void delete(UUID id, UUID userId) {
        TaskEntity entity = taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Task not found"));
        taskRepository.delete(entity);
    }

    @Override
    public boolean existsByUserAndListAndTitle(UUID userId, UUID listId, String title) {
        return taskRepository.existsByUserAndTasklistAndTitleIgnoreCase(loadUser(userId), loadList(listId, userId), title);
    }

    @Override
    public boolean existsByUserAndListAndTitleAndIdNot(UUID userId, UUID listId, String title, UUID id) {
        return taskRepository.existsByUserAndTasklistAndTitleIgnoreCaseAndIdNot(
                loadUser(userId),
                loadList(listId, userId),
                title,
                id
        );
    }

    @Override
    public boolean existsByListId(UUID listId, UUID userId) {
        return taskRepository.existsByTasklist(loadList(listId, userId));
    }

    private UserEntity loadUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private TasklistEntity loadList(UUID listId, UUID userId) {
        return tasklistRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new NotFoundException("Task list not found"));
    }

    private Task toDomain(TaskEntity entity) {
        return new Task(
                entity.getId(),
                entity.getUser().getId(),
                entity.getTasklist().getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.isCompleted(),
                entity.getCompletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
