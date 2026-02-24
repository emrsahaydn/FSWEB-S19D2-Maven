package com.workintech.s18d4.controller;

import com.workintech.s18d4.dto.AccountResponse;
import com.workintech.s18d4.entity.Account;
import com.workintech.s18d4.entity.Customer;
import com.workintech.s18d4.service.AccountService;
import com.workintech.s18d4.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final CustomerService customerService;

    @Autowired
    public AccountController(AccountService accountService, CustomerService customerService) {
        this.accountService = accountService;
        this.customerService = customerService;
    }

    @GetMapping
    public List<Account> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{id}")
    public Account find(@PathVariable long id) {
        return accountService.find(id);
    }

    @PostMapping("/{customerId}")
    public AccountResponse save(@PathVariable long customerId, @RequestBody Account account) {
        Customer customer = customerService.find(customerId);
        if (customer != null) {
            customer.getAccounts().add(account);
            account.setCustomer(customer);
            Account savedAccount = accountService.save(account);
            return new AccountResponse(savedAccount.getId(), savedAccount.getAccountName(), savedAccount.getMoneyAmount(), savedAccount.getCustomer().getId());
        }
        throw new RuntimeException("Customer not found: " + customerId);
    }

    @PutMapping("/{customerId}")
    public AccountResponse update(@PathVariable long customerId, @RequestBody Account account) {
        Customer customer = customerService.find(customerId);
        Account foundAccount = accountService.find(account.getId());

        if (customer != null && foundAccount != null) {
            foundAccount.setAccountName(account.getAccountName());
            foundAccount.setMoneyAmount(account.getMoneyAmount());
            foundAccount.setCustomer(customer);

            Account savedAccount = accountService.save(foundAccount);
            return new AccountResponse(savedAccount.getId(), savedAccount.getAccountName(), savedAccount.getMoneyAmount(), savedAccount.getCustomer().getId());
        }
        throw new RuntimeException("Customer or Account not found");
    }

    @DeleteMapping("/{id}")
    public AccountResponse delete(@PathVariable long id) {
        Account account = accountService.find(id);

        if (account != null) {
            accountService.delete(id);

            return new AccountResponse(account.getId(), account.getAccountName(), account.getMoneyAmount(), account.getCustomer().getId());
        }

        return null;
    }
}