package com.jstamps.docai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.jstamps.docai.dto.contract.ClinicalTrialAccessRequestDto;
import com.jstamps.docai.dto.contract.ClinicalTrialAccessRequestForm;
import com.jstamps.docai.dto.contract.ClinicalTrialAccessSendRequestForm;
import com.jstamps.docai.dto.contract.ClinicalTrialPreviewDto;
import com.jstamps.docai.dto.form.ResetPasswordForm;
import com.jstamps.docai.dto.form.SearchClinicalTrialForm;
import com.jstamps.docai.dto.medInstitution.ClinicalTrialDto;
import com.jstamps.docai.dto.medInstitution.MedInstitutionDto;
import com.jstamps.docai.dto.user.UserDto;
import com.jstamps.docai.service.FhirService;
import com.jstamps.docai.service.UserService;
import com.jstamps.docai.util.ValidationUtil;
import com.jstamps.docai.validator.AuthException;
import com.jstamps.docai.validator.ValidationException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FhirService fhirService;

    @GetMapping("/current")
    public UserDto getCurrentUser(){
        return userService.getCurrentUser();
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@Valid @RequestBody ResetPasswordForm resetPasswordForm, BindingResult result){
        if(result.hasErrors()){
            String errorMsg = ValidationUtil.formatValidationErrorMessages(result.getAllErrors());
            throw new AuthException(errorMsg);
        }

        String newPassword = resetPasswordForm.getPassword();
        String newPasswordRepeated = resetPasswordForm.getPasswordRepeat();
        if(!ValidationUtil.passwordMatch(newPassword, newPasswordRepeated)){
            throw new AuthException("Password does not match");
        }

        userService.resetPassword(newPassword);

        return "Success";
    }

    @GetMapping("/trialAccessRequest")
    public List<ClinicalTrialAccessRequestDto> getClinicalTrialAccessRequests(
            @RequestParam(value = "requestType", required = false, defaultValue="") String requestType
    ) throws Exception {
        return userService.getClinicalTrialAccessRequests(requestType);
    }

    @PostMapping("/trialAccessRequest")
    public ClinicalTrialAccessRequestDto trialAccessRequestDecision(
            @Valid @RequestBody ClinicalTrialAccessRequestForm clinicalTrialAccessRequestForm,
            BindingResult result
    ) throws Exception {
        if(result.hasErrors()){
            String errorMsg = ValidationUtil.formatValidationErrorMessages(result.getAllErrors());
            throw new ValidationException(errorMsg);
        }
        return userService.trialAccessRequestDecision(clinicalTrialAccessRequestForm);
    }

    @GetMapping("/institution")
    public List<MedInstitutionDto> getMedInstitutionDatasource(){
        return userService.getMedInstitutionDatasource();
    }

    @PostMapping("/clinicalTrialPreview")
    public List<ClinicalTrialPreviewDto> getClinicalTrialsPreview(
            @Valid @RequestBody SearchClinicalTrialForm searchClinicalTrialForm,
            @RequestParam(value = "page", required = false, defaultValue="") String page,
            @RequestParam(value = "perPage", required = false, defaultValue="") String perPage,
            BindingResult result
    ) throws Exception {
        if(result.hasErrors()){
            String errorMsg = ValidationUtil.formatValidationErrorMessages(result.getAllErrors());
            throw new ValidationException(errorMsg);
        }
        return userService.getClinicalTrialsPreview(searchClinicalTrialForm, page, perPage);
    }

    @PostMapping("/sendAccessRequest")
    public ClinicalTrialAccessSendRequestForm sendAccessRequest(
            @Valid @RequestBody ClinicalTrialAccessSendRequestForm clinicalTrialAccessSendRequestForm,
            BindingResult result
    ) throws Exception {
        if(result.hasErrors()){
            String errorMsg = ValidationUtil.formatValidationErrorMessages(result.getAllErrors());
            throw new ValidationException(errorMsg);
        }
        return userService.sendAccessRequest(clinicalTrialAccessSendRequestForm);
    }

    @GetMapping("/clinicalTrial/{clinicalTrialId}")
    public ClinicalTrialDto getClinicalTrial(
            @PathVariable String clinicalTrialId,
            @RequestParam(value = "accessUserRole", required = false, defaultValue="requester") String accessUserRole
    ) throws Exception {
        return userService.getClinicalTrial(clinicalTrialId, accessUserRole);
    }

    @GetMapping("/image/{binaryId}")
    public byte[] getImage(@PathVariable String binaryId) {
        return fhirService.getImage(binaryId);
    }

    @GetMapping("/file/{clinicalTrialId}")
    public byte[] exportInPdf(@PathVariable String clinicalTrialId) {
        return userService.exportInPdf(clinicalTrialId);
    }
}
