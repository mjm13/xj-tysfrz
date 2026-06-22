package com.xj.zbpt.business.access.service;

public interface AuthProvider {

    AuthResult authenticate(String username, String rawPassword);

    record AuthResult(boolean success, String failureReason) {
        public static AuthResult ok() {
            return new AuthResult(true, null);
        }

        public static AuthResult fail(String reason) {
            return new AuthResult(false, reason);
        }
    }
}
