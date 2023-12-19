package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.LoginDto;
import com.partyhub.PartyHub.dto.RegisterDto;
import com.partyhub.PartyHub.entities.CustomerDetails;
import com.partyhub.PartyHub.entities.Role;
import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.repository.CustomerDetailsRepository;
import com.partyhub.PartyHub.repository.RoleRepository;
import com.partyhub.PartyHub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDetailsRepository customerDetailsRepository;

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("User sign in success", HttpStatus.OK);
    }

    @PostMapping("register")
    @Transactional
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        if(userRepository.existsByEmail(registerDto.getEmail())){
            return new ResponseEntity<>("Email already used!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        Role role = roleRepository.findByName("USER").orElseThrow(()-> new RuntimeException("nu e rol"));
        user.setRoles(Collections.singletonList(role));

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAge(registerDto.getAge());
        customerDetails.setFullName(registerDto.getFullName());
        customerDetails.setDiscountForNextTicket(0);
        customerDetailsRepository.save(customerDetails);

        user.setCustomerDetails(customerDetails);

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }
}
