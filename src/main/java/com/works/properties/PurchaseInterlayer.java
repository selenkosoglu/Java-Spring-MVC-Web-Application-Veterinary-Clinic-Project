package com.works.properties;

import lombok.Data;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PurchaseInterlayer {

    private Integer supplier_id;

    @Min(value = 1, message = "En az 1 olabilir")
    @NotNull(message = "product_id not null!")
    private Integer product_id;

    @Min(value = 1, message = "En az 1 olabilir")
    @NotNull(message = "purchase_number not null!")
    private Integer purchase_number;

    @NotEmpty(message = "pNote not empty!")
    @NotNull(message = "pNote not null!")
    private String pNote;

    @Min(value = 1, message = "En az 1 olabilir")
    @NotNull(message = "purchase_type not null!")
    private Integer purchase_type;
}