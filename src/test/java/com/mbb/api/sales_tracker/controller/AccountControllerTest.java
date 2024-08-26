package com.mbb.api.sales_tracker.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.mbb.api.sales_tracker.model.Account;
import com.mbb.api.sales_tracker.service.AccountService;

@WebMvcTest(AccountController.class)
@ActiveProfiles("unit")
public class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    private List<Account> accounts;

    @BeforeEach
    void setup() {
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
    @WithMockUser(username = "user", roles = "USER")
    void testGetAllAccounts() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> page = new PageImpl<>(accounts, pageable, 2);

        when(accountService.getAccounts(0, 10)).thenReturn(page);

        mockMvc.perform(get("/account").contentType(MediaType.APPLICATION_JSON).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Account 1"))
            .andExpect(jsonPath("$.content[1].name").value("Account 2"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testGetAccountById() throws Exception {
        Account account = new Account();
        account.setId(1L);
        account.setName("Account 1");

        when(accountService.getAccountById(1L)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/account/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Account 1"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testGetAccountByIdNotFound() throws Exception {
        when(accountService.getAccountById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/account/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testCreateAccount() throws Exception {
        Account account = new Account();
        account.setId(1L);
        account.setName("Account 1");

        when(accountService.createAccount(Mockito.any(Account.class))).thenReturn(account);

        String json = "{ \"name\": \"Account 1\", \"primaryUserId\": 1, \"createdBy\": 1, \"updatedBy\": 1 }";

        mockMvc.perform(post("/account").contentType(MediaType.APPLICATION_JSON).content(json).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Account 1"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testCreateAccountNotFound() throws Exception {
        when(accountService.createAccount(Mockito.any(Account.class))).thenThrow(new RuntimeException());

        String json = "{ \"name\": \"Account 1\", \"primaryUserId\": 1, \"createdBy\": 1, \"updatedBy\": 1 }";

        mockMvc.perform(post("/account").contentType(MediaType.APPLICATION_JSON).content(json).with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testUpdateAccount() throws Exception {
        Account account = new Account();
        account.setId(1L);
        account.setName("Account 1");

        when(accountService.updateAccount(Mockito.eq(1L), Mockito.any(Account.class))).thenReturn(account);

        String json = "{ \"name\": \"Updated Account\", \"primaryUserId\": 1, \"createdBy\": 1, \"updatedBy\": 1 }";

        mockMvc.perform(put("/account/1").contentType(MediaType.APPLICATION_JSON).content(json).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Account 1"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testUpdateAccountNotFound() throws Exception {
        when(accountService.updateAccount(Mockito.eq(1L), Mockito.any(Account.class))).thenThrow(new RuntimeException());

        String json = "{ \"name\": \"Account 1\", \"primaryUserId\": 1, \"createdBy\": 1, \"updatedBy\": 1 }";

        mockMvc.perform(put("/account/1").contentType(MediaType.APPLICATION_JSON).content(json).with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testDeleteAccount() throws Exception {
        when(accountService.deleteAccountById(1L)).thenReturn(true);

        mockMvc.perform(delete("/account/1").with(csrf()))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testDeleteAccountNotFound() throws Exception {
        when(accountService.deleteAccountById(1L)).thenReturn(false);

        mockMvc.perform(delete("/account/1").with(csrf()))
            .andExpect(status().isNotFound());
    }

}
