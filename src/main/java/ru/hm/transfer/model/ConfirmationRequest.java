package ru.hm.transfer.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ConfirmationRequest {
    @NotBlank
    private String operationId;
    @NotBlank
    private String code;
}
