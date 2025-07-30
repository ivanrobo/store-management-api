package ro.robert.store.management.user.control;

import org.springframework.stereotype.Component;
import ro.robert.store.management.user.entity.UserEntity;
import ro.robert.store.management.user.entity.request.UserCreateRequest;
import ro.robert.store.management.user.entity.response.UserResponse;

@Component
public class UserMapper {
    
    public UserEntity toEntity(UserCreateRequest request) {
        UserEntity entity = new UserEntity();
        entity.setUsername(request.getUsername());
        entity.setPassword(request.getPassword());
        entity.setEmail(request.getEmail());
        entity.setEnabled(true);
        return entity;
    }
    
    public UserResponse toResponse(UserEntity entity) {
        UserResponse response = new UserResponse();
        response.setId(entity.getId());
        response.setUsername(entity.getUsername());
        response.setEmail(entity.getEmail());
        response.setEnabled(entity.getEnabled());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setRoles(entity.getRoles());
        return response;
    }
}
