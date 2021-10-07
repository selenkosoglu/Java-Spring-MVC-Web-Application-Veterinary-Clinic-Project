package com.works.properties;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PayInInterlayer {

    private Integer cu_id;

    @NotEmpty(message = "pin_detail not empty!")
    @NotNull(message = "pin_detail not null!")
    private String pin_detail;

    @Min(value = 1, message = "Ödeme yöntemi seçiniz")
    @Max(value = 3, message = "3 çeşit ödeme yöntemi olabilir")
    @NotNull(message = "pin_paymentType not null!")
    private Integer pin_paymentType;

    @Min(value = 1, message = "En az 0 olabilir")
    @NotNull(message = "pin_price not null!")
    private Integer pin_price;


}
