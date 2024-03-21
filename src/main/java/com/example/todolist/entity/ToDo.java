package com.example.todolist.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity class representing a _ToDo_ item.
 */
@Entity
@Table(name = "TB_TODOS")
public class ToDo extends RepresentationModel<ToDo> implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO) // Automatic id generation
    private UUID toDoId;

    private String title;
    private String description;
    private LocalDate dueDate;

    /**
     * Get the unique identifier of the _ToDo_ item.
     * @return The _ToDo_ item's unique identifier.
     */
    public UUID getToDoId() {
        return toDoId;
    }

    /**
     * Set the unique identifier of the _ToDo_ item.
     * @param toDoId The unique identifier to set.
     */
    public void setToDoId(UUID toDoId) {
        this.toDoId = toDoId;
    }

    /**
     * Get the title of the _ToDo_ item.
     * @return The title of the _ToDo_ item.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the _ToDo_ item.
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the description of the _ToDo_ item.
     * @return The description of the _ToDo_ item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the _ToDo_ item.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the due date of the _ToDo_ item.
     * @return The due date of the _ToDo_ item.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Set the due date of the _ToDo_ item.
     * @param dueDate The due date to set.
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
