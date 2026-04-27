package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskGateway {

    Task save(UUID userId, UUID listId, String title, String description, boolean completed);

    List<Task> findAllByUserId(UUID userId);

    List<Task> findAllByUserIdAndListId(UUID userId, UUID listId);

    Optional<Task> findByIdAndUserId(UUID id, UUID userId);

    Task update(UUID id, UUID userId, String title, String description, boolean completed);

    void delete(UUID id, UUID userId);

    boolean existsByUserAndListAndTitle(UUID userId, UUID listId, String title);

    boolean existsByUserAndListAndTitleAndIdNot(UUID userId, UUID listId, String title, UUID id);

    boolean existsByListId(UUID listId, UUID userId);
}
