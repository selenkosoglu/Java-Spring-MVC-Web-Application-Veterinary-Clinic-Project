package com.works.properties;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class PetListInterlayer {

    @NotNull(message = "name NULL OLAMAZ")
    @NotEmpty(message = "name EMPTY OLAMAZ")
    private String name;

    @NotNull(message = "chipNumber NULL OLAMAZ")
    @NotEmpty(message = "chipNumber EMPTY OLAMAZ")
    private String chipNumber;

    @NotNull(message = "earTag NULL OLAMAZ")
    @NotEmpty(message = "earTag EMPTY OLAMAZ")
    private String earTag;

    @NotNull(message = "bornDate NULL OLAMAZ")
    private Date bornDate;

    @NotNull(message = "type NULL OLAMAZ")
    @NotEmpty(message = "type EMPTY OLAMAZ")
    private String type;

    @NotNull(message = "neutering NULL OLAMAZ")
    @NotEmpty(message = "neutering EMPTY OLAMAZ")
    private String neutering;

    @NotNull(message = "breed NULL OLAMAZ")
    @NotEmpty(message = "breed EMPTY OLAMAZ")
    private String breed;

    @NotNull(message = "color NULL OLAMAZ")
    @NotEmpty(message = "color EMPTY OLAMAZ")
    private String color;

    @NotNull(message = "gender NULL OLAMAZ")
    @NotEmpty(message = "gender EMPTY OLAMAZ")
    private String gender;
}