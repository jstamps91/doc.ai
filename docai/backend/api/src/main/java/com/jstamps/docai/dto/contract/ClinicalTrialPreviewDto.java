package com.jstamps.docai.dto.contract;

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
public class ClinicalTrialPreviewDto {

    private String clinicalTrial;

    private Date time;
    private ClinicalTrialType clinicalTrialType;
    private AccessType accessType;

    private String institution;

    private String patientId;

    //relevant parameters
    //doctorId
}
