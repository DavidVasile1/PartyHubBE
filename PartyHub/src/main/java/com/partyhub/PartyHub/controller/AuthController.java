package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.AuthResponseDto;
import com.partyhub.PartyHub.dto.LoginDto;
import com.partyhub.PartyHub.dto.RegisterDto;
import com.partyhub.PartyHub.entities.Role;
import com.partyhub.PartyHub.entities.User;
import com.partyhub.PartyHub.entities.UserDetails;
import com.partyhub.PartyHub.exceptions.EmailAlreadyUsedException;
import com.partyhub.PartyHub.exceptions.RoleNotFoundException;
import com.partyhub.PartyHub.exceptions.UserAlreadyVerifiedException;
import com.partyhub.PartyHub.exceptions.UserNotFoundException;
import com.partyhub.PartyHub.security.JwtGenerator;
import com.partyhub.PartyHub.service.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtGenerator jwtGenerator;
    private final EmailSenderService emailSenderService;
    private final ProfileService profileService;

    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),
                loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);

        User user = userService.findByEmail(loginDto.getEmail());
        if(user!=null){
            if(!user.isVerified()){
                AuthResponseDto responseDto = new AuthResponseDto(null);
                responseDto.setActivated(false);
                return new ResponseEntity<>(responseDto, HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<ApiResponse> verify(@PathVariable UUID token) {
        User user = this.userService.findByVerificationToken(token);
        try {


                if (!user.isVerified()) {
                    user.setVerified(true);
                    this.userService.save(user);
                    return ResponseEntity.ok(new ApiResponse(true, "Account activated!"));
                } else {
                    throw new UserAlreadyVerifiedException("User already verified!");
                }

        }catch ( UserAlreadyVerifiedException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "User already verified!"));
        }catch ( UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse( false,"User not found!"));
        }
    }

    @GetMapping("reset-password/{email}")
    public ResponseEntity<ApiResponse> sendResetPasswordEmail(@PathVariable String email) {
        try {
            String clientUrl = "http://localhost:4200";

            User user = userService.findByEmail(email);
            user.setVerificationToken(UUID.randomUUID());
            this.userService.save(user);

            String emailContent = "<html><body>"
                    + "<h1>Password Reset</h1>"
                    + "<p>An email has been sent to you regarding resetting your password. "
                    + "Please click the following button to proceed:</p>"
                    + "<a href=\"" + clientUrl + "/reset-password/" + user.getVerificationToken() + "\">"
                    + "<button style=\"background-color: red; color: white; padding: 15px 32px; text-align: center; border-radius: 15px; border: none;"
                    + "text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px; cursor: pointer;\">Reset Password</button></a>"
                    + "<p>If you did not request to reset your password, please ignore this email.</p>"
                    + "</body></html>";

            this.emailSenderService.sendHtmlEmail(email, "Password Reset - PartyHub", emailContent);

            return new ResponseEntity<>(new ApiResponse(true, "Email sent!"), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse(false, "User not found!"), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("reset-password/{token}")
    public ResponseEntity<ApiResponse> resetPassword(@PathVariable UUID token,@RequestBody String newPassword) {
        try {
            User user = this.userService.findByVerificationToken(token);
            profileService.resetPassword(user.getEmail(), newPassword);
            return ResponseEntity.ok(new ApiResponse(true, "Password reset successfully!"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false, "Failed to reset password"));
        }
    }

    @PostMapping("register")
    @Transactional
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterDto registerDto){
        try {

            if (userService.existsByEmail(registerDto.getEmail())) {
                throw new EmailAlreadyUsedException("Email already used!");
            }

            User user = new User();
            user.setEmail(registerDto.getEmail());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

            Role role = roleService.findByName("USER");

            user.setRoles(Collections.singletonList(role));
            user.setVerificationToken(UUID.randomUUID());

            UserDetails userDetails = userDetailsService.create(registerDto.getAge(), registerDto.getFullName());
            user.setUserDetails(userDetails);

            userService.save(user);

            String emailContent = "<html><body>"
                    + "<h1>Account Activation</h1>"
                    + "<p>An email has been sent to you for activating your account. "
                    + "Please click the following button to proceed:</p>"
                    + "<a href=\"" + "http://localhost:4200" + "/verify/" + user.getVerificationToken() + "\">"
                    + "<button style=\"background-color: red; color: white; padding: 15px 32px; text-align: center; border-radius: 15px; border: none;"
                    + "text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px; cursor: pointer;\">Activate account</button></a>"
                    + "<p>If you did not register for an account, please ignore this email.</p>"
                    + "</body></html>";

            this.emailSenderService.sendEmail(registerDto.getEmail(), "PartyHub", emailContent);

            return new ResponseEntity<>(new ApiResponse(true, "User registration successful"), HttpStatus.OK);
        }catch (EmailAlreadyUsedException e) {
            return new ResponseEntity<>(new ApiResponse(false, "Email already used!"), HttpStatus.BAD_REQUEST);
        }catch (RoleNotFoundException e){
            return new ResponseEntity<>(new ApiResponse(false, "Role not found!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (RuntimeException e){
            return new ResponseEntity<>(new ApiResponse(false, "Server error!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
