package com.study.study.study.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class StudyForm {

    @NotBlank
    @Length
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String path;

    @NotBlank
    @Length
    private String shortDescription;

    @NotBlank
    private String fullDescription;
}