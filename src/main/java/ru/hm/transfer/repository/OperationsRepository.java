package ru.hm.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hm.transfer.model.TransferOperations;

@Repository
public interface OperationsRepository extends JpaRepository<TransferOperations, String> {
}
