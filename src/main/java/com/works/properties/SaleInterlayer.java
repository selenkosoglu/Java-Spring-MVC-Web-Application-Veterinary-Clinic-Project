package com.works.properties;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SaleInterlayer {

    private Integer cid;

    @Min(value = 1, message = "En az 1 olabilir")
    @NotNull(message = "pid not null!")
    private Integer pid;

    @Min(value = 1, message = "En az 1 olabilir")
    @NotNull(message = "pAmount not null!")
    private Integer pAmount;

    @Max(value = 3, message = "3 çeşit ödeme yöntemi olabilir")
    @Min(value = 1, message = "En az 1 olabilir")
    @NotNull(message = "pPaymentType not null!")
    private Integer pPaymentType;

    @NotEmpty(message = "pNote not empty!")
    @NotNull(message = "pNote not null!")
    private String pNote;
}
