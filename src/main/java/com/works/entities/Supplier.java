package com.works.entities;

import com.works.entities.listener.BaseEntity;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class Supplier extends BaseEntity<String> { //Tedarikçi

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer supplier_id;

    @NotNull(message = "İsim null olamaz")
    @NotEmpty(message = "İsim boş olamaz")
    @Length(min = 2, max = 30, message = "İsim alanı en az 2, en fazla 30 karakter olabilir")
    private String supplier_name;

    @NotNull(message = "Mail Null olamaz")
    @NotEmpty(message = "Mail Boş olamaz")
    @Column(unique = true)
    private String supplier_mail;

    @NotNull(message = "Tel Null olamaz")
    @NotEmpty(message = "Tel Boş olamaz")
    private String supplier_tel;

    @Column(length = 10)
    private String supplier_statu;
}
