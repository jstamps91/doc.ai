package com.jstamps.docai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.jstamps.docai.dto.form.ClinicalTrialForm;
import com.jstamps.docai.dto.form.MedWorkerForm;
import com.jstamps.docai.dto.medInstitution.ClinicalTrialDto;
import com.jstamps.docai.dto.user.CommonUserDto;
import com.jstamps.docai.dto.user.MedWorkerDto;
import com.jstamps.docai.service.DoctorService;
import com.jstamps.docai.util.ValidationUtil;
import com.jstamps.docai.validator.ValidationException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping
    public MedWorkerDto editUser(@Valid @RequestBody MedWorkerForm medWorkerDto, BindingResult result){
        if(result.hasErrors()){
            String errorMsg = ValidationUtil.formatValidationErrorMessages(result.getAllErrors());
            throw new ValidationException(errorMsg);
        }

        return doctorService.editUser(medWorkerDto);
    }

    @GetMapping("/patients")
    public List<CommonUserDto> getPatients(){
        return doctorService.getPatients();
    }

    @PostMapping("/clinicalTrial")
    public ClinicalTrialDto addClinicalTrial(@Valid @ModelAttribute ClinicalTrialForm clinicalTrialForm, BindingResult result) throws Exception {
        if(result.hasErrors()){
            String errorMsg = ValidationUtil.formatValidationErrorMessages(result.getAllErrors());
            throw new ValidationException(errorMsg);
        }

        if(!validInputFile(clinicalTrialForm.getFile())) {
            throw new ValidationException("Invalid file format");
        }

        return doctorService.addClinicalTrial(clinicalTrialForm);
    }

    private boolean validInputFile(MultipartFile file) throws IOException {
        if(file == null) return true;

        String contentType = file.getContentType();
        if(!contentType.equals("image/png") && !contentType.equals("image/jpeg")) {
            return false;
        }

        long l = 1l;
        try {
            l = file.getResource().contentLength();
            if(l > 2000000l) {
                return false;
            }
        } catch (IOException e1) {
            return false;
        }

        return true;
    }
}
