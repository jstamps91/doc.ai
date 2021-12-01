package com.jstamps.docai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jstamps.docai.converter.CommonUserConverter;
import com.jstamps.docai.converter.declaration.UserInterface;
import com.jstamps.docai.dto.form.EditClinicalTrialForm;
import com.jstamps.docai.dto.medInstitution.ClinicalTrialDto;
import com.jstamps.docai.dto.user.CommonUserDto;
import com.jstamps.docai.model.user.CommonUser;
import com.jstamps.docai.model.user.User;
import com.jstamps.docai.repository.CommonUserRepository;
import com.jstamps.docai.security.service.UserDetailsServiceImpl;

import java.util.List;

@Service
public class CommonUserService implements UserInterface<CommonUser, CommonUserDto, CommonUserDto> {

    @Autowired
    private CommonUserConverter commonUserConverter;

    @Autowired
    private CommonUserRepository commonUserRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private FhirService fhirService;

    @Autowired
    private HyperledgerService hyperledgerService;

    @Autowired
    private SymmetricCryptography symmetricCryptography;

    @Override
    public CommonUserDto editUser(CommonUserDto object) {
        CommonUser commonUser = commonUserConverter.convertFromDto(object, true);
        return commonUserConverter.convertToDto(commonUserRepository.save(commonUser));
    }

    public List<ClinicalTrialDto> getUserClinicalTrials(){
        User user = (User) userDetailsService.getLoggedUser();
        String userId = symmetricCryptography.putInfoInDb(user.getId());
        return fhirService.searchImagingStudy(userId, false);
    }

    public List<ClinicalTrialDto> getUserClinicalTrialsDefineAccess(){
        User user = (User) userDetailsService.getLoggedUser();
        String userId = symmetricCryptography.putInfoInDb(user.getId());
        return fhirService.searchImagingStudy(userId, true);
    }

    public ClinicalTrialDto updateClinicalTrial(EditClinicalTrialForm editClinicalTrialForm) throws Exception {
        User user = (User) userDetailsService.getLoggedUser();
        ClinicalTrialDto clinicalTrialBefore = fhirService.getImagingStudy(editClinicalTrialForm.getId());
        String userId = symmetricCryptography.putInfoInDb(user.getId());
        ClinicalTrialDto clinicalTrial = fhirService.updateClinicalTrial(userId, editClinicalTrialForm);
        hyperledgerService.defineClinicalTrialAccess(user, clinicalTrial, clinicalTrialBefore);
        return clinicalTrial;
    }
}
