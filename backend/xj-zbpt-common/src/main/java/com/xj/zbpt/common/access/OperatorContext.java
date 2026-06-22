package com.xj.zbpt.common.access;

import java.util.Collections;
import java.util.Set;

public record OperatorContext(
        String platformUserId,
        String username,
        Set<String> roles,
        Set<String> permissions,
        DataScope dataScope,
        String departmentCode,
        Set<String> scopedDeptCodes,
        boolean globalScope
) {
    public OperatorContext {
        roles = roles == null ? Set.of() : Set.copyOf(roles);
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
        scopedDeptCodes = scopedDeptCodes == null ? Set.of() : Set.copyOf(scopedDeptCodes);
    }

    public boolean isGlobal() {
        return globalScope;
    }

    public boolean hasPermission(String permissionCode) {
        return permissions.contains(permissionCode);
    }

    public static OperatorContext global(
            String platformUserId,
            String username,
            Set<String> roles,
            Set<String> permissions,
            String departmentCode
    ) {
        return new OperatorContext(
                platformUserId,
                username,
                roles,
                permissions,
                DataScope.GLOBAL,
                departmentCode,
                Collections.emptySet(),
                true
        );
    }
}
