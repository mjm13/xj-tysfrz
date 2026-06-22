package com.xj.tysfrz.business.access.service;

import com.xj.tysfrz.business.access.domain.UserStatus;
import com.xj.tysfrz.business.access.infrastructure.PlatformUserEntity;
import com.xj.tysfrz.business.access.infrastructure.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SelfBuiltAuthProvider implements AuthProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SelfBuiltAuthProvider(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResult authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .map(user -> validate(user, rawPassword))
                .orElse(AuthResult.fail("用户名或密码错误"));
    }

    private AuthResult validate(PlatformUserEntity user, String rawPassword) {
        if (user.getStatusEnum() == UserStatus.DISABLED) {
            return AuthResult.fail("账号已禁用");
        }
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            return AuthResult.fail("用户名或密码错误");
        }
        return AuthResult.ok();
    }
}
