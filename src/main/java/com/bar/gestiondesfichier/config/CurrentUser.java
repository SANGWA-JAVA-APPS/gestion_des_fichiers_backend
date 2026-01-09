package com.bar.gestiondesfichier.config;

import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.repository.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class CurrentUser {

    private Account account;
    private Long accountId;
    private String role;
    private String username;

    void setAccount(Account account) {
        this.account = account;
        this.accountId = account.getId();
        this.username = account.getUsername();
        this.role = account.getAccountCategory().getName();
    }

    public Account getAccount() {
        return account;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isManager() {
        return "MANAGER".equalsIgnoreCase(role);
    }

    public boolean isUser() {
        return "USER".equalsIgnoreCase(role);
    }
}
