package ru.hm.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.hm.transfer.model.TransferOperations;

@Repository
public interface OperationsRepository extends JpaRepository<TransferOperations, String> {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    default TransferOperations saveInNewTransaction(TransferOperations transferOperations){
        return this.save(transferOperations);
    }
}
