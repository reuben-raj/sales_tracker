package com.mbb.api.sales_tracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.mbb.api.sales_tracker.model.Account;
import com.mbb.api.sales_tracker.repository.AccountRepository;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private List<Account> accounts;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        Account account1 = new Account();
        account1.setId(1L);
        account1.setName("Account 1");
        account1.setCreatedAt(LocalDateTime.now());
        account1.setUpdatedAt(LocalDateTime.now());

        Account account2 = new Account();
        account2.setId(2L);
        account2.setName("Account 2");
        account2.setCreatedAt(LocalDateTime.now());
        account2.setUpdatedAt(LocalDateTime.now());

        accounts = List.of(account1, account2);
    }
    
    @Test
    void testGetAllAccounts() {
        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> result = accountService.getAllAccounts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Account 1", result.get(0).getName());
    }

    @Test
    void testGetAccountById() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(accounts.get(0)));

        Optional<Account> result = accountService.getAccountById(1L);

        assertTrue(result.isPresent());
        assertEquals("Account 1", result.get().getName());
    }

    @Test
    void testCreateAccount() {
        when(accountRepository.save(any(Account.class))).thenReturn(accounts.get(0));

        Account result = accountService.createAccount(accounts.get(0));

        assertNotNull(result);
        assertEquals("Account 1", result.getName());
    }

    @Test
    void testUpdateAccount() {
        Account existingAccount = new Account();
        existingAccount.setId(1L);
        existingAccount.setName("Existing Account");

        Account updatedDetails = new Account();
        updatedDetails.setName("Updated Account");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

        Account result = accountService.updateAccount(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Account", result.getName());
    }

    @Test
    void testUpdateAccountNotFound() {
        Account updatedDetails = new Account();
        updatedDetails.setName("Updated Account");

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.updateAccount(1L, updatedDetails);
        });

        assertEquals("Account not found with id 1", exception.getMessage());
    }

    @Test
    void testDeleteAccountById() {
        when(accountRepository.existsById(1L)).thenReturn(true);

        boolean result = accountService.deleteAccountById(1L);

        assertTrue(result);
        verify(accountRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAccountByIdNotFound() {
        when(accountRepository.existsById(1L)).thenReturn(false);

        boolean result = accountService.deleteAccountById(1L);

        assertFalse(result);
        verify(accountRepository, never()).deleteById(1L);
    }

    @Test
    void testGetAccounts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> accountPage = new PageImpl<>(List.of(accounts.get(0)), pageable, 1);

        when(accountRepository.findAll(pageable)).thenReturn(accountPage);

        Page<Account> result = accountService.getAccounts(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Account 1", result.getContent().get(0).getName());
    }
}
