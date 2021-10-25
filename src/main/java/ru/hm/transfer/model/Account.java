package ru.hm.transfer.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@Entity
@Table(name = "account")
@OptimisticLocking(type = OptimisticLockType.VERSION)
public class Account {
    @Id
    @Length(min = 16,max = 16)
    @Pattern(regexp = "^[0-9]{1,16}$")
    //@Column(length=16)
    private String cardNumber;
    @NotNull
    @Length(min = 4,max = 4)
    @Pattern(regexp = "^[0-1][0-9]{1,3}$")
    @Column(length=4, nullable=false, unique=false)
    private String cardFromValidTill;
    @NotNull
    @Length(min = 3,max = 3)
    @Pattern(regexp = "^[0-9]{1,3}$")
    @Column(length=3, nullable=false, unique=false)
    private String cardFromCVV;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "value", column = @Column(name = "balance_value")),
            @AttributeOverride( name = "currency", column = @Column(name = "balance_currency"))
    })
    @NotNull
    private Amount balance;

    @Version
    private long version;

    public int getBalanceValue(){
        return balance.getValue();
    }
    public void setBalanceValue(int newBalance){
        balance.setValue(newBalance);
    }
    public String getBalanceCurrency(){
        return balance.getCurrency();
    }

    @Override
    public String toString() {
        return "Account{" +
                "cardNumber='" + cardNumber + '\'' +
                ", cardFromValidTill='" + cardFromValidTill + '\'' +
                ", cardFromCVV='" + cardFromCVV + '\'' +
                ", balance=" + balance +
                ", version=" + version +
                '}';
    }
}
