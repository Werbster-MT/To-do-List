package com.example.todolist.controller;

import com.example.todolist.dtos.ToDoRecordDto;
import com.example.todolist.entity.ToDo;
import com.example.todolist.exceptions.ToDoNotFoundException;
import com.example.todolist.service.ToDoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Controller class for handling _ToDo_ related endpoints.
 */
@Tag(name = "ToDo", description = "the ToDo Restful Api")
@RestController
public class ToDoController {
    private final ToDoService toDoService;

    /**
     * Constructs a new ToDoController with the specified ToDoService.
     * @param toDoService The ToDoService to be used by the controller.
     */
    public ToDoController(ToDoService toDoService){
        this.toDoService = toDoService;
    }

    /**
     * Handles the creation of a new _ToDo_ record.
     * @param toDoRecord The ToDoRecordDto containing data for the new _ToDo_ record.
     * @return ResponseEntity containing the created _ToDo_ record.
     */
    @Operation(
            summary = "Create a new ToDo",
            description = "saves a new ToDo´s entity on data source",
            method = "POST"
    )

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ToDoRecordDto.class))
            )
    })
    @PostMapping(value="/todos", produces = "application/json")
    public ResponseEntity<ToDo> createToDo(@RequestBody @Valid ToDoRecordDto toDoRecord) {
        var toDo = new ToDo();
        BeanUtils.copyProperties(toDoRecord, toDo);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDoService.save(toDo));
    }

    /**
     * Handles the retrieval of all _ToDo_ records.
     * @return ResponseEntity containing a list of all _ToDo_ records.
     */
    @Operation(
            summary = "Fetch all ToDo´s",
            description = "fetches all ToDo´s entities and their data from data source",
            method = "GET"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ToDoRecordDto.class))
            )
    })
    @GetMapping(value = "/todos", produces = "application/json")
    public ResponseEntity<List<ToDo>> getToDoList() {
        List <ToDo> toDoList = toDoService.getList();
        if(!toDoList.isEmpty()){
            for(ToDo toDo: toDoList){
                UUID id = toDo.getToDoId();
                toDo.add(linkTo(methodOn(ToDoController.class).getToDo(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(toDoList);
    }

    /**
     * Handles the retrieval of a single _ToDo_ record by its id.
     * @param id The id of the _ToDo_ record to retrieve.
     * @return ResponseEntity containing the requested _ToDo_ record.
     * @throws ToDoNotFoundException if the requested _ToDo_ record is not found.
     */
    @Operation(
            summary = "Fetch a specific ToDo",
            description = "fetches a ToDo entity by id from data source",
            method = "GET"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ToDoRecordDto.class))
            )
    })
    @GetMapping(value = "/todos/{id}", produces = "application/json")
    public ResponseEntity<Object> getToDo(@PathVariable("id") UUID id) {
        Optional<ToDo> foundToDo = toDoService.getById(id);
        if(foundToDo.isEmpty()){
            throw new ToDoNotFoundException(id);
        }

        foundToDo.get().add(linkTo(methodOn(ToDoController.class).getToDoList()).withRel("ToDo List"));
        return ResponseEntity.status(HttpStatus.OK).body(toDoService.getById(id));
    }

    /**
     * Handles the update of an existing _ToDo_ record.
     * @param id The id of the _ToDo_ record to update.
     * @param toDoRecord The ToDoRecordDto containing updated data for the _ToDo_ record.
     * @return ResponseEntity containing the updated _ToDo_ record.
     * @throws ToDoNotFoundException if the requested _ToDo_ record is not found.
     */
    @Operation(
            summary = "Updates a ToDo",
            description = "updates a ToDo entity by id from data source",
            method = "PUT"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ToDoRecordDto.class))
            )
    })
    @PutMapping(value="/todos/{id}", produces = "application/json")
    public ResponseEntity<Object> updateToDo(@PathVariable(value="id") UUID id, @RequestBody @Valid ToDoRecordDto toDoRecord) {
        Optional<ToDo> foundToDoOptional = toDoService.getById(id);

        if (foundToDoOptional.isEmpty()) {
            throw new ToDoNotFoundException(id);
        }

        else {
            ToDo foundToDo = foundToDoOptional.get();
            BeanUtils.copyProperties(toDoRecord, foundToDo);
            return ResponseEntity.status(HttpStatus.OK).body(toDoService.save(foundToDo));
        }
    }

    /**
     * Handles the deletion of an existing _ToDo_ record.
     * @param id The id of the _ToDo_ record to delete.
     * @return ResponseEntity indicating success of the deletion operation.
     * @throws ToDoNotFoundException if the requested _ToDo_ record is not found.
     */
    @Operation(
            summary = "Deletes a ToDo",
            description = "deletes a ToDo entity by id from data source",
            method = "DELETE"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "successful operation",
                    content = @Content(schema = @Schema(implementation = ToDoRecordDto.class))
            )
    })
    @DeleteMapping(value="/todos/{id}", produces = "application/json")
    public ResponseEntity<Object> deleteToDo(@PathVariable("id") UUID id) {
        Optional<ToDo> foundTodo = toDoService.getById(id);
        if(foundTodo.isEmpty()){
            throw new ToDoNotFoundException(id);
        }

        return ResponseEntity.status(HttpStatus.OK).body(toDoService.delete(id));
    }
}
