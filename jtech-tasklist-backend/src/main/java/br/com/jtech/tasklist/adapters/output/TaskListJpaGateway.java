package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.adapters.output.repositories.TasklistRepository;
import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TasklistEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListGateway;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TaskListJpaGateway implements TaskListGateway {

    private final TasklistRepository tasklistRepository;
    private final UserRepository userRepository;

    public TaskListJpaGateway(TasklistRepository tasklistRepository, UserRepository userRepository) {
        this.tasklistRepository = tasklistRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TaskList save(UUID userId, String name) {
        UserEntity user = loadUser(userId);
        TasklistEntity entity = new TasklistEntity();
        entity.setUser(user);
        entity.setName(name);
        return toDomain(tasklistRepository.save(entity));
    }

    @Override
    public List<TaskList> findAllByUserId(UUID userId) {
        return tasklistRepository.findAllByUserIdOrderByCreatedAtAsc(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<TaskList> findByIdAndUserId(UUID id, UUID userId) {
        return tasklistRepository.findByIdAndUserId(id, userId).map(this::toDomain);
    }

    @Override
    public TaskList updateName(UUID id, UUID userId, String name) {
        TasklistEntity entity = tasklistRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Task list not found"));
        entity.setName(name);
        return toDomain(tasklistRepository.save(entity));
    }

    @Override
    public void delete(UUID id, UUID userId) {
        TasklistEntity entity = tasklistRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Task list not found"));
        tasklistRepository.delete(entity);
    }

    @Override
    public boolean existsByUserAndName(UUID userId, String name) {
        return tasklistRepository.existsByUserAndNameIgnoreCase(loadUser(userId), name);
    }

    @Override
    public boolean existsByUserAndNameAndIdNot(UUID userId, String name, UUID id) {
        return tasklistRepository.existsByUserAndNameIgnoreCaseAndIdNot(loadUser(userId), name, id);
    }

    private UserEntity loadUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private TaskList toDomain(TasklistEntity entity) {
        return new TaskList(
                entity.getId(),
                entity.getUser().getId(),
                entity.getName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
