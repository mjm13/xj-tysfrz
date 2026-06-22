package com.xj.zbpt.business.access.domain;

import com.xj.zbpt.common.access.DataScope;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataScopeResolverTest {

    private final DataScopeResolver resolver = new DataScopeResolver();

    private static final List<DataScopeResolver.OrgNodeRecord> SAMPLE_TREE = List.of(
            new DataScopeResolver.OrgNodeRecord("SYSU", null),
            new DataScopeResolver.OrgNodeRecord("CAT-party", "SYSU"),
            new DataScopeResolver.OrgNodeRecord("01020", "CAT-party"),
            new DataScopeResolver.OrgNodeRecord("CAT-admin", "SYSU")
    );

    @Test
    void global_shouldReturnEmptyScopedSet() {
        Set<String> scoped = resolver.resolve(DataScope.GLOBAL, "SYSU", SAMPLE_TREE);
        assertThat(scoped).isEmpty();
        assertThat(resolver.isGlobalScope(DataScope.GLOBAL)).isTrue();
    }

    @Test
    void ownDept_shouldReturnSingleDepartment() {
        Set<String> scoped = resolver.resolve(DataScope.OWN_DEPT, "CAT-party", SAMPLE_TREE);
        assertThat(scoped).containsExactly("CAT-party");
    }

    @Test
    void ownDeptAndSub_shouldIncludeDescendants() {
        Set<String> scoped = resolver.resolve(DataScope.OWN_DEPT_AND_SUB, "CAT-party", SAMPLE_TREE);
        assertThat(scoped).containsExactlyInAnyOrder("CAT-party", "01020");
    }

    @Test
    void unknownDepartment_shouldThrow() {
        assertThatThrownBy(() -> resolver.resolve(DataScope.OWN_DEPT, "MISSING", SAMPLE_TREE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("MISSING");
    }
}
