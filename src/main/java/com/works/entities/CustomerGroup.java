package com.works.entities;

import com.works.entities.listener.BaseEntity;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
public class CustomerGroup extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cu_gr_id;

    @Column(length = 30, unique = true)
    @NotEmpty(message = "Grup Adı boş olamaz!")
    @NotNull(message = "Grup Adı null olamaz!")
    @Length(min = 3, max = 30, message = "Grup adı an az 3, en fazla 30 karakter olabilir")
    private String cu_gr_name;
}
