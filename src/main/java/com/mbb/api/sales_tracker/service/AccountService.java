package com.mbb.api.sales_tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mbb.api.sales_tracker.model.Account;
import com.mbb.api.sales_tracker.repository.AccountRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Transactional
    public Account createAccount(Account accountDetails) {
        return accountRepository.save(accountDetails);
    }

    @Transactional
    public Account updateAccount(Long id, Account accountDetails) {
        return accountRepository.findById(id)
        .map(account -> {
            account.setName(accountDetails.getName());
            account.setPrimaryUserId(accountDetails.getPrimaryUserId());
            account.setCreatedBy(accountDetails.getCreatedBy());
            account.setUpdatedBy(accountDetails.getUpdatedBy());
            return accountRepository.save(account);
        })
        .orElseThrow(() -> new RuntimeException("Account not found with id "+id));
    }

    public boolean deleteAccountById(Long id) {
        if(accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Page<Account> getAccounts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return accountRepository.findAll(pageable);
    }

}
