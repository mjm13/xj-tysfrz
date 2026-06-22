package com.xj.zbpt.framework.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.xj.zbpt.common.access.OperatorContext;

public final class OperatorSessionSupport {

    private OperatorSessionSupport() {
    }

    public static void bindOperator(OperatorContext operator) {
        StpUtil.getSession().set(OperatorSessionKeys.OPERATOR, operator);
    }

    public static OperatorContext getOperator() {
        if (!StpUtil.isLogin()) {
            return null;
        }
        Object value = StpUtil.getSession().get(OperatorSessionKeys.OPERATOR);
        if (value instanceof OperatorContext operator) {
            return operator;
        }
        return null;
    }
}
