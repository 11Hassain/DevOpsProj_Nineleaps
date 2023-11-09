package com.example.devopsproj.commons.enumerations;
/**
 * This enum represents user roles within the application.
 * It defines the available roles, such as SUPER_ADMIN, ADMIN, PROJECT_MANAGER, and USER.
 */
public enum EnumRole {


    SUPER_ADMIN("SUPER_ADMIN"), //0
    ADMIN("ADMIN"), //1
    PROJECT_MANAGER("PROJECT_MANAGER"), //2
    USER("USER"); //3

    private final String value;

    EnumRole(String value){
        this.value = value;
    }

    public String getEnumRole(){
        return value;
    }
}
