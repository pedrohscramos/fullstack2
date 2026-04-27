package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.TaskList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskListGateway {

    TaskList save(UUID userId, String name);

    List<TaskList> findAllByUserId(UUID userId);

    Optional<TaskList> findByIdAndUserId(UUID id, UUID userId);

    TaskList updateName(UUID id, UUID userId, String name);

    void delete(UUID id, UUID userId);

    boolean existsByUserAndName(UUID userId, String name);

    boolean existsByUserAndNameAndIdNot(UUID userId, String name, UUID id);
}
