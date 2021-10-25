package ru.hm.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hm.transfer.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

}
