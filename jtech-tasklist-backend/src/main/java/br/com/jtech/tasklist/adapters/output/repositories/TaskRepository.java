package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TasklistEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    List<TaskEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    List<TaskEntity> findAllByUserIdAndTasklistIdOrderByCreatedAtDesc(UUID userId, UUID tasklistId);

    Optional<TaskEntity> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByUserAndTasklistAndTitleIgnoreCase(UserEntity user, TasklistEntity tasklist, String title);

    boolean existsByUserAndTasklistAndTitleIgnoreCaseAndIdNot(UserEntity user, TasklistEntity tasklist, String title, UUID id);

    boolean existsByTasklist(TasklistEntity tasklist);
}
