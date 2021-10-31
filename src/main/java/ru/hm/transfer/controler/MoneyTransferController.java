package ru.hm.transfer.controler;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hm.transfer.model.*;
import ru.hm.transfer.service.MoneyTransferService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@Validated
@RequestMapping("/")
public class MoneyTransferController {

    private final MoneyTransferService service;

    public MoneyTransferController(MoneyTransferService service) {
        this.service = service;
    }


    @GetMapping("/")
    public String hello(){
        return "Hello from Money Transfer App";
    }

    @PostMapping("/transfer")
    public Response transfer (@RequestBody @Valid Transfer transfer) {
        System.out.println(transfer);
        return service.transfer(transfer);

    }

    @PostMapping("/confirmOperation")
    public Response confirmOperation(@RequestBody @Valid ConfirmationRequest confirmationRequest){
        System.out.println(confirmationRequest);
        return service.confirmOperation(confirmationRequest.getOperationId(),confirmationRequest.getCode());
    }

    @PostMapping("/put")
    public Account put(@RequestBody @NotBlank String number, @RequestBody @Min(0) Integer amount) {
        return service.putMoneyOnCard(number, amount);
    }

    @GetMapping("/accounts")
    public List<Account> getAllAccounts(){
        return service.allAccounts();
    }

    @GetMapping("/operations")
    public List<TransferOperations> getAllOperations(){
        return service.allOperation();
    }

    @PostMapping("/newaccount")
    public Account newAccount(@RequestBody @Valid Account account) {
        return service.createAccount(account);
    }



}
