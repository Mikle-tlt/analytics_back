package com.example.analytics_back.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@Getter
public enum Role implements GrantedAuthority {
    ADMIN("Админ"),
    MANAGER("Менеджер"),
    ;

    private final String name;

    @Override
    public String getAuthority() {
        return name();
    }
}

