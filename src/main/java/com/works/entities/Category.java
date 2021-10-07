package com.works.entities;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer category_id;

    @Column(length = 20, unique = true)
    @NotEmpty(message = "Grup Adı boş olamaz!")
    @NotNull(message = "Grup Adı null olamaz!")
    private String category_title;

    @Column(length = 40)
    @NotEmpty(message = "Grup Adı boş olamaz!")
    @NotNull(message = "Grup Adı null olamaz!")
    private String category_detail;

}
