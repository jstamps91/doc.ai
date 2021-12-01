package com.jstamps.docai.service;

import org.hl7.fhir.r4.model.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jstamps.docai.chaincode.dto.ClinicalTrialOfflineDto;
import com.jstamps.docai.converter.AdminConverter;
import com.jstamps.docai.converter.CommonUserConverter;
import com.jstamps.docai.converter.MedInstitutionConverter;
import com.jstamps.docai.converter.MedWorkerConverter;
import com.jstamps.docai.dto.contract.ClinicalTrialAccessRequestDto;
import com.jstamps.docai.dto.contract.ClinicalTrialAccessRequestForm;
import com.jstamps.docai.dto.contract.ClinicalTrialAccessSendRequestForm;
import com.jstamps.docai.dto.contract.ClinicalTrialPreviewDto;
import com.jstamps.docai.dto.form.SearchClinicalTrialForm;
import com.jstamps.docai.dto.medInstitution.ClinicalTrialDto;
import com.jstamps.docai.dto.medInstitution.MedInstitutionDto;
import com.jstamps.docai.dto.user.UserDto;
import com.jstamps.docai.model.medInstitution.MedInstitution;
import com.jstamps.docai.model.user.Admin;
import com.jstamps.docai.model.user.CommonUser;
import com.jstamps.docai.model.user.MedWorker;
import com.jstamps.docai.model.user.User;
import com.jstamps.docai.repository.AdminRepository;
import com.jstamps.docai.repository.CommonUserRepository;
import com.jstamps.docai.repository.MedInstitutionRepository;
import com.jstamps.docai.repository.MedWorkerRepository;
import com.jstamps.docai.security.service.UserDetailsServiceImpl;
import com.jstamps.docai.util.Constants;
import com.jstamps.docai.util.PdfExporter;
import com.jstamps.docai.util.StringUtil;
import com.jstamps.docai.validator.AuthException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private MedInstitutionRepository medInstitutionRepository;

    @Autowired
    private MedWorkerRepository medWorkerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CommonUserRepository commonUserRepository;

    @Autowired
    private MedWorkerConverter medWorkerConverter;

    @Autowired
    private AdminConverter adminConverter;

    @Autowired
    private CommonUserConverter commonUserConverter;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MedInstitutionConverter medInstitutionConverter;

    @Autowired
    private HyperledgerService hyperledgerService;

    @Autowired
    private FhirService fhirService;

    @Autowired
    private SymmetricCryptography symmetricCryptography;

    public UserDto getCurrentUser(){
        User user = (User) userDetailsService.getLoggedUser();
        UserDto userDto = convert(user);
        return userDto;
    }

    public String resetPassword(String newPassword){
        User user = (User) userDetailsService.getLoggedUser();

        if(userPasswordEncoder.matches(newPassword, user.getPassword())){
            throw new AuthException("Password does not change since new value is equal to current");
        }

        user.setPassword(userPasswordEncoder.encode(newPassword));
        switch (user.getRole()){
            case Constants.ROLE_SUPER_ADMIN:
                adminRepository.save((Admin)user);
                break;
            case Constants.ROLE_MED_ADMIN:
            case Constants.ROLE_DOCTOR:
                medWorkerRepository.save((MedWorker)user);
                break;
            case Constants.ROLE_COMMON_USER:
            default:
                commonUserRepository.save((CommonUser)user);
        }

        return "Success";
    }

    private UserDto convert(User user){
        switch (user.getRole()){
            case Constants.ROLE_SUPER_ADMIN:
                return adminConverter.convertToDto((Admin)user);
            case Constants.ROLE_MED_ADMIN:
            case Constants.ROLE_DOCTOR:
                return medWorkerConverter.convertToDto((MedWorker) user);
            case Constants.ROLE_COMMON_USER:
            default:
                return commonUserConverter.convertToDto((CommonUser)user);
        }
    }

    public List<ClinicalTrialAccessRequestDto> getClinicalTrialAccessRequests(String requestType) throws Exception {
        User user = (User) userDetailsService.getLoggedUser();
        List<ClinicalTrialAccessRequestDto> clinicalTrialAccessRequestDtos = hyperledgerService.getClinicalTrialAccessRequests(user, requestType);

        return clinicalTrialAccessRequestDtos;
    }

    public ClinicalTrialAccessRequestDto trialAccessRequestDecision(ClinicalTrialAccessRequestForm clinicalTrialAccessRequestForm) throws Exception {
        User user = (User) userDetailsService.getLoggedUser();
        hyperledgerService.trialAccessRequestDecision(user, clinicalTrialAccessRequestForm);
        return new ClinicalTrialAccessRequestDto();
    }

    public List<MedInstitutionDto> getMedInstitutionDatasource(){
        return medInstitutionRepository.findAll().
                stream().map(this::convert).collect(Collectors.toList());
    }

    public List<ClinicalTrialPreviewDto> getClinicalTrialsPreview(
            SearchClinicalTrialForm searchClinicalTrialForm,
            String page,
            String perPage
    ) throws Exception {
        User user = (User) userDetailsService.getLoggedUser();
        List<ClinicalTrialPreviewDto> clinicalTrialPreviewDtos = hyperledgerService.getClinicalTrialsPreview(searchClinicalTrialForm, page, perPage, user);
        return clinicalTrialPreviewDtos;
    }

    public ClinicalTrialAccessSendRequestForm sendAccessRequest(ClinicalTrialAccessSendRequestForm clinicalTrialAccessSendRequestForm) throws Exception {
        User user = (User) userDetailsService.getLoggedUser();
        clinicalTrialAccessSendRequestForm.setSender(user.getId());
        clinicalTrialAccessSendRequestForm.setTime(new Date());
        hyperledgerService.sendAccessRequest(user, clinicalTrialAccessSendRequestForm);
        return clinicalTrialAccessSendRequestForm;
    }

    public ClinicalTrialDto getClinicalTrial(String clinicalTrialId, String accessUserRole) throws Exception {
        User user = userDetailsService.getLoggedUser();
        String currentDate = StringUtil.parseDate(new Date());
        ClinicalTrialOfflineDto clinicalTrialOfflineChaincodeDto = hyperledgerService.accessToClinicalTrial(user, clinicalTrialId, currentDate, accessUserRole);
        ClinicalTrialDto offlineClinicalTrial = fhirService.getImagingStudy(clinicalTrialOfflineChaincodeDto.getOfflineDataUrl());

        if(hyperledgerService.areDataValid(offlineClinicalTrial.toString(), clinicalTrialOfflineChaincodeDto.getHashData())){
            if(clinicalTrialOfflineChaincodeDto.isAnonymity()){
                //anonymize data
                offlineClinicalTrial = anonymizeData(offlineClinicalTrial);
            }
            String doctorId = symmetricCryptography.getInfoFromDb(offlineClinicalTrial.getDoctorId());
            MedWorker doctor = medWorkerRepository.getOne(doctorId);
            String medInstitution = doctor.getMedInstitution().getName();
            offlineClinicalTrial.setInstitution(medInstitution);
            return offlineClinicalTrial;
        } else {
            throw new Exception("Data are corrupted!!!");
        }
    }

    public byte[] exportInPdf(String clinicalTrialId) {
        ClinicalTrialDto clinicalTrialDto = fhirService.getImagingStudy(clinicalTrialId);
        clinicalTrialDto = anonymizeData(clinicalTrialDto);
        try {
            Binary binary = fhirService.getBinary(clinicalTrialDto.getResourcePath());

            return PdfExporter.clinicalTrialExportPdf(clinicalTrialDto, binary);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MedInstitutionDto convert(MedInstitution medInstitution){
        return medInstitutionConverter.convertToDto(medInstitution);
    }

    private ClinicalTrialDto anonymizeData(ClinicalTrialDto clinicalTrialDto){
        String patientId = symmetricCryptography.getInfoFromDb(clinicalTrialDto.getPatient());
        User patient = commonUserRepository.getOne(patientId);
        String patientFirstName = patient.getFirstName();
        String patientLastName = patient.getLastName();
        String anonymizeIntroduction = StringUtil.anonymizePatientData(clinicalTrialDto.getIntroduction(), patientFirstName, patientLastName);
        String anonymizeRelevantParameters = StringUtil.anonymizePatientData(clinicalTrialDto.getRelevantParameters(), patientFirstName, patientLastName);;
        String anonymizeConclusion = StringUtil.anonymizePatientData(clinicalTrialDto.getConclusion(), patientFirstName, patientLastName);;

        clinicalTrialDto.setIntroduction(anonymizeIntroduction);
        clinicalTrialDto.setRelevantParameters(anonymizeRelevantParameters);
        clinicalTrialDto.setConclusion(anonymizeConclusion);
        return clinicalTrialDto;
    }
}
