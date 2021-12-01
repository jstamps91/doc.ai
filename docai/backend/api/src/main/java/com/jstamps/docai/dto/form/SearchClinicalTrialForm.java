package com.jstamps.docai.dto.form;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchClinicalTrialForm {

    private String clinicalTrialType;
    private boolean relevantParameters;
    private String institutions;
    private Date from;
    private Date until;
    private String orderBy;
}

