package com.jstamps.docai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jstamps.docai.converter.MedWorkerConverter;
import com.jstamps.docai.converter.declaration.UserInterface;
import com.jstamps.docai.dto.form.ClinicalTrialForm;
import com.jstamps.docai.dto.form.MedWorkerForm;
import com.jstamps.docai.dto.medInstitution.ClinicalTrialDto;
import com.jstamps.docai.dto.user.CommonUserDto;
import com.jstamps.docai.dto.user.MedWorkerDto;
import com.jstamps.docai.model.user.CommonUser;
import com.jstamps.docai.model.user.MedWorker;
import com.jstamps.docai.model.user.User;
import com.jstamps.docai.repository.CommonUserRepository;
import com.jstamps.docai.repository.MedWorkerRepository;
import com.jstamps.docai.security.service.UserDetailsServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService implements UserInterface<MedWorker, MedWorkerDto, MedWorkerForm> {

    @Autowired
    private MedWorkerRepository medWorkerRepository;

    @Autowired
    private CommonUserRepository commonUserRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MedWorkerConverter medWorkerConverter;

    @Autowired
    private FhirService fhirService;

    @Autowired
    private HyperledgerService hyperledgerService;

    @Autowired
    private SymmetricCryptography symmetricCryptography;

    @Override
    public MedWorkerDto editUser(MedWorkerForm medWorkerDto) {
        MedWorker medWorker = convert(medWorkerDto, true);
        return convert(medWorkerRepository.save(medWorker));
    }

    public ClinicalTrialDto addClinicalTrial(ClinicalTrialForm clinicalTrialForm) throws Exception {
        User user = userDetailsService.getLoggedUser();
        String doctor = user.getId();
        String doctorName = user.getFirstName() + " " + user.getLastName();
        clinicalTrialForm.setDoctor(symmetricCryptography.putInfoInDb(doctor));
        clinicalTrialForm.setDoctorName(doctorName);
        clinicalTrialForm.setPatient(symmetricCryptography.putInfoInDb(clinicalTrialForm.getPatient()));

        String contentType = clinicalTrialForm.getFile().getContentType();
        byte[] fileContent = clinicalTrialForm.getFile().getBytes();
        Date clinicalTrialTime = new Date();
        ClinicalTrialDto clinicalTrialDto = fhirService.addClinicalTrial(clinicalTrialForm, contentType, fileContent, clinicalTrialTime);
        hyperledgerService.addClinicalTrial(user, clinicalTrialDto);
        return clinicalTrialDto;
    }

    public List<CommonUserDto> getPatients(){
        return commonUserRepository.findAll().stream().map(this::convert).collect(Collectors.toList());
    }

    private MedWorker convert(MedWorkerForm medWorkerForm, boolean update){
        return medWorkerConverter.convertFromDto(medWorkerForm, update);
    }

    private MedWorkerDto convert(MedWorker medWorker){
        return medWorkerConverter.convertToDto(medWorker);
    }

    private CommonUserDto convert(CommonUser commonUser){
        return CommonUserDto
                .builder()
                .id(commonUser.getId())
                .firstName(commonUser.getFirstName())
                .lastName(commonUser.getLastName())
                .birthday(commonUser.getBirthday())
                .build();
    }
}
