package com.example.DevOpsProj.dto.responseDto;

public class JiraProjectDTO {
    private String name;
    private String key;
    private String projectTypeKey;
    private String leadAccountId;

    public JiraProjectDTO() {
    }

    public JiraProjectDTO(String name, String key, String projectTypeKey, String leadAccountId) {
        this.name = name;
        this.key = key;
        this.projectTypeKey = projectTypeKey;
        this.leadAccountId = leadAccountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProjectTypeKey() {
        return projectTypeKey;
    }

    public void setProjectTypeKey(String projectTypeKey) {
        this.projectTypeKey = projectTypeKey;
    }

    public String getLeadAccountId() {
        return leadAccountId;
    }

    public void setLeadAccountId(String leadAccountId) {
        this.leadAccountId = leadAccountId;
    }
}
