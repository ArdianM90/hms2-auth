package com.hms.auth.model;

import java.util.UUID;

public record AppUserDto(UUID id, String email, String passwordHash, String roleCode) {}
