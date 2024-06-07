package com.arrow.Arrow.controller;

import com.arrow.Arrow.dto.JwtRequest;
import com.arrow.Arrow.dto.JwtResponse;
import com.arrow.Arrow.dto.MembersDTO;
import com.arrow.Arrow.dto.UserDTO;
import com.arrow.Arrow.entity.User;
import com.arrow.Arrow.services.UserServiceImpl;
import com.arrow.Arrow.component.JwtTokenUtil;
import com.arrow.Arrow.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Objects;

@Transactional
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private JwtTokenUtil helper;

    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        User user = userServiceImpl.createUser(userDTO);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/members/{username}/showTree")
    public ResponseEntity<?> getUserWithChildren(@PathVariable String username, @RequestHeader("Authorization") String jwtToken) throws Exception {
        logger.info("Inside getUserWithChildren controller class ..");
        logger.info("Request: {}, {}",username, jwtToken);
        String token = jwtToken.substring(7);
        String tokenUsername=helper.getUsernameFromToken(token);
        logger.info(tokenUsername);
        if(Objects.equals(username, tokenUsername)) {
            MembersDTO membersDTO = userServiceImpl.getUserWithChildren(username);
            return ResponseEntity.ok(membersDTO);
        }
        else {
            throw new IllegalAccessException("Username is illegal !");
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest request) {
        //System.out.println(request.getUsername() + " " + request.getPassword());
        logger.info("Username: {}", request.getUsername());
        logger.info("Password: {}", request.getPassword());
        //this.doAuthenticate(request.getUsername(), request.getPassword());
        try {
            User userDetails = userServiceImpl.findUserByUsername(request.getUsername());
            if(userDetails.getAccountBalance()==null)
            {
                userDetails.setAccountBalance(0.0);
            }
            if (Objects.equals(userDetails.getPassword(), request.getPassword()) && Objects.equals(userDetails.getUsername(), request.getUsername())) {
                this.doAuthenticate(request.getUsername(), request.getPassword());
                //User userDetails= userServiceImpl.loadUserByUsername(request.getUsername());
                String token = this.helper.generateToken(userDetails.getUsername());
                JwtResponse response = JwtResponse.builder()
                        .jwttoken(token)
                        .id(userDetails.getId())
                        .username(userDetails.getUsername())
                        .accountBal(userDetails.getAccountBalance())
                        .role(userDetails.getRole())
                        .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                throw new BadCredentialsException("Invalid password");
            }
        } catch (UsernameNotFoundException ex) {
            logger.error("User not present: :/ {}", ex.getMessage());
            //throw new BadCredentialsException("Username/Password not valid");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Username not found");
        } catch (Exception e) {
            logger.error("LoginError : :( {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password Invalid");
        } catch (InternalError e) {
            logger.error("LoginError : :( {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }

    private void doAuthenticate(String username, String password) {
        logger.debug(password);
        Collection<GrantedAuthority> authorities = userServiceImpl.getAuthoritiesForUser(username);
        new UsernamePasswordAuthenticationToken(username, password);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}