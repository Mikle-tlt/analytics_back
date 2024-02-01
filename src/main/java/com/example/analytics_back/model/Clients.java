package com.example.analytics_back.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Clients {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
    private String name;
    private String contact;
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Users owner;

    public Clients(String name, String contact, Users owner) throws ParseException {
        this.name = name;
        this.contact = contact;
        this.owner = owner;
    }
}
