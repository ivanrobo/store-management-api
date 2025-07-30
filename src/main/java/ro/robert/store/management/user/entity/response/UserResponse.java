package ro.robert.store.management.user.entity.response;

import lombok.Data;
import ro.robert.store.management.user.entity.RoleEntity;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<RoleEntity> roles;
}
