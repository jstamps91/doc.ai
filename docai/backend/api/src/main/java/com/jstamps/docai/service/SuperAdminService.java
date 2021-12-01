package com.jstamps.docai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jstamps.docai.chaincode.client.RegisterUserHyperledger;
import com.jstamps.docai.converter.AdminConverter;
import com.jstamps.docai.converter.MedInstitutionConverter;
import com.jstamps.docai.converter.MedWorkerConverter;
import com.jstamps.docai.converter.declaration.UserInterface;
import com.jstamps.docai.dto.form.MedWorkerForm;
import com.jstamps.docai.dto.medInstitution.MedInstitutionDto;
import com.jstamps.docai.dto.user.MedWorkerDto;
import com.jstamps.docai.dto.user.UserDto;
import com.jstamps.docai.model.medInstitution.MedInstitution;
import com.jstamps.docai.model.user.Admin;
import com.jstamps.docai.model.user.MedWorker;
import com.jstamps.docai.repository.AdminRepository;
import com.jstamps.docai.repository.MedInstitutionRepository;
import com.jstamps.docai.repository.MedWorkerRepository;
import com.jstamps.docai.util.Constants;
import com.jstamps.docai.validator.AuthException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuperAdminService implements UserInterface<Admin, UserDto, UserDto> {

    @Autowired
    private MedInstitutionRepository medInstitutionRepository;

    @Autowired
    private MedWorkerRepository medWorkerRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Autowired
    private MedInstitutionConverter medInstitutionConverter;

    @Autowired
    private MedWorkerConverter medWorkerConverter;

    @Autowired
    private AdminConverter adminConverter;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SymmetricCryptography symmetricCryptography;

    public List<MedInstitutionDto> getAll(){
        return medInstitutionRepository.findAll().
                stream().map(this::convert).collect(Collectors.toList());
    }

    public MedInstitutionDto addNewInstitution(MedInstitutionDto medInstitutionDto){
        MedInstitution medInstitution = convert(medInstitutionDto);
        return convert(medInstitutionRepository.save(medInstitution));
    }

    public List<MedWorkerDto> getAllMedInstitutionAdmins(){
        return medWorkerRepository.findAllByRole(Constants.ROLE_MED_ADMIN)
                .stream().map(this::convert).collect(Collectors.toList());
    }

    public MedWorkerDto addNewMedInstitutionAdmin(MedWorkerForm medWorkerDto){
        medWorkerDto.setRole(Constants.ROLE_MED_ADMIN);
        medWorkerDto.setPassword(userPasswordEncoder.encode(medWorkerDto.getPassword()));
        MedWorker medWorker = convert(medWorkerDto);
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

    private MedInstitutionDto convert(MedInstitution medInstitution){
        return medInstitutionConverter.convertToDto(medInstitution);
    }

    private MedInstitution convert(MedInstitutionDto medInstitutionDto){
        return medInstitutionConverter.convertFromDto(medInstitutionDto, false);
    }

    private MedWorkerDto convert(MedWorker medWorker){
        return medWorkerConverter.convertToDto(medWorker);
    }

    private MedWorker convert(MedWorkerForm medWorkerForm){
        return medWorkerConverter.convertFromDto(medWorkerForm, false);
    }

    @Override
    public UserDto editUser(UserDto object) {
        Admin admin = adminConverter.convertFromDto(object, true);
        return adminConverter.convertToDto(adminRepository.save(admin));
    }
}
