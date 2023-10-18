package com.example.devopsproj.commons.enumerations;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

 class EnumRoleTest {

    @Test
     void testGetEnumRoleSuperAdmin() {
        String role = EnumRole.SUPER_ADMIN.getEnumRole();
        assertThat(role).isEqualTo("SUPER_ADMIN");
    }

    @Test
     void testGetEnumRoleAdmin() {
        String role = EnumRole.ADMIN.getEnumRole();
        assertThat(role).isEqualTo("ADMIN");
    }

    @Test
     void testGetEnumRoleProjectManager() {
        String role = EnumRole.PROJECT_MANAGER.getEnumRole();
        assertThat(role).isEqualTo("PROJECT_MANAGER");
    }

    @Test
    void testGetEnumRoleUser() {
        String role = EnumRole.USER.getEnumRole();
        assertThat(role).isEqualTo("USER");
    }
}
