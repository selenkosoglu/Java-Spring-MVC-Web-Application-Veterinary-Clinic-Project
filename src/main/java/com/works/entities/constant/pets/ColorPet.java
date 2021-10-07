package com.works.entities.constant.pets;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class ColorPet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer color_id;
    private String color_name;

}
