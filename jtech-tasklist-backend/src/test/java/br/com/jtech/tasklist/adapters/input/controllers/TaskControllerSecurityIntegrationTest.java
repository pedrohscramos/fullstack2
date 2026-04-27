package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.output.repositories.TaskRepository;
import br.com.jtech.tasklist.adapters.output.repositories.TasklistRepository;
import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TasklistEntity;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.application.ports.output.TokenGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenGateway tokenGateway;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TasklistRepository tasklistRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserEntity userA;
    private UserEntity userB;
    private TasklistEntity listA;
    private String tokenA;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        tasklistRepository.deleteAll();
        userRepository.deleteAll();

        userA = new UserEntity();
        userA.setName("User A");
        userA.setEmail("a@example.com");
        userA.setPasswordHash(passwordEncoder.encode("secret123"));
        userA = userRepository.save(userA);

        userB = new UserEntity();
        userB.setName("User B");
        userB.setEmail("b@example.com");
        userB.setPasswordHash(passwordEncoder.encode("secret123"));
        userB = userRepository.save(userB);

        listA = new TasklistEntity();
        listA.setUser(userA);
        listA.setName("A List");
        listA = tasklistRepository.save(listA);

        tokenA = tokenGateway.generateAccessToken(userA.getId(), userA.getEmail());
    }

    @Test
    void shouldBlockProtectedRouteWithoutToken() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWithInvalidToken() throws Exception {
        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateAndListTasksForAuthenticatedUser() throws Exception {
        var createPayload = objectMapper.writeValueAsString(new CreateTaskBody(listA.getId().toString(), "Task A", "desc", false));

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Task A"));

        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task A"));
    }

    @Test
    void shouldNotAllowUserToCreateTaskInOtherUsersList() throws Exception {
        String tokenB = tokenGateway.generateAccessToken(userB.getId(), userB.getEmail());
        var createPayload = objectMapper.writeValueAsString(new CreateTaskBody(listA.getId().toString(), "Task X", "desc", false));

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListOnlyOwnersTasks() throws Exception {
        TaskEntity taskA = new TaskEntity();
        taskA.setUser(userA);
        taskA.setTasklist(listA);
        taskA.setTitle("Only A");
        taskA.setDescription("desc");
        taskA.setCompleted(false);
        taskRepository.save(taskA);

        String tokenB = tokenGateway.generateAccessToken(userB.getId(), userB.getEmail());
        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private record CreateTaskBody(String listId, String title, String description, boolean completed) {}
}
