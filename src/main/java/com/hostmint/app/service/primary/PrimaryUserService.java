package com.hostmint.app.service.primary;

import com.hostmint.app.repository.AuthorityRepository;
import com.hostmint.app.repository.PersistentTokenRepository;
import com.hostmint.app.repository.UserRepository;
import com.hostmint.app.repository.search.UserSearchRepository;
import com.hostmint.app.service.UserService;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;

@Primary
public class PrimaryUserService extends UserService {

    public PrimaryUserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        UserSearchRepository userSearchRepository,
        PersistentTokenRepository persistentTokenRepository,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager
    ) {
        super(userRepository, passwordEncoder, userSearchRepository, persistentTokenRepository, authorityRepository, cacheManager);
    }
}
