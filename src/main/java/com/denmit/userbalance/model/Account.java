package com.denmit.userbalance.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Table(name = "ACCOUNT")
public class Account implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "accountsIdSeq", sequenceName = "account_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountsIdSeq")
    @Column(name = "ID", nullable = false, unique = true, updatable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "USER_ID", unique = true)
    @JsonIgnore
    private User user;

    @Column(name = "BALANCE", precision = 19, scale = 2)
    private BigDecimal balance;
}
