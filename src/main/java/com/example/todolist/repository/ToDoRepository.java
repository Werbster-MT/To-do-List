package com.example.todolist.repository;

import com.example.todolist.entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for managing _ToDo_ entities.
 * Extends JpaRepository providing CRUD operations for _ToDo_ entities with UUID as the entity identifier.
 */
public interface ToDoRepository extends JpaRepository<ToDo, UUID> {

}
