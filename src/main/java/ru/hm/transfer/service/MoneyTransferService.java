package ru.hm.transfer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hm.transfer.exception.CardValidationException;
import ru.hm.transfer.exception.IncorrectConformationException;
import ru.hm.transfer.exception.MoneyTransferException;
import ru.hm.transfer.model.Account;
import ru.hm.transfer.model.Response;
import ru.hm.transfer.model.Transfer;
import ru.hm.transfer.model.TransferOperations;
import ru.hm.transfer.repository.AccountRepository;
import ru.hm.transfer.repository.OperationsRepository;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

@Service
public class MoneyTransferService {

    private final AccountRepository accountRepository;
    private final OperationsRepository operationsRepository;

    public MoneyTransferService(AccountRepository accountRepository,
            OperationsRepository operationsRepository) {
        this.accountRepository = accountRepository;
        this.operationsRepository = operationsRepository;
    }

    @Transactional
    public Response transfer(Transfer transfer,TransferOperations transferOperations) {
        Account accountFrom = accountRepository.findById(transfer.getCardFromNumber())
                                               .orElseThrow(() -> new CardValidationException("card not found"));
        validateAccount(transfer, accountFrom);
        Account accountTo = accountRepository.findById(transfer.getCardToNumber())
                                             .orElseThrow(() -> new CardValidationException("card not found"));

        checkAccountNotOutdated(accountTo);
        if (!accountTo.getBalanceCurrency().equalsIgnoreCase(transfer.getAmount().getCurrency())) {
            throw new CardValidationException("not valid CardTo account");
        }
        accountFrom.setBalanceValue(accountFrom.getBalanceValue() - transfer.getAmount().getValue());
        accountTo.setBalanceValue(accountTo.getBalanceValue() + transfer.getAmount().getValue());
        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);
        transferOperations.setSuccess(true);
        saveOperation(transferOperations);
        return new Response(transferOperations.getId());
    }

    public Response confirmOperation(String operationId, String verificationCode) {
        if (("password").equals(verificationCode) &&
                operationsRepository.findById(operationId).isPresent()) {
            return new Response(operationId);
        } else {
            throw new IncorrectConformationException("Incorrect operation Id or verification code");
        }
    }

    private void validateAccount(Transfer transfer, Account accountFrom) {
        if ((accountFrom.getCardFromCVV().equals(transfer.getCardFromCVV()))
                && (accountFrom.getCardFromValidTill().equals(transfer.getCardFromValidTill()))
                && (accountFrom.getBalanceCurrency().equalsIgnoreCase(transfer.getAmount().getCurrency()))
        ) {
            checkAccountNotOutdated(accountFrom);
            if (accountFrom.getBalanceValue() < transfer.getAmount().getValue()) {
                throw new MoneyTransferException("not enough money");
            }
        } else {
            throw new CardValidationException("not valid");
        }

    }

    public TransferOperations saveOperation(TransferOperations transferOperations){
        return operationsRepository.save(transferOperations);
    }

    private void checkAccountNotOutdated(Account account) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT + 03: 00"));
        int month = Integer.parseInt(account.getCardFromValidTill().substring(0, 2));
        int year = Integer.parseInt(account.getCardFromValidTill().substring(2, 4));
        if (!(((c.get(Calendar.YEAR) % 100) < year)
                || (((c.get(Calendar.YEAR) % 100) == year) && (c.get(Calendar.MONTH) <= month)))) {
            throw new CardValidationException("Card outdated");
        }
    }


    public List<TransferOperations> allOperation() {
        return operationsRepository.findAll();
    }

    public Account createAccount(Account account) {
        checkAccountNotOutdated(account);
        return accountRepository.save(account);
    }

    public Account putMoneyOnCard(String number, Integer amount) {
        Account account = accountRepository.findById(number).orElseThrow(MoneyTransferException::new);
        account.setBalanceValue(account.getBalanceValue() + amount);
        return accountRepository.save(account);
    }

    public List<Account> allAccounts() {
        return accountRepository.findAll();
    }


}
