package com.xj.zbpt.business.access.web;

import com.xj.zbpt.business.access.web.dto.ScopedDeptsDto;
import com.xj.zbpt.common.access.OperatorContext;
import com.xj.zbpt.common.response.ApiResponse;
import com.xj.zbpt.framework.auth.CurrentOperator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @GetMapping("/scoped-depts")
    public ApiResponse<ScopedDeptsDto> scopedDepts(@CurrentOperator OperatorContext operator) {
        return ApiResponse.ok(new ScopedDeptsDto(
                operator.isGlobal(),
                operator.dataScope().name(),
                operator.departmentCode(),
                operator.scopedDeptCodes()
        ));
    }
}
