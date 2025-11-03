package cl.duoc.ms_lab.security;

import java.util.Set;

public record JwtUser(
        String username,   // sub
        String userId,     // claim userId
        Set<String> roles, // ADMIN / LAB_TECH
        String labCode     // puede ser null si ADMIN
) {}
