package ru.hm.transfer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hm.transfer.TransferApplication;
import ru.hm.transfer.exception.*;
import ru.hm.transfer.model.*;
import ru.hm.transfer.repository.*;


import java.time.LocalDateTime;
import java.util.List;


@Service
public class MoneyTransferService {

    public static final String PASSWORD = "0000";

    private final AccountRepository accountRepository;

    private final OperationsRepository operationsRepository;

    public MoneyTransferService(AccountRepository accountRepository,
            OperationsRepository operationsRepository) {
        this.accountRepository = accountRepository;
        this.operationsRepository = operationsRepository;
    }

    @Transactional
    public Response transfer(Transfer transfer) {
        TransferOperations transferOperations = new TransferOperations(transfer);
        transferOperations = operationsRepository.saveInNewTransaction(transferOperations);
        Account accountFrom = accountRepository.findById(transfer.getCardFromNumber())
                                               .orElseThrow(() -> new CardValidationException("card not found"));
        validateAccount(transfer, accountFrom);
        Account accountTo = accountRepository.findById(transfer.getCardToNumber())
                                             .orElseThrow(() -> new CardValidationException("card not found"));
        checkAccountNotOutdated(accountTo);
        if (!verifyCurrency(accountTo, transfer)) {
            throw new CardValidationException("not valid CardTo account");
        }
        accountFrom.getBalance().setValue(accountFrom.getBalance().getValue()
                                                  - getAmountWithCommission(transfer.getAmount().getValue()));
        accountTo.getBalance().setValue(accountTo.getBalance().getValue() + transfer.getAmount().getValue());
        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);
        transferOperations.setSuccess(true);
        operationsRepository.saveInNewTransaction(transferOperations);
        return new Response(transferOperations.getId());
    }

    public Response confirmOperation(String operationId, String verificationCode) {
        if (isPasswordValid(verificationCode) && isOperationExists(operationId)) {
            return new Response(operationId);
        }
        throw new IncorrectConformationException("Incorrect operation Id or verification code");
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
        account.getBalance().setValue(account.getBalance().getValue() + amount);
        return accountRepository.save(account);
    }

    public List<Account> allAccounts() {
        return accountRepository.findAll();
    }

    private boolean isPasswordValid(String verificationCode) {
        return PASSWORD.equals(verificationCode);
    }

    private boolean isOperationExists(String operationId) {
        return operationsRepository.existsById(operationId);
    }

    private void validateAccount(Transfer transfer, Account accountFrom) {
        if (
                (accountFrom.getCardFromCVV().equals(transfer.getCardFromCVV()))
                && (accountFrom.getCardFromValidTill().equals(transfer.getCardFromValidTill()))
                && verifyCurrency(accountFrom, transfer)
        ) {
            checkAccountNotOutdated(accountFrom);
            if (accountFrom.getBalance().getValue() < getAmountWithCommission(transfer.getAmount().getValue())) {
                throw new MoneyTransferException("not enough money");
            }
        } else {
            throw new CardValidationException("not valid");
        }
    }

    private int getAmountWithCommission(int value) {
        return Math.round(TransferApplication.PERCENT_OF_COMMISSION * value) + value;
    }

    private void checkAccountNotOutdated(Account account) {
        LocalDateTime date = LocalDateTime.now();
        int month = Integer.parseInt(account.getCardFromValidTill().substring(0, 2));
        int year = Integer.parseInt(account.getCardFromValidTill().substring(3, 5));
        if (!(((date.getYear() % 100) < year)
                || (((date.getYear() % 100) == year) && (date.getMonthValue() <= month)))) {
            throw new CardValidationException("Card outdated");
        }
    }

    private boolean verifyCurrency(Account account, Transfer transfer) {
        return account.getBalance().getCurrency().equalsIgnoreCase(transfer.getAmount().getCurrency());
    }

}
