package com.xj.zbpt.basic.ping;

import com.xj.zbpt.common.exception.BizException;
import com.xj.zbpt.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 示例链路控制器（非业务）：用于验证统一响应与全局异常等横切能力。
 */
@RestController
@RequestMapping("/api/ping")
@Tag(name = "Ping", description = "骨架自检接口")
public class PingController {

    @GetMapping
    @Operation(summary = "存活探测，返回 pong")
    public ApiResponse<String> ping() {
        return ApiResponse.ok("pong");
    }

    @GetMapping("/boom")
    @Operation(summary = "触发业务异常，用于验证全局异常处理")
    public ApiResponse<Void> boom(@RequestParam(defaultValue = "演示业务异常") String message) {
        throw new BizException(message);
    }
}
