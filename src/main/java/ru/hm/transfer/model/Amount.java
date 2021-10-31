package ru.hm.transfer.model;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Embeddable
public class Amount {
    @Min(0)
    private int value;

    @NotBlank
    private String currency;

}
