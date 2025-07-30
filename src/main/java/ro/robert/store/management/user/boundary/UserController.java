package ro.robert.store.management.user.boundary;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ro.robert.store.management.annotation.TrackExecutionTime;
import ro.robert.store.management.user.control.UserService;
import ro.robert.store.management.user.entity.request.AssignRoleRequest;
import ro.robert.store.management.user.entity.request.UserCreateRequest;
import ro.robert.store.management.user.entity.response.UserResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    @TrackExecutionTime("Create User")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        UserResponse response = userService.createUser(userCreateRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PatchMapping("/assign-role")
    @TrackExecutionTime("Assign Role to User")
    public ResponseEntity<UserResponse> assignRole(@Valid @RequestBody AssignRoleRequest assignRoleRequest) {
        UserResponse response = userService.assignRole(assignRoleRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
