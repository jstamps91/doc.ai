package com.jstamps.docai.dto.form;

import lombok.*;
import com.jstamps.docai.enums.AccessType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditClinicalTrialForm {
    @NotBlank
    private String id;

    @NotNull
    private AccessType accessType;
}

