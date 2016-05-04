package org.anole.infrastructure.model;

import java.util.Date;

public class AnoleUserProjectMap {
    private Integer id;

    private String username;

    private String project;

    private Byte role;

    private Byte envId;

    private Date createTime;

    private Date updateTime;

    private Byte status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project == null ? null : project.trim();
    }

    public Byte getRole() {
        return role;
    }

    public void setRole(Byte role) {
        this.role = role;
    }

    public Byte getEnvId() {
        return envId;
    }

    public void setEnvId(Byte envId) {
        this.envId = envId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}