package ru.hm.transfer.model;

public class Response {

    public Response(String operationId) {
        this.operationId = operationId;
    }

    private String operationId;

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
}
