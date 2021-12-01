package com.jstamps.docai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.jstamps.docai.dto.form.MedWorkerForm;
import com.jstamps.docai.dto.medInstitution.MedAdminDoctors;
import com.jstamps.docai.dto.user.MedWorkerDto;
import com.jstamps.docai.security.service.UserDetailsServiceImpl;
import com.jstamps.docai.service.MedAdminService;
import com.jstamps.docai.util.ValidationUtil;
import com.jstamps.docai.validator.AuthException;
import com.jstamps.docai.validator.ValidationException;

import javax.validation.Valid;

@RestController
@RequestMapping("/medAdmin")
public class MedAdminController {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MedAdminService medAdminService;

    @GetMapping("/doctor")
    public MedAdminDoctors getAllMedInstitutionDoctors(){
        return medAdminService.getAllMedInstitutionDoctors();
    }

    @PostMapping("/doctor")
    public MedWorkerDto addNewMedInstitutionDoctor(@Valid @RequestBody MedWorkerForm medWorkerDto, BindingResult result){
        if(result.hasErrors()){
            String errorMsg = ValidationUtil.formatValidationErrorMessages(result.getAllErrors());
            throw new ValidationException(errorMsg);
        }

        if(userDetailsService.getUser(medWorkerDto.getEmail()) != null){
            throw new AuthException("user with this username already exsists");
        }

        return medAdminService.addNewMedInstitutionDoctor(medWorkerDto);
    }
}
