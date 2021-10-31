package ru.hm.transfer.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Embeddable
public class Transfer {

    @Length(min = 16,max = 16)
    @Pattern(regexp = "^[0-9]{1,16}$")
    private String cardFromNumber;

    @NotNull
    @Length(min = 5,max = 5)
    @Pattern(regexp = "[0-1][0-9]/[0-9][0-9]")
    private String cardFromValidTill;

    @NotNull
    @Length(min = 3,max = 3)
    @Pattern(regexp = "^[0-9]{1,3}$")
    private String cardFromCVV;

    @Length(min = 16,max = 16)
    @Pattern(regexp = "^[0-9]{1,16}$")
    private String cardToNumber;

    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "value", column = @Column(name = "transfer_value")),
            @AttributeOverride( name = "currency", column = @Column(name = "transfer_currency"))
    })
    private Amount amount;


}
