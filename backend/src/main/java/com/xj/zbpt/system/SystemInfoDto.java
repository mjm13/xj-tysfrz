package com.xj.zbpt.system;

/**
 * 平台运行态版本信息（非 demo 业务）。
 */
public record SystemInfoDto(
        String platformName,
        String release,
        String backendVersion,
        String buildTime,
        String gitCommit,
        String flywayVersion,
        String apiPrefix,
        boolean maintenance
) {
}
