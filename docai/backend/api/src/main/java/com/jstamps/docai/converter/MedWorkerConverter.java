package com.jstamps.docai.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jstamps.docai.converter.declaration.ConverterInterface;
import com.jstamps.docai.dto.medInstitution.MedInstitutionDto;
import com.jstamps.docai.dto.form.MedWorkerForm;
import com.jstamps.docai.dto.user.MedWorkerDto;
import com.jstamps.docai.model.medInstitution.MedInstitution;
import com.jstamps.docai.model.user.MedWorker;
import com.jstamps.docai.repository.MedInstitutionRepository;
import com.jstamps.docai.repository.MedWorkerRepository;

import java.util.Date;

@Component
public class MedWorkerConverter implements ConverterInterface<MedWorker, MedWorkerDto, MedWorkerForm> {

    @Autowired
    private MedInstitutionRepository medInstitutionRepository;

    @Autowired
    private MedWorkerRepository medWorkerRepository;

    @Override
    public MedWorkerDto convertToDto(MedWorker medWorker) {
        MedInstitution medInstitution = medWorker.getMedInstitution();
        return MedWorkerDto
                .builder()
                .id(medWorker.getId())
                .email(medWorker.getEmail())
                .firstName(medWorker.getFirstName())
                .lastName(medWorker.getLastName())
                .occupation(medWorker.getOccupation())
                .role(medWorker.getRole())
                .enabled(medWorker.isEnabled())
                .activeSince(medWorker.getActiveSince())
                .medInstitution( new MedInstitutionDto(
                                medInstitution.getId(),
                                medInstitution.getMembershipOrganizationId(),
                                medInstitution.getName(),
                                medInstitution.getAddress()
                            ))
                .build();
    }

    @Override
    public MedWorker convertFromDto(MedWorkerForm medWorkerForm, boolean update) {
        if(update){
            MedWorker medWorker = medWorkerRepository.findByUsername(medWorkerForm.getEmail());
            medWorker.setFirstName(medWorkerForm.getFirstName());
            medWorker.setLastName(medWorkerForm.getLastName());
            medWorker.setOccupation(medWorkerForm.getOccupation());
            return medWorker;
        } else {
            return MedWorker
                    .builder()
                    .email(medWorkerForm.getEmail())
                    .username(medWorkerForm.getEmail())
                    .password(medWorkerForm.getPassword())
                    .firstName(medWorkerForm.getFirstName())
                    .lastName(medWorkerForm.getLastName())
                    .occupation(medWorkerForm.getOccupation())
                    .role(medWorkerForm.getRole())
                    .enabled(true)
                    .activeSince(new Date())
                    .medInstitution(
                            medInstitutionRepository.getOne(medWorkerForm.getMedInstitution())
                    )
                    .build();
        }
    }
}
