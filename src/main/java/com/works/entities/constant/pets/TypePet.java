package com.works.entities.constant.pets;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class TypePet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ty_id;

    private String ty_name;

}
