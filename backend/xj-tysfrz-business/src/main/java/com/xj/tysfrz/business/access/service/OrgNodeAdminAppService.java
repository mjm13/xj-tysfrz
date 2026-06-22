package com.xj.tysfrz.business.access.service;

import com.xj.tysfrz.business.access.infrastructure.OrgNodeMapper;
import com.xj.tysfrz.business.access.web.dto.OrgNodeSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgNodeAdminAppService {

    private final OrgNodeMapper orgNodeMapper;

    public OrgNodeAdminAppService(OrgNodeMapper orgNodeMapper) {
        this.orgNodeMapper = orgNodeMapper;
    }

    public List<OrgNodeSummaryDto> listOrgNodes() {
        return orgNodeMapper.selectList(null).stream()
                .map(n -> new OrgNodeSummaryDto(n.getCode(), n.getName(), n.getParentCode()))
                .toList();
    }
}
