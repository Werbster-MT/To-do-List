package com.example.todolist;

import com.example.todolist.controller.ToDoController;
import com.example.todolist.entity.ToDo;
import com.example.todolist.exceptions.ToDoNotFoundException;
import com.example.todolist.service.ToDoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class contains unit tests for the ToDoController class. It utilizes Spring Boot's @WebMvcTest annotation
 * to focus only on the web layer, mocking the service layer dependencies.
 */
@WebMvcTest(ToDoController.class)
public class ToDoControllerTests {
    // Constants
    private static final String END_POINT_PATH = "/todos";

    // Dependencies
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ToDoService service;

    // Test data
    private String title;
    private String description;
    private LocalDate dueDate;
    private UUID toDoId;
    private ToDo mockToDo;

    /**
     * Sets up test data before each test case.
     */
    @BeforeEach
    public void setUp() {
        title = "Task 1.";
        description = "Testing Spring Boot restful API.";
        dueDate = LocalDate.now();
        toDoId = UUID.randomUUID();

        mockToDo = new ToDo();
        mockToDo.setTitle(title);
        mockToDo.setDescription(description);
        mockToDo.setDueDate(dueDate);
    }

    /**
     * Test case to verify the creation of a _ToDo_.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testCreateToDoShouldReturn201Created() throws Exception {
        ToDo newToDo = mockToDo;
        Mockito.when(service.save(newToDo)).thenReturn(newToDo);
        String requestBody = objectMapper.writeValueAsString(newToDo);

        mockMvc.perform(post(END_POINT_PATH).contentType("application/json")
            .content(requestBody))
            .andExpect(content().contentType("application/json"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title", is(title)))
            .andExpect(jsonPath("$.description", is(description)))
            .andExpect(jsonPath("$.dueDate", is(dueDate.toString())))
            .andDo(print());
    }

    /**
     * Test case to verify handling of missing parameters during _ToDo_ creation.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testMissingParametersCreateToDoShouldReturn400BadRequest() throws Exception {
        ToDo newToDo = mockToDo;
        newToDo.setTitle("");
        newToDo.setDescription("");
        newToDo.setDueDate(null);

        String requestBody = objectMapper.writeValueAsString(newToDo);
        mockMvc.perform(post(END_POINT_PATH).contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    /**
     * Test case to verify the successful retrieval of a list of ToDos.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testGetToDoListShouldReturn200OK() throws Exception {
        ToDo savedToDo = mockToDo;
        savedToDo.setToDoId(toDoId);
        List<ToDo> toDoList = List.of(mockToDo);
        Mockito.when(service.getList()).thenReturn(toDoList);
        String requestBody = objectMapper.writeValueAsString(savedToDo);

        mockMvc.perform(get(END_POINT_PATH).contentType("application/json")
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title", is(title)))
            .andExpect(jsonPath("$[0].description", is(description)))
            .andExpect(jsonPath("$[0].dueDate", is(dueDate.toString())))
            .andDo(print());

        Mockito.verify(service, Mockito.times(1)).getList();
    }

    /**
     * Test case to verify the handling of an empty list of ToDos.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testGetToDoListShouldReturn204NoContent() throws Exception {
        Mockito.when(service.getList()).thenReturn(new ArrayList<>());

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    /**
     * Test case to verify the successful retrieval of a specific _ToDo_ by its ID.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testGetToDoShouldReturn200OK() throws Exception {
        Optional<ToDo> savedToDo = Optional.ofNullable(mockToDo);
        Mockito.when(service.getById(toDoId)).thenReturn(savedToDo);
        String requestURI = END_POINT_PATH + "/" + toDoId;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.description", is(description)))
                .andExpect(jsonPath("$.dueDate", is(dueDate.toString())))
                .andDo(print());
    }

    /**
     * Test case to verify the handling of a GET request for a non-existent _ToDo_ ID.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testWrongIdGetToShouldReturn404NotFound() throws Exception {
        String requestURI = END_POINT_PATH + "/" + toDoId;
        Mockito.doThrow(ToDoNotFoundException.class).when(service).getById(toDoId);

        mockMvc.perform(get(requestURI).contentType("application/json"))
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    /**
     * Test case to verify the successful update of a _ToDo_.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testUpdateToDoShouldReturn200OK() throws Exception {
        ToDo newToDo = mockToDo;
        String newTitle = "Task 2";
        newToDo.setTitle(newTitle);
        String requestURI = END_POINT_PATH + "/" + toDoId;
        String requestBody = objectMapper.writeValueAsString(newToDo);
        Optional<ToDo> savedToDo = Optional.ofNullable(mockToDo);
        Mockito.when(service.getById(toDoId)).thenReturn(savedToDo);
        Mockito.when(service.update(newToDo)).thenReturn(newToDo);

        mockMvc.perform(put(requestURI, toDoId).contentType("application/json")
                .content(requestBody))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(newTitle)))
                .andDo(print());
    }

    /**
     * Test case to verify the handling of an update request for a non-existent _ToDo_ ID.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testWrongIdUpdateToDoShouldReturn404NotFound() throws Exception {
        ToDo newToDo = mockToDo;
        newToDo.setToDoId(toDoId);
        String requestURI = END_POINT_PATH + "/" + toDoId;
        String requestBody = objectMapper.writeValueAsString(newToDo);
        Mockito.doThrow(ToDoNotFoundException.class).when(service).getById(toDoId);

        mockMvc.perform(put(requestURI).contentType("application/json")
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    /**
     * Test case to verify the handling of an update request with wrong parameters.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testWrongParametersUpdateToDoShouldReturn400BadRequest() throws Exception {
        ToDo newToDo = mockToDo;
        newToDo.setTitle("");
        String requestURI = END_POINT_PATH + "/" + toDoId;
        String requestBody = objectMapper.writeValueAsString(newToDo);
        Mockito.when(service.save(newToDo)).thenReturn(newToDo);

        mockMvc.perform(put(requestURI).contentType("application/json")
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    /**
     * Test case to verify the successful deletion of a _ToDo_.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testDeleteToDoShouldReturn200OK() throws Exception {
        Optional<ToDo> savedToDo = Optional.ofNullable(mockToDo);
        String requestURI = END_POINT_PATH + "/" + toDoId;
        Mockito.when(service.getById(toDoId)).thenReturn(savedToDo);
        Mockito.when(service.delete(toDoId)).thenReturn(savedToDo);

        mockMvc.perform(delete(requestURI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.description", is(description)))
                .andExpect(jsonPath("$.dueDate", is(dueDate.toString())))
                .andDo(print());
    }
    /**
     * Test case to verify the handling of a delete request for a non-existent _ToDo_ ID.
     *
     * @throws Exception if any error occurs during the test execution
     */
    @Test
    public void testWrongIdDeleteToDoShouldReturn404NotFound() throws Exception {
        String requestURI = END_POINT_PATH + "/" + toDoId;
        Mockito.doThrow(ToDoNotFoundException.class).when(service).delete(toDoId);

        mockMvc.perform(delete(requestURI))
            .andExpect(status().isNotFound())
            .andDo(print());
    }
}