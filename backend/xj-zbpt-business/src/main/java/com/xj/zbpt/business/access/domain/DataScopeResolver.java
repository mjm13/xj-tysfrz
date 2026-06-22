package com.xj.zbpt.business.access.domain;

import com.xj.zbpt.common.access.DataScope;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DataScopeResolver {

    public Set<String> resolve(DataScope dataScope, String departmentCode, List<OrgNodeRecord> orgNodes) {
        if (dataScope == DataScope.GLOBAL) {
            return Set.of();
        }
        if (departmentCode == null || departmentCode.isBlank()) {
            throw new IllegalArgumentException("departmentCode required for non-GLOBAL DataScope");
        }
        Set<String> knownCodes = new HashSet<>();
        Map<String, List<String>> children = new HashMap<>();
        for (OrgNodeRecord node : orgNodes) {
            knownCodes.add(node.code());
            if (node.parentCode() != null) {
                children.computeIfAbsent(node.parentCode(), k -> new java.util.ArrayList<>()).add(node.code());
            }
        }
        if (!knownCodes.contains(departmentCode)) {
            throw new IllegalArgumentException("Unknown department code: " + departmentCode);
        }
        return switch (dataScope) {
            case OWN_DEPT -> Set.of(departmentCode);
            case OWN_DEPT_AND_SUB -> collectDescendants(departmentCode, children);
            case GLOBAL -> Set.of();
        };
    }

    public boolean isGlobalScope(DataScope dataScope) {
        return dataScope == DataScope.GLOBAL;
    }

    private Set<String> collectDescendants(String root, Map<String, List<String>> children) {
        Set<String> result = new HashSet<>();
        ArrayDeque<String> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            String current = queue.removeFirst();
            result.add(current);
            List<String> childList = children.getOrDefault(current, List.of());
            queue.addAll(childList);
        }
        return result;
    }

    public record OrgNodeRecord(String code, String parentCode) {
    }
}
