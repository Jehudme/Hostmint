package com.hostmint.app.service;

import com.hostmint.app.domain.User;
import com.hostmint.app.domain.enumeration.LogLevel;
import com.hostmint.app.repository.AuthorityRepository;
import com.hostmint.app.repository.UserRepository;
import com.hostmint.app.repository.search.UserSearchRepository;
import com.hostmint.app.service.dto.AdminUserDTO;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom extension of the core JHipster UserService to add auditing logic.
 * Since UserService is a class (not an interface), we extend it directly.
 */
@Service
@Transactional
@Primary
public class ExtendedUserService extends UserService {

    private final Logger log = LoggerFactory.getLogger(ExtendedUserService.class);
    private final InternalAuditService internalAuditService;

    public ExtendedUserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        UserSearchRepository userSearchRepository,
        CacheManager cacheManager,
        AuthorityRepository authorityRepository,
        InternalAuditService internalAuditService
    ) {
        // Passing the required dependencies to the original JHipster UserService
        super(userRepository, passwordEncoder, userSearchRepository, authorityRepository, cacheManager);
        this.internalAuditService = internalAuditService;
    }

    @Override
    public User registerUser(AdminUserDTO userDTO, String password) {
        User user = super.registerUser(userDTO, password);

        internalAuditService.log("ACCOUNT_REGISTRATION", "User", LogLevel.INFO, "New user registered: " + user.getLogin());

        return user;
    }

    @Override
    public void changePassword(String currentClearTextPassword, String newPassword) {
        // Perform the actual password change
        super.changePassword(currentClearTextPassword, newPassword);

        // Log the security event
        internalAuditService.log("PASSWORD_CHANGED", "User", LogLevel.WARN, "User successfully updated their password.");
    }

    @Override
    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        Optional<AdminUserDTO> result = super.updateUser(userDTO);

        result.ifPresent(updated ->
            internalAuditService.log("USER_PROFILE_UPDATED", "User", LogLevel.INFO, "Updated profile details for: " + updated.getLogin())
        );

        return result;
    }

    @Override
    public void deleteUser(String login) {
        // Important: Log BEFORE calling super.deleteUser if you want to be sure
        // the record is still there for the auditor to see.
        internalAuditService.log("USER_DELETED", "User", LogLevel.WARN, "Administrative deletion of user account: " + login);

        super.deleteUser(login);
    }
}
