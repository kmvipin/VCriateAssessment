package com.example.vcriateassessment.controller;

import com.example.vcriateassessment.model.ApiResponse;
import com.example.vcriateassessment.model.AuthCredential;
import com.example.vcriateassessment.model.JwtAuthResponse;
import com.example.vcriateassessment.repository.AuthCredentialRepository;
import com.example.vcriateassessment.security.JwtTokenHelper;
import com.example.vcriateassessment.security.PersonDetailService;
import com.example.vcriateassessment.security.interf.MyUserDetails;
import com.example.vcriateassessment.service.EmailService;
import com.example.vcriateassessment.service.LoginService;
import com.example.vcriateassessment.service.OTPService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    private final LoginService loginService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PersonDetailService personDetailService;

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private AuthCredentialRepository authCredentialRepository;

    @Autowired
    private EmailService emailService;

    @Value("${vcriate.same_site}")
    private String same_site;

    @Autowired
    public PublicController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody AuthCredential authCredential) {
        try{
            this.authenticate(authCredential.getEmail(), authCredential.getPassword());
        }
        catch (Exception e) {
            JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
            jwtAuthResponse.setSuccess(false);
            jwtAuthResponse.setMessage(e.getMessage());
            return ResponseEntity.status(401).body(jwtAuthResponse);
        }
        MyUserDetails userDetails = this.personDetailService
                .loadUserByUsername(authCredential.getEmail());
        List<GrantedAuthority> auth = (List<GrantedAuthority>) userDetails.getAuthorities();
        String userRole = auth.get(0).getAuthority();
        if(!userRole.equalsIgnoreCase("ROLE_ADMIN") && !userRole.equalsIgnoreCase("ROLE_NORMAL")){
            JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
            jwtAuthResponse.setSuccess(false);
            jwtAuthResponse.setMessage("Invalid User");
            return ResponseEntity.status(200).body(jwtAuthResponse);
        }

        String token = this.jwtTokenHelper.generateToken(authCredential.getEmail());
        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken("Bearer_"+token);
        ResponseCookie responseCookie = ResponseCookie.from("authorization_token","Bearer_"+token)
                .path("/").sameSite(same_site).httpOnly(true).secure(true).build();
        response.setSuccess(true);
        response.setRole(userDetails.getRole());
        response.setMessage("Login SuccessFully");
        return ResponseEntity.status(200).header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body(response);
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse>
    savePerson(@RequestBody @Valid AuthCredential person,
               @RequestParam String otp, HttpServletRequest request){
        int status;
        ApiResponse apiResponse = new ApiResponse();
        if(authCredentialRepository.existsByEmail(person.getEmail())){
            return ResponseEntity.status(400).body(new ApiResponse(false,"Email Already Exist"));
        }
        ResponseEntity<ApiResponse> res = verifyOTP(otp,person.getEmail(),request);
        if(!res.getBody().isSuccess()){
            return res;
        }
        try{
            authCredentialRepository.save(person);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Person Saved Successfully");
            status = 201;
        }
        catch (Exception e){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Something went wrong. Please try again");
            status = 200;
        }
        return ResponseEntity.status(status).body(apiResponse);
    }

    @GetMapping("/verify/user-status")
    public ResponseEntity<ApiResponse> checkUserStatus(@RequestParam String email, @RequestParam String username){
        if(authCredentialRepository.existsByEmail(email)){
            return ResponseEntity.status(200).body(new ApiResponse(false,"Email Already Exist"));
        }
        return ResponseEntity.status(200).body(new ApiResponse(true,"UserName And Email Are Available"));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse> sendOtp(@RequestParam String email, HttpSession session, HttpServletResponse res){
        String otp = OTPService.generateOtp();
        boolean success = emailService.sendOtpEmail(email,otp);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setSuccess(success);
        if(success){
            apiResponse.setMessage("OTP Sent Successfully");
            session.setAttribute(email+"_otp",otp);
        }
        else{
            apiResponse.setMessage("Something Went Wrong");
        }
        return ResponseEntity.status(200).body(apiResponse);//header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body(apiResponse);
    }

    @GetMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOTP(@RequestParam String otp, @RequestParam String email, HttpServletRequest request){
        String generatedOTP = (String)request.getSession().getAttribute(email+"_otp");
        if(!otp.equals(generatedOTP)){
            return ResponseEntity.status(200).body(new ApiResponse(false,"Invalid OTP"));
        }
        return ResponseEntity.status(200).body(new ApiResponse(true,"Verified Successfully"));
    }

    private void authenticate(String username, String password) throws Exception {

        UsernamePasswordAuthenticationToken authenticationToken = new
                UsernamePasswordAuthenticationToken(username, password);
        try {
            this.authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid username or password !!");
        }
    }
}