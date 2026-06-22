package com.xj.tysfrz.basic.system;

import com.xj.tysfrz.framework.config.AppInfoProperties;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Service;

@Service
public class SystemInfoService {

    private final AppInfoProperties appInfo;
    private final ObjectProvider<BuildProperties> buildProperties;
    private final ObjectProvider<GitProperties> gitProperties;
    private final ObjectProvider<Flyway> flyway;

    public SystemInfoService(
            AppInfoProperties appInfo,
            ObjectProvider<BuildProperties> buildProperties,
            ObjectProvider<GitProperties> gitProperties,
            ObjectProvider<Flyway> flyway
    ) {
        this.appInfo = appInfo;
        this.buildProperties = buildProperties;
        this.gitProperties = gitProperties;
        this.flyway = flyway;
    }

    public SystemInfoDto getInfo() {
        String release = appInfo.release();
        return new SystemInfoDto(
                appInfo.platformName(),
                release,
                release,
                buildProperties.getIfAvailable() != null
                        ? buildProperties.getObject().getTime().toString()
                        : "unknown",
                gitProperties.getIfAvailable() != null
                        ? gitProperties.getObject().getShortCommitId()
                        : "unknown",
                resolveFlywayVersion(),
                "/api",
                appInfo.maintenance()
        );
    }

    private String resolveFlywayVersion() {
        Flyway instance = flyway.getIfAvailable();
        if (instance == null) {
            return "unknown";
        }
        MigrationInfo current = instance.info().current();
        if (current != null && current.getVersion() != null) {
            return current.getVersion().getVersion();
        }
        MigrationInfo[] applied = instance.info().applied();
        if (applied.length == 0) {
            return "0";
        }
        MigrationInfo latest = applied[applied.length - 1];
        return latest.getVersion() != null ? latest.getVersion().getVersion() : "0";
    }
}
