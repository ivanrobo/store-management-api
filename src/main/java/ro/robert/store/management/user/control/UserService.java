package ro.robert.store.management.user.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.robert.store.management.exception.entity.ServiceErrorType;
import ro.robert.store.management.exception.entity.ServiceException;
import ro.robert.store.management.user.boundary.RoleRepository;
import ro.robert.store.management.user.boundary.UserRepository;
import ro.robert.store.management.user.entity.RoleEntity;
import ro.robert.store.management.user.entity.UserEntity;
import ro.robert.store.management.user.entity.request.UserCreateRequest;
import ro.robert.store.management.user.entity.response.UserResponse;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        log.info("Creating new user with username: {}", request.getUsername());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new ServiceException(ServiceErrorType.VALIDATION_ERROR, 
                "Username already exists: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new ServiceException(ServiceErrorType.VALIDATION_ERROR, 
                "Email already exists: " + request.getEmail());
        }
        
        RoleEntity userRole = roleRepository.findByName("USER")
            .orElseThrow(() -> {
                log.error("USER role not found in database");
                return new ServiceException(ServiceErrorType.INTERNAL_SERVER_ERROR, 
                    "USER role not found in system");
            });
        
        UserEntity entity = userMapper.toEntity(request);
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setRoles(Set.of(userRole));
        UserEntity savedEntity = userRepository.save(entity);
        
        log.info("Successfully created user with ID: {} and username: {} with USER role", 
            savedEntity.getId(), savedEntity.getUsername());
        return userMapper.toResponse(savedEntity);
    }
}
