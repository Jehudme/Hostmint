package com.hostmint.app.service;

import com.hostmint.app.aop.audit.Audit;
import com.hostmint.app.domain.User;
import com.hostmint.app.domain.enumeration.LogLevel;
import com.hostmint.app.repository.AuthorityRepository;
import com.hostmint.app.repository.UserRepository;
import com.hostmint.app.repository.search.UserSearchRepository;
import com.hostmint.app.service.dto.AdminUserDTO;
import java.util.Optional;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom extension of the core JHipster UserService.
 * Auditing is now handled declaratively via the @Audit aspect.
 */
@Service
@Transactional
@Primary
public class ExtendedUserService extends UserService {

    public ExtendedUserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        UserSearchRepository userSearchRepository,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager
    ) {
        super(userRepository, passwordEncoder, userSearchRepository, authorityRepository, cacheManager);
    }

    @Override
    @Audit(action = "ACCOUNT_REGISTRATION", entity = "#result.login", message = "'New user registered via system'")
    public User registerUser(AdminUserDTO userDTO, String password) {
        return super.registerUser(userDTO, password);
    }

    @Override
    @Audit(action = "PASSWORD_CHANGED", entity = "'User'", level = LogLevel.WARN, message = "'User successfully updated their password'")
    public void changePassword(String currentClearTextPassword, String newPassword) {
        super.changePassword(currentClearTextPassword, newPassword);
    }

    @Override
    @Audit(action = "USER_PROFILE_UPDATED", entity = "#userDTO.login", message = "'Updated profile details'")
    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        return super.updateUser(userDTO);
    }

    @Override
    @Audit(action = "USER_DELETED", entity = "#login", level = LogLevel.WARN, message = "'Administrative deletion of user account'")
    public void deleteUser(String login) {
        super.deleteUser(login);
    }
}
