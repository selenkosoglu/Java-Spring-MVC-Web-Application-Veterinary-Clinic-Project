package com.works.entities;

import com.works.entities.listener.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class PaymentOut extends BaseEntity<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pout_id;

    @NotEmpty(message = "Detay kısmı boş olamaz!")
    @NotNull(message = "Detay kısmı null olamaz!")
    private String pout_detail;

    @Min(value = 1, message = "En az 1 olabilir")
    private Integer pout_price;

    private Integer pout_operationType;

    @Min(value = 1, message = "Ödeme yöntemi seçiniz")
    @Max(value = 3, message = "3 çeşit ödeme yöntemi olabilir")
    private Integer pout_paymentType;

}
