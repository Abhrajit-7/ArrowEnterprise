package com.arrow.Arrow.services;

import com.arrow.Arrow.entity.User;
import com.sun.jdi.request.DuplicateRequestException;
import com.arrow.Arrow.dto.MembersChildDTO;
import com.arrow.Arrow.dto.MembersDTO;
import com.arrow.Arrow.dto.Role;
import com.arrow.Arrow.dto.UserDTO;
import com.arrow.Arrow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl{

    @Autowired
    public UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public User createUser(UserDTO userDTO) {

        User user=new User();
        User existingUser = userRepository.findByUsername(userDTO.getUsername());
        if (existingUser != null) {
            throw new DuplicateRequestException(user.getUsername());
        }

        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        //user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());

        if(userDTO.getParentId()!=null) {
            User parent1 = userRepository.findById(userDTO.getParentId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid parent ID"));
            user.setParent(parent1);
        }
        return userRepository.save(user);
    }

    public User findUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    public Collection<GrantedAuthority> getAuthoritiesForUser(String username) {

        // Assuming you retrieve roles for the user from the database or another source
        User userRoleFetch= findUserByUsername(username);
        Set<Role> userRoles= Collections.singleton(userRoleFetch.getRole());

        // Convert roles to GrantedAuthority objects
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors
                        .toList());
    }

    public MembersDTO getUserWithChildren(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found by username : " + username);
        }
        //User user = userServiceImpl.getUserWithChildren(username);
        System.out.println(user.getUsername());
        //Setting up parents and children response DTO
        MembersDTO membersDTO=new MembersDTO();
        membersDTO.setUsername(user.getUsername());
        List<MembersChildDTO> childrenDTO = user.getChildren().stream()
                .map(child -> {
                    MembersChildDTO membersChildDTO=new MembersChildDTO();
                    membersChildDTO.setId(child.getId());
                    membersChildDTO.setUsername(child.getUsername());
                    return membersChildDTO;
                })
                .toList();
        membersDTO.setChildren(childrenDTO);
        return membersDTO;
    }

    public boolean deductUserBalance(String username, double withdrawalAmount) {
        // Retrieve user from the database
        User user = userRepository.findByUsername(username);

        // If user is found and has sufficient balance, deduct the withdrawal amount
        if (user != null && user.getAccountBalance() >= withdrawalAmount) {
            double newBalance = user.getAccountBalance() - withdrawalAmount;
            user.setAccountBalance(newBalance);
            userRepository.save(user);
            return true; // Deduction successful
        }
        return false; // Insufficient balance or user not found
    }
}