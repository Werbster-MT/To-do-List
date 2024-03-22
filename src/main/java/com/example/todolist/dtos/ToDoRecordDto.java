package com.example.todolist.dtos;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a _ToDo_ record.
 */
public record ToDoRecordDto(@NotBlank String title, @NotBlank String description, @NotNull @FutureOrPresent LocalDate dueDate) {

}