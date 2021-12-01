package com.jstamps.docai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jstamps.docai.chaincode.client.RegisterUserHyperledger;
import com.jstamps.docai.converter.MedWorkerConverter;
import com.jstamps.docai.dto.form.MedWorkerForm;
import com.jstamps.docai.dto.medInstitution.MedAdminDoctors;
import com.jstamps.docai.dto.user.MedWorkerDto;
import com.jstamps.docai.model.user.MedWorker;
import com.jstamps.docai.repository.MedWorkerRepository;
import com.jstamps.docai.security.service.UserDetailsServiceImpl;
import com.jstamps.docai.util.Constants;
import com.jstamps.docai.validator.AuthException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedAdminService {

    @Autowired
    private MedWorkerRepository medWorkerRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MedWorkerConverter medWorkerConverter;

    @Autowired
    private SymmetricCryptography symmetricCryptography;

    public MedAdminDoctors getAllMedInstitutionDoctors(){
        MedWorker medAdmin = (MedWorker) userDetailsService.getLoggedUser();

        List<MedWorkerDto> doctors = medWorkerRepository
                .findAllByMedInstitutionAndRole(medAdmin.getMedInstitution(), Constants.ROLE_DOCTOR)
                .stream().map(this::convert).collect(Collectors.toList());
        return new MedAdminDoctors(medAdmin.getMedInstitution().getName(), doctors);
    }

    public MedWorkerDto addNewMedInstitutionDoctor(MedWorkerForm medWorkerForm){
        medWorkerForm.setRole(Constants.ROLE_DOCTOR);
        medWorkerForm.setPassword(userPasswordEncoder.encode(medWorkerForm.getPassword()));

        MedWorker medAdmin = (MedWorker) userDetailsService.getLoggedUser();
        medWorkerForm.setMedInstitution(medAdmin.getMedInstitution().getId());

        MedWorker medWorker = convert(medWorkerForm);
        MedWorker savedMedWorker = medWorkerRepository.save(medWorker);
        try {
            String appUserIdentityId = savedMedWorker.getEmail();
            String org = savedMedWorker.getMedInstitution().getMembershipOrganizationId();
            String userIdentityId = symmetricCryptography.putInfoInDb(savedMedWorker.getId());
            RegisterUserHyperledger.enrollOrgAppUser(appUserIdentityId, org, userIdentityId);
        } catch (Exception e) {
            throw new AuthException("Error while signUp in hyperledger");
        }
        return convert(savedMedWorker);
    }

    private MedWorkerDto convert(MedWorker medWorker){
        return medWorkerConverter.convertToDto(medWorker);
    }

    private MedWorker convert(MedWorkerForm medWorkerForm){
        return medWorkerConverter.convertFromDto(medWorkerForm, false);
    }
}
