package ru.hm.transfer.service;

import jdk.dynalink.Operation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import ru.hm.transfer.exception.*;
import ru.hm.transfer.model.*;
import ru.hm.transfer.repository.*;


import java.nio.file.AccessMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static ru.hm.transfer.service.MoneyTransferService.PASSWORD;

@RunWith(SpringRunner.class)
class MoneyTransferServiceTest {

    public static final int VALUE = 100;
    public static final String WRONG_PASSWORD = "wrong password";
    private static MoneyTransferService service;
    private static AccountRepository accountRepository;
    private static OperationsRepository operationsRepository;

    @BeforeAll
    static void prepareForTest() {
        accountRepository = Mockito.mock(AccountRepository.class);
        operationsRepository = Mockito.mock(OperationsRepository.class);
        service = new MoneyTransferService(accountRepository, operationsRepository);
    }

    public static Stream<Arguments> allAccounts() {
        return Stream.of(Arguments.of(List.of(), "Empty list"),
                         Arguments.of(List.of(new Account(), new Account()), "Ordinary list"));
    }

    public static Stream<Arguments> allOperation() {
        return Stream.of(Arguments.of(List.of(), "Empty list"),
                         Arguments.of(List.of(new TransferOperations(), new TransferOperations()), "Ordinary list"));
    }

    @Test
    void transfer() {
        Transfer transfer = new Transfer();
        transfer.setCardToNumber("22");
        transfer.setCardFromNumber("11");
        Amount amount=new Amount();
        amount.setCurrency("r");
        amount.setValue(100);
        transfer.setAmount(amount);
        Account accountTo = new Account();
        Account accountFrom = new Account();
        when(operationsRepository.saveInNewTransaction(Mockito.any())).thenReturn(new TransferOperations(transfer))
                                                                      .thenReturn(new TransferOperations(transfer))
                                                                      .thenReturn(new TransferOperations(transfer))
                                                                      .thenReturn(new TransferOperations(transfer))
                                                                      .thenReturn(new TransferOperations(transfer))
                                                                      .thenReturn(new TransferOperations(transfer));
        when(accountRepository.findById("11")).thenReturn(Optional.of(accountFrom))
                                              .thenReturn(Optional.of(accountFrom))
        .thenReturn(Optional.of(accountFrom));
        when(accountRepository.findById("22")).thenReturn(Optional.of(accountTo))
                                              .thenReturn(Optional.of(accountTo))
                                              .thenReturn(Optional.of(accountTo));



        assertThrows(NullPointerException.class,()->service.transfer(transfer));
        Mockito.verify(operationsRepository,Mockito.times(1)).saveInNewTransaction(Mockito.any());

        accountFrom.setCardFromValidTill("12/22");
        accountFrom.setCardFromCVV("111");
        Amount balance = new Amount();
        balance.setValue(1000);
        balance.setCurrency("r");
        accountFrom.setBalance(balance);

        accountTo.setCardFromValidTill("12/22");
        Amount balanceTo = new Amount();
        balance.setCurrency("r");
        accountTo.setBalance(balance);
        transfer.setCardFromCVV("222");
        assertThrows(CardValidationException.class,()->service.transfer(transfer));
        Mockito.verify(operationsRepository,Mockito.times(2)).saveInNewTransaction(Mockito.any());

        transfer.setCardFromCVV("111");
        transfer.setCardFromValidTill("12/22");
        assertEquals(Response.class,service.transfer(transfer).getClass());
        Mockito.verify(operationsRepository,Mockito.times(4)).saveInNewTransaction(Mockito.any());

    }

    @Test
    void confirmOperation() {
        when(operationsRepository.existsById("1")).thenReturn(true);
        when(operationsRepository.existsById("2")).thenReturn(false);
        assertThrows(IncorrectConformationException.class,()->service.confirmOperation("1",WRONG_PASSWORD));
        assertThrows(IncorrectConformationException.class,()->service.confirmOperation("2",WRONG_PASSWORD));
        assertThrows(IncorrectConformationException.class,()->service.confirmOperation("2",PASSWORD));
        assertEquals("1", service.confirmOperation("1", PASSWORD).getOperationId());
        assertThrows(RuntimeException.class,()->service.confirmOperation(null,PASSWORD));
        assertThrows(RuntimeException.class,()->service.confirmOperation("1",null));
    }

    @MethodSource
    @ParameterizedTest(name = "{1}")
    void allOperation(List<TransferOperations> list, String testName) {
        when(operationsRepository.findAll()).thenReturn(list);
        assertEquals(list, service.allOperation());
    }

    @Test
    void createAccount() {

    }

    @Test
    void putMoneyOnCard() {
        Account account = new Account();
        account.setBalance(new Amount());
        account.getBalance().setValue(VALUE);
        when(accountRepository.findById("1")).thenReturn(Optional.empty());
        assertThrows(MoneyTransferException.class, () -> service.putMoneyOnCard("1", VALUE));
        when(accountRepository.findById("2")).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        assertEquals(VALUE + VALUE, service.putMoneyOnCard("2", VALUE).getBalance().getValue());
    }

    @MethodSource
    @ParameterizedTest(name = "{1}")
    void allAccounts(List<Account> accounts, String testName) {
        when(accountRepository.findAll()).thenReturn(accounts);
        assertEquals(accounts, service.allAccounts());
    }

}