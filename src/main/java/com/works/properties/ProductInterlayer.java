package com.works.properties;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ProductInterlayer {

    private Integer product_id;

    @NotNull(message = "Ürün Adı Null Olamaz!")
    @NotEmpty(message = "Ürün Adı Boş Bırakılamaz!")
    private String product_name;

    @Min(value = 1, message = "En Az 1 Olabilir!")
    @NotNull(message = "product_unit not null!")
    private Integer product_unit;//birim

    @Min(value = 1, message = "En Az 1 Olabilir!")
    @NotNull(message = "category not null!")
    private Integer category;

    @Min(value = 1, message = "En Az 1 Olabilir!")
    @NotNull(message = "product_kdv not null!")
    private Integer product_kdv;//kdv

    @Min(value = 1, message = "En Az 1 Olabilir!")
    @NotNull(message = "product_alis not null!")
    private Integer product_alis;

    @NotNull(message = "product_satis not null!")
    @Min(value = 1, message = "En Az 1 Olabilir!")
    private Integer product_satis;

    @NotNull(message = "product_stokMiktari not null!")
    @Min(value = 1, message = "En Az 1 Olabilir!")
    private Integer product_stokMiktari;

    private Boolean product_statu;
}
