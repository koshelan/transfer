package ru.hm.transfer.model;

import lombok.*;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.auditing.CurrentDateTimeProvider;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "operations")
@OptimisticLocking(type = OptimisticLockType.VERSION)
public class TransferOperations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Embedded
    private Transfer transfer;

    @CreatedDate
    @Column(nullable=false)
    private Date date;

    @Value("false")
    private boolean success;

    public TransferOperations(Transfer transfer) {
        this.transfer = transfer;
        this.date = new Date();
    }


}
