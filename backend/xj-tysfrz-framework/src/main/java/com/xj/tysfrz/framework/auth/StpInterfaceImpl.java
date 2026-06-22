package com.xj.tysfrz.framework.auth;

import cn.dev33.satoken.stp.StpInterface;
import com.xj.tysfrz.common.access.OperatorContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        OperatorContext operator = OperatorSessionSupport.getOperator();
        if (operator == null) {
            return List.of();
        }
        return new ArrayList<>(operator.permissions());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        OperatorContext operator = OperatorSessionSupport.getOperator();
        if (operator == null) {
            return List.of();
        }
        return new ArrayList<>(operator.roles());
    }
}
