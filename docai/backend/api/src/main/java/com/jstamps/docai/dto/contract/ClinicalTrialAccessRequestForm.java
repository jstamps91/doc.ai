package com.jstamps.docai.dto.contract;

import lombok.*;
import com.jstamps.docai.enums.AccessType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalTrialAccessRequestForm {

    @NotBlank
    private String id;

    @NotNull
    private AccessType accessDecision;

    private boolean anonymity;
    private Date from;
    private Date until;
}
