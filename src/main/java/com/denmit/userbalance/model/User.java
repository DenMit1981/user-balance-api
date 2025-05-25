package com.denmit.userbalance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Table(name = "USERS")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "usersIdSeq", sequenceName = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersIdSeq")
    @Column(name = "ID", nullable = false, unique = true, updatable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DATE_OF_BIRTH")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;

    @Column(name = "PASSWORD", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<EmailData> emails;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<PhoneData> phones;

    public void setAccount(Account account) {
        this.account = account;
        if (account != null) {
            account.setUser(this);
        }
    }

    public void setEmails(Set<EmailData> emails) {
        this.emails = emails;
        if (emails != null) {
            emails.forEach(email -> email.setUser(this));
        }
    }

    public void setPhones(Set<PhoneData> phones) {
        this.phones = phones;
        if (phones != null) {
            phones.forEach(phone -> phone.setUser(this));
        }
    }
}
