package com.works.properties;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordInterlayer {

    @NotEmpty(message = "newpassword not empty!")
    @NotNull(message = "newpassword not null!")
    @Length(min = 4, max = 10, message = "newpassword en az 4 en fazla 10 karakter olabilir.")
    private String newpassword;

    @NotEmpty(message = "newpasswordr not empty!")
    @NotNull(message = "newpasswordr not null!")
    @Length(min = 4, max = 10, message = "newpasswordr en az 4 en fazla 10 karakter olabilir.")
    private String newpasswordr;

}
