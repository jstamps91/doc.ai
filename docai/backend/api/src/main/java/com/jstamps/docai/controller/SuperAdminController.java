package com.jstamps.docai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.jstamps.docai.dto.form.MedWorkerForm;
import com.jstamps.docai.dto.medInstitution.MedInstitutionDto;
import com.jstamps.docai.dto.user.MedWorkerDto;
import com.jstamps.docai.dto.user.UserDto;
import com.jstamps.docai.security.service.UserDetailsServiceImpl;
import com.jstamps.docai.service.SuperAdminService;
import com.jstamps.docai.util.ValidationUtil;
import com.jstamps.docai.validator.AuthException;
import com.jstamps.docai.validator.ValidationException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/superAdmin")
public class SuperAdminController {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private SuperAdminService superAdminService;

    @PostMapping
    public UserDto editUser(@Valid @RequestBody UserDto userDto, BindingResult result){
        if(result.hasErrors()){
            String errorMsg = ValidationUtil.formatValidationErrorMessages(result.getAllErrors());
            throw new ValidationException(errorMsg);
        }

        return superAdminService.editUser(userDto);
    }

    @GetMapping("/institution")
    public List<MedInstitutionDto> getAll(){
        return superAdminService.getAll();
    }

    @PostMapping("/institution")
    public MedInstitutionDto addNewInstitution(@Valid @RequestBody MedInstitutionDto medInstitutionDto, BindingResult result){
        if(result.hasErrors()){
            String errorMsg = ValidationUtil.formatValidationErrorMessages(result.getAllErrors());
            throw new ValidationException(errorMsg);
        }

        return superAdminService.addNewInstitution(medInstitutionDto);
    }

    @GetMapping("/institutionAdmin")
    public List<MedWorkerDto> getAllMedInstitutionAdmins(){
        return superAdminService.getAllMedInstitutionAdmins();
    }

    @PostMapping("/institutionAdmin")
    public MedWorkerDto addNewMedInstitutionAdmin(@Valid @RequestBody MedWorkerForm medWorkerDto, BindingResult result){
        if(result.hasErrors()){
            String errorMsg = ValidationUtil.formatValidationErrorMessages(result.getAllErrors());
            throw new ValidationException(errorMsg);
        }

        if(userDetailsService.getUser(medWorkerDto.getEmail()) != null){
            throw new AuthException("user with this username already exsists");
        }

        return superAdminService.addNewMedInstitutionAdmin(medWorkerDto);
    }
}
