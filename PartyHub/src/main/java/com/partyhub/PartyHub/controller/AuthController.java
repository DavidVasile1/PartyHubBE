package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.AuthResponseDto;
import com.partyhub.PartyHub.dto.LoginDto;
import com.partyhub.PartyHub.dto.RegisterDto;
import com.partyhub.PartyHub.entities.UserDetails;
import com.partyhub.PartyHub.entities.Role;
import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.security.JwtGenerator;
import com.partyhub.PartyHub.service.UserDetailsService;
import com.partyhub.PartyHub.service.RoleService;
import com.partyhub.PartyHub.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtGenerator jwtGenerator;

    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        System.out.println(token);
        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
    }

    @PostMapping("register")
    @Transactional
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        if(userService.existsByEmail(registerDto.getEmail())){
            return new ResponseEntity<>("Email already used!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        Role role = roleService.findByName("USER").orElseThrow(()-> new RuntimeException("Role not found"));
        user.setRoles(Collections.singletonList(role));

        UserDetails userDetails = userDetailsService.create(registerDto.getAge(), registerDto.getFullName());

        user.setUserDetails(userDetails);

        userService.save(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }
}
