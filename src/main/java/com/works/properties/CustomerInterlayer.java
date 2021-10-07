package com.works.properties;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CustomerInterlayer {

    @NotNull(message = "cu_name NULL OLAMAZ")
    @NotEmpty(message = "cu_name EMPTY OLAMAZ")
    private String cu_name;

    @NotNull(message = "cu_surname NULL OLAMAZ")
    @NotEmpty(message = "cu_surname EMPTY OLAMAZ")
    private String cu_surname;

    @NotNull(message = "cu_tel1 NULL OLAMAZ")
    @NotEmpty(message = "cu_tel1 EMPTY OLAMAZ")
    private String cu_tel1;

    private String cu_tel2;

    @NotNull(message = "cu_mail NULL OLAMAZ")
    @NotEmpty(message = "cu_mail EMPTY OLAMAZ")
    private String cu_mail;

    private String cu_taxname;

    private String cu_note;

    @NotNull(message = "cu_tcnumber NULL OLAMAZ")
    @NotEmpty(message = "cu_tcnumber EMPTY OLAMAZ")
    private String cu_tcnumber;

    @NotNull(message = "cu_cities NULL OLAMAZ")
    @NotEmpty(message = "cu_cities EMPTY OLAMAZ")
    private String cu_cities;

    @NotNull(message = "cu_districts NULL OLAMAZ")
    @NotEmpty(message = "cu_districts EMPTY OLAMAZ")
    private String cu_districts;

    @NotNull(message = "cu_group NULL OLAMAZ")
    @NotEmpty(message = "cu_group EMPTY OLAMAZ")
    private String cu_group;

    @NotNull(message = "cu_address NULL OLAMAZ")
    @NotEmpty(message = "cu_address EMPTY OLAMAZ")
    private String cu_address;

    @NotNull(message = "cu_rateOfDiscount NULL OLAMAZ")
    @NotEmpty(message = "cu_rateOfDiscount EMPTY OLAMAZ")
    private String cu_rateOfDiscount;

    @NotNull(message = "cu_smsNotice NULL OLAMAZ")
    @NotEmpty(message = "cu_smsNotice EMPTY OLAMAZ")
    private String cu_smsNotice;

    @NotNull(message = "cu_mailNotice NULL OLAMAZ")
    @NotEmpty(message = "cu_mailNotice EMPTY OLAMAZ")
    private String cu_mailNotice;

}
