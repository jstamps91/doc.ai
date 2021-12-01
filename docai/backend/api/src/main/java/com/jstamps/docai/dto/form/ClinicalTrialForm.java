package com.jstamps.docai.dto.form;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import com.jstamps.docai.enums.ClinicalTrialType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalTrialForm {

    @NotNull
    private MultipartFile file;

    @NotBlank
    private String introduction;

    @NotBlank
    private String conclusion;

    @NotNull
    private ClinicalTrialType clinicalTrialType;

    private String relevantParameters;

    @NotBlank
    private String patient;

    private String doctor;
    private String doctorName;

}

