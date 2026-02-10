package com.epam.rd.autocode.spring.project.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client extends User {

    public Client() {
        super();
    }

    public Client(Long id, String email, String password, String name, BigDecimal balance) {
        super(id, email, password, name);
        this.balance = balance;
    }

    @Column(name = "BALANCE", nullable = false)
    private BigDecimal balance;

    @Column(name = "is_blocked")
    private Boolean isBlocked;

    public Boolean isBlocked() {
        return isBlocked;
    }
}
