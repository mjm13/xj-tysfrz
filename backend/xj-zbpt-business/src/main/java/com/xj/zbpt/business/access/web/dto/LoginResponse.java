package com.xj.zbpt.business.access.web.dto;

import java.util.Set;

public record LoginResponse(String accessToken, UserProfileDto profile) {
}
