package com.jstamps.docai.dto.medInstitution;

import lombok.*;
import com.jstamps.docai.enums.AccessType;
import com.jstamps.docai.enums.ClinicalTrialType;

import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalTrialDto {

    private String id;

    private Date time;
    private String introduction;
    private String relevantParameters;
    private String conclusion;
    private ClinicalTrialType clinicalTrialType;

    private String patient;
    private String doctor;
    private String doctorId;
    private String institution;

    private AccessType accessType;

    private String resourcePath;
}
