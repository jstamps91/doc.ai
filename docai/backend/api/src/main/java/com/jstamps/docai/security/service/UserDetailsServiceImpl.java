package com.jstamps.docai.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jstamps.docai.model.user.Admin;
import com.jstamps.docai.model.user.CommonUser;
import com.jstamps.docai.model.user.MedWorker;
import com.jstamps.docai.model.user.User;
import com.jstamps.docai.repository.AdminRepository;
import com.jstamps.docai.repository.CommonUserRepository;
import com.jstamps.docai.repository.MedWorkerRepository;
import com.jstamps.docai.security.dto.UserPrinciple;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MedWorkerRepository medWorkerRepository;

    @Autowired
    private CommonUserRepository commonUserRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = getUser(username);
        return UserPrinciple.build(user);
    }

    public User getLoggedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        User user;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
            user = getUser(username);
            return user;
        } else {
            return null;
        }

    }

    public User getUser(String email){
        MedWorker medWorker = medWorkerRepository.findByUsername(email);
        if(medWorker != null){
            return medWorker;
        }
        CommonUser commonUser = commonUserRepository.findByUsername(email);
        if(commonUser != null){
            return commonUser;
        }

        Admin admin = adminRepository.findByUsername(email);
        return admin;
    }


}
