package com.jstamps.docai.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.jstamps.docai.chaincode.Config;
import com.jstamps.docai.chaincode.client.RegisterUserHyperledger;
import com.jstamps.docai.model.user.CommonUser;
import com.jstamps.docai.model.user.User;
import com.jstamps.docai.repository.CommonUserRepository;
import com.jstamps.docai.security.dto.ErrorResponse;
import com.jstamps.docai.security.dto.SignUpDto;
import com.jstamps.docai.security.jwt.JwtProvider;
import com.jstamps.docai.security.dto.JwtResponse;
import com.jstamps.docai.security.dto.SignInDto;
import com.jstamps.docai.security.service.UserDetailsServiceImpl;
import com.jstamps.docai.service.SymmetricCryptography;
import com.jstamps.docai.util.Constants;
import com.jstamps.docai.validator.AuthException;

import javax.validation.Valid;
import java.util.Date;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthRestAPIs {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CommonUserRepository commonUserRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private SymmetricCryptography symmetricCryptography;

    @PostMapping(value = "/signUp")
    public String signUp(@Valid @RequestBody SignUpDto signUpForm, BindingResult result){
        if(result.hasErrors()){
            throw new AuthException("error while signUp");
        }

        if(userDetailsService.getUser(signUpForm.getEmail()) != null){
            throw new AuthException("user with this username already exsists");
        }

        CommonUser commonUser = CommonUser
                .builder()
                .firstName(signUpForm.getFirstName())
                .lastName(signUpForm.getLastName())
                .gender(signUpForm.getGender())
                .birthday(signUpForm.getBirthday())
                .username(signUpForm.getEmail())
                .email(signUpForm.getEmail())
                .password(userPasswordEncoder.encode(signUpForm.getPassword()))
                .enabled(true)
                .role(Constants.ROLE_COMMON_USER)
                .activeSince(new Date())
                .build();
        CommonUser saved = commonUserRepository.save(commonUser);
        try {
            String userIdentityId = symmetricCryptography.putInfoInDb(saved.getId());
            RegisterUserHyperledger.enrollOrgAppUser(saved.getEmail(), Config.COMMON_USER_ORG, userIdentityId);
        } catch (Exception e) {
            throw new AuthException("Error while signUp in hyperledger");
        }
        return "Success";
    }
    @PostMapping("/signIn")
    public ResponseEntity<?> authenticateUser(@RequestBody SignInDto loginRequest) {
        User userDb = userDetailsService.getUser(loginRequest.getUsername());

        if(userDb == null) {
			return new ResponseEntity<>(new ErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
		}

    	if(!userDb.isEnabled()) {
			return new ResponseEntity<>(new ErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
		}

    	Authentication authentication = null;
		try {

			authentication = authenticationManager.authenticate(
			            new UsernamePasswordAuthenticationToken(
			                    loginRequest.getUsername(),
			                    loginRequest.getPassword()
			            )
			    );
		} catch (AuthenticationException e) {
			System.out.println("ne validan");
			return new ResponseEntity<>(new ErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
		}

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("Get auth: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));
    }
}
