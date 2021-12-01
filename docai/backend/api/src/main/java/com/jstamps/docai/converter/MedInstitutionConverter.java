package com.jstamps.docai.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jstamps.docai.chaincode.Config;
import com.jstamps.docai.converter.declaration.ConverterInterface;
import com.jstamps.docai.dto.medInstitution.MedInstitutionDto;
import com.jstamps.docai.model.medInstitution.MedInstitution;
import com.jstamps.docai.repository.MedInstitutionRepository;
import com.jstamps.docai.util.StringUtil;

@Component
public class MedInstitutionConverter implements ConverterInterface<MedInstitution, MedInstitutionDto, MedInstitutionDto> {

    @Autowired
    private MedInstitutionRepository medInstitutionRepository;

    @Override
    public MedInstitutionDto convertToDto(MedInstitution medInstitution) {
        return new MedInstitutionDto(
                medInstitution.getId(),
                medInstitution.getMembershipOrganizationId(),
                medInstitution.getName(),
                medInstitution.getAddress()
        );
    }

    @Override
    public MedInstitution convertFromDto(MedInstitutionDto medInstitutionDto, boolean update) {
        if(update){
            return medInstitutionRepository.getOne(medInstitutionDto.getId());
        }
        String membershipOrganizationId = StringUtil.generateMembershipOrganizationId(Config.ORG_COUNT);
        return new MedInstitution(
                medInstitutionDto.getId(),
                medInstitutionDto.getName(),
                medInstitutionDto.getAddress(),
                membershipOrganizationId
        );
    }
}
