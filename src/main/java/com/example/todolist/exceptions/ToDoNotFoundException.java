package com.example.todolist.exceptions;

import java.util.UUID;

/**
 * Exception thrown when a _ToDo_ entity is not found.
 */
public class ToDoNotFoundException extends RuntimeException {

    /**
     * Constructs a ToDoNotFoundException with the specified id.
     * @param id The unique identifier of the _ToDo_ entity that was not found.
     */
    public ToDoNotFoundException(UUID id) {
        super("ToDo not found with id: " + id);
    }
}