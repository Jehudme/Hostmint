package com.hostmint.app.service.primary;

import com.hostmint.app.aop.audit.Auditable;
import com.hostmint.app.domain.User;
import com.hostmint.app.repository.AuthorityRepository;
import com.hostmint.app.repository.UserRepository;
import com.hostmint.app.repository.search.UserSearchRepository;
import com.hostmint.app.service.UserService;
import com.hostmint.app.service.dto.AdminUserDTO;
import java.util.Optional;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PrimaryUserService extends UserService {

    public PrimaryUserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        UserSearchRepository userSearchRepository,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager
    ) {
        super(userRepository, passwordEncoder, userSearchRepository, authorityRepository, cacheManager);
    }

    @Override
    @Auditable(action = "USER_REGISTERED", entityName = "User", entityIdExpression = "#result.id", message = "A new user registered.")
    public User registerUser(AdminUserDTO userDTO, String password) {
        return super.registerUser(userDTO, password);
    }

    @Override
    @Auditable(action = "USER_CREATED", entityName = "User", entityIdExpression = "#result.id", message = "Admin created a new user.")
    public User createUser(AdminUserDTO userDTO) {
        return super.createUser(userDTO);
    }

    @Override
    @Auditable(action = "USER_UPDATED", entityName = "User", entityIdExpression = "#userDTO.id", message = "Admin updated a user.")
    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        return super.updateUser(userDTO);
    }

    @Override
    @Auditable(action = "USER_DELETED", entityName = "User", message = "Admin deleted a user.")
    public void deleteUser(String login) {
        super.deleteUser(login);
    }

    @Override
    @Auditable(action = "PASSWORD_CHANGED", entityName = "User", message = "User changed their password.")
    public void changePassword(String currentClearTextPassword, String newPassword) {
        super.changePassword(currentClearTextPassword, newPassword);
    }
}
