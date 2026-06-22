package com.xj.tysfrz.business.access.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xj.tysfrz.business.access.infrastructure.OrgNodeEntity;
import com.xj.tysfrz.business.access.infrastructure.OrgNodeMapper;
import com.xj.tysfrz.business.access.web.dto.CreateOrgNodeRequest;
import com.xj.tysfrz.business.access.web.dto.OrgNodeSummaryDto;
import com.xj.tysfrz.business.access.web.dto.UpdateOrgNodeRequest;
import com.xj.tysfrz.common.exception.BizException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrgNodeAdminAppService {

    private final OrgNodeMapper orgNodeMapper;

    public OrgNodeAdminAppService(OrgNodeMapper orgNodeMapper) {
        this.orgNodeMapper = orgNodeMapper;
    }

    public List<OrgNodeSummaryDto> listOrgNodes() {
        return orgNodeMapper.selectList(new LambdaQueryWrapper<OrgNodeEntity>().orderByAsc(OrgNodeEntity::getCode))
                .stream()
                .map(this::toSummary)
                .toList();
    }

    public List<OrgNodeSummaryDto> listRootNodes() {
        return orgNodeMapper.selectList(new LambdaQueryWrapper<OrgNodeEntity>()
                        .isNull(OrgNodeEntity::getParentCode)
                        .orderByAsc(OrgNodeEntity::getCode))
                .stream()
                .map(this::toSummary)
                .toList();
    }

    public List<OrgNodeSummaryDto> listChildNodes(String parentCode) {
        requireNode(parentCode);
        return orgNodeMapper.selectList(new LambdaQueryWrapper<OrgNodeEntity>()
                        .eq(OrgNodeEntity::getParentCode, parentCode)
                        .orderByAsc(OrgNodeEntity::getCode))
                .stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional
    public OrgNodeSummaryDto createOrgNode(CreateOrgNodeRequest request) {
        if (orgNodeMapper.selectById(request.code()) != null) {
            throw new BizException("组织节点 code 已存在: " + request.code());
        }
        OrgNodeEntity parent = requireNode(request.parentCode());
        assertNoCycle(request.code(), request.parentCode());

        OrgNodeEntity entity = new OrgNodeEntity();
        entity.setCode(request.code());
        entity.setName(request.name());
        entity.setParentCode(request.parentCode());
        entity.setLevel(parent.getLevel() + 1);
        orgNodeMapper.insert(entity);
        return toSummary(entity);
    }

    @Transactional
    public OrgNodeSummaryDto updateOrgNode(String code, UpdateOrgNodeRequest request) {
        OrgNodeEntity entity = requireNode(code);
        entity.setName(request.name());
        if (request.parentCode() != null && !request.parentCode().equals(entity.getParentCode())) {
            OrgNodeEntity parent = requireNode(request.parentCode());
            assertNoCycle(code, request.parentCode());
            entity.setParentCode(request.parentCode());
            entity.setLevel(parent.getLevel() + 1);
        }
        orgNodeMapper.updateById(entity);
        return toSummary(entity);
    }

    private OrgNodeEntity requireNode(String code) {
        OrgNodeEntity entity = orgNodeMapper.selectById(code);
        if (entity == null) {
            throw new BizException("父节点不存在: " + code);
        }
        return entity;
    }

    private void assertNoCycle(String nodeCode, String newParentCode) {
        if (nodeCode.equals(newParentCode)) {
            throw new BizException("父节点设置会导致组织环");
        }
        Map<String, String> parentByCode = loadParentByCode();
        String current = newParentCode;
        while (current != null) {
            if (nodeCode.equals(current)) {
                throw new BizException("父节点设置会导致组织环");
            }
            current = parentByCode.get(current);
        }
    }

    private Map<String, String> loadParentByCode() {
        Map<String, String> parentByCode = new HashMap<>();
        for (OrgNodeEntity node : orgNodeMapper.selectList(null)) {
            parentByCode.put(node.getCode(), node.getParentCode());
        }
        return parentByCode;
    }

    private OrgNodeSummaryDto toSummary(OrgNodeEntity entity) {
        long childCount = orgNodeMapper.selectCount(new LambdaQueryWrapper<OrgNodeEntity>()
                .eq(OrgNodeEntity::getParentCode, entity.getCode()));
        return new OrgNodeSummaryDto(
                entity.getCode(),
                entity.getName(),
                entity.getParentCode(),
                childCount > 0
        );
    }
}
