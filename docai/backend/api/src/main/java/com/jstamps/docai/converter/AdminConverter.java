package com.jstamps.docai.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jstamps.docai.converter.declaration.ConverterInterface;
import com.jstamps.docai.dto.user.UserDto;
import com.jstamps.docai.model.user.Admin;
import com.jstamps.docai.repository.AdminRepository;

import java.util.Date;

@Component
public class AdminConverter implements ConverterInterface<Admin, UserDto, UserDto> {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDto convertToDto(Admin admin) {
        return UserDto
                .builder()
                .id(admin.getId())
                .email(admin.getEmail())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .role(admin.getRole())
                .enabled(admin.isEnabled())
                .activeSince(admin.getActiveSince())
                .build();
    }

    @Override
    public Admin convertFromDto(UserDto object, boolean update) {
        if(update){
            Admin admin = adminRepository.findByUsername(object.getEmail());
            admin.setFirstName(object.getFirstName());
            admin.setLastName(object.getLastName());
            return admin;
        } else {
            return Admin
                    .builder()
                    .email(object.getEmail())
                    .username(object.getEmail())
                    .firstName(object.getFirstName())
                    .lastName(object.getLastName())
                    .role(object.getRole())
                    .enabled(true)
                    .activeSince(new Date())
                    .build();
        }
    }
}
