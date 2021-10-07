package com.works.entities;

import com.works.entities.listener.BaseEntity;

import com.works.entities.security.User;
import lombok.Data;

import javax.persistence.*;


@Data
@Entity
public class Lab extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lab_id;

    private Integer lab_type;//lab türü
    private String lab_file;

    private String lab_detail;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "cu_id")
    Customer customer;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "us_id")
    User user;

}