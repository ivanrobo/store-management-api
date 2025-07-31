package ro.robert.store.management.user.boundary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ro.robert.store.management.annotation.TrackExecutionTime;
import ro.robert.store.management.exception.entity.ServiceErrorResponse;
import ro.robert.store.management.user.control.UserService;
import ro.robert.store.management.user.entity.request.AssignRoleRequest;
import ro.robert.store.management.user.entity.request.UserCreateRequest;
import ro.robert.store.management.user.entity.response.UserResponse;

@Tag(name = "User Management", description = "API for managing users and their roles")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @Operation(summary = "Create a new user", description = "Creates a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceErrorResponse.class)))
    })
    @PostMapping
    @TrackExecutionTime("Create User")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        UserResponse response = userService.createUser(userCreateRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Assign role to user", description = "Assigns a role to an existing user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role assigned successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "404", description = "User or role not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceErrorResponse.class)))
    })
    @SecurityRequirement(name = "basicAuth")
    @PatchMapping("/assign-role")
    @TrackExecutionTime("Assign Role to User")
    public ResponseEntity<UserResponse> assignRole(@Valid @RequestBody AssignRoleRequest assignRoleRequest) {
        UserResponse response = userService.assignRole(assignRoleRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
