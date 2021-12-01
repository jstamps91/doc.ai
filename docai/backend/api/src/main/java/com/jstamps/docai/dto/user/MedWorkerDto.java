package com.jstamps.docai.dto.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import com.jstamps.docai.dto.medInstitution.MedInstitutionDto;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MedWorkerDto extends UserDto {

    private String occupation;
    private MedInstitutionDto medInstitution;
}
