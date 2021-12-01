package com.jstamps.docai.dto.contract;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalTrialAccessSendRequestForm {

    @NotBlank
    private String clinicalTrial;
    private String sender;
    private Date time;
    private String patient;

}
