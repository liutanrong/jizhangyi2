package com.liu.Account.module.dataobject;

/**
 * Created by tanrong on 16/7/4.
 */
public class EventLogDo {
    private Long id;
    private Long userId;
    private Long installationId;
    private Character isDeleted;
    private String operationName;
    private String beforeOperation;
    private String afterOperation;
    private String extension;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getInstallationId() {
        return installationId;
    }

    public void setInstallationId(Long installationId) {
        this.installationId = installationId;
    }

    public Character getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Character isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getBeforeOperation() {
        return beforeOperation;
    }

    public void setBeforeOperation(String beforeOperation) {
        this.beforeOperation = beforeOperation;
    }

    public String getAfterOperation() {
        return afterOperation;
    }

    public void setAfterOperation(String afterOperation) {
        this.afterOperation = afterOperation;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
