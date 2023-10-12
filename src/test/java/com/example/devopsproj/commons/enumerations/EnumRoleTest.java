package com.example.devopsproj.commons.enumerations;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumRoleTest {

    @Test
    public void testGetEnumRoleSuperAdmin() {
        String role = EnumRole.SUPER_ADMIN.getEnumRole();
        assertThat(role).isEqualTo("SUPER_ADMIN");
    }

    @Test
    public void testGetEnumRoleAdmin() {
        String role = EnumRole.ADMIN.getEnumRole();
        assertThat(role).isEqualTo("ADMIN");
    }

    @Test
    public void testGetEnumRoleProjectManager() {
        String role = EnumRole.PROJECT_MANAGER.getEnumRole();
        assertThat(role).isEqualTo("PROJECT_MANAGER");
    }

    @Test
    public void testGetEnumRoleUser() {
        String role = EnumRole.USER.getEnumRole();
        assertThat(role).isEqualTo("USER");
    }
}
