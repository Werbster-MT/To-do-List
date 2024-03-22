package com.example.todolist.service;

import com.example.todolist.entity.ToDo;
import com.example.todolist.repository.ToDoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing _ToDo_ entities.
 */
@Service
public class ToDoService {

    private final ToDoRepository toDoRepository;

    /**
     * Constructs a ToDoService with the specified ToDoRepository.
     * @param toDoRepository The ToDoRepository to be used by the service.
     */
    public ToDoService(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    /**
     * Saves a _ToDo_ entity.
     * @param toDo The _ToDo_ entity to save.
     * @return The saved _ToDo_ entity.
     */
    public ToDo save(ToDo toDo) {
        return toDoRepository.save(toDo);
    }

    /**
     * Retrieves a list of all _ToDo_ entities, sorted by due date in ascending order.
     * @return A list of _ToDo_ entities.
     */
    public List<ToDo> getList() {
        Sort sort = Sort.by("dueDate").ascending();
        return toDoRepository.findAll(sort);
    }

    /**
     * Retrieves a _ToDo_ entity by its unique identifier.
     * @param id The unique identifier of the _ToDo_ entity to retrieve.
     * @return An Optional containing the retrieved _ToDo_ entity, or an empty Optional if not found.
     */
    public Optional<ToDo> getById(UUID id) {
        return toDoRepository.findById(id);
    }

    /**
     * Deletes a _ToDo_ entity by its unique identifier.
     * @param id The unique identifier of the _ToDo_ entity to delete.
     * @return An Optional containing the deleted _ToDo_ entity, or an empty Optional if not found.
     */
    public Optional<ToDo> delete(UUID id) {
        Optional<ToDo> foundToDo = getById(id);
        toDoRepository.deleteById(id);
        return foundToDo;
    }

    /**
     * Saves the provided _ToDo_ entity by updating it in the database.
     *
     * @param toDo The _ToDo_ entity to be updated.
     * @return The updated _ToDo_ entity after it's saved in the database.
     */
    public ToDo update(ToDo toDo) {
        return toDoRepository.save(toDo);
    }
}