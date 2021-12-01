package com.jstamps.docai.dto.medInstitution;

import lombok.*;
import com.jstamps.docai.dto.user.MedWorkerDto;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MedAdminDoctors {

    private String institution;
    private List<MedWorkerDto> doctors;
}
