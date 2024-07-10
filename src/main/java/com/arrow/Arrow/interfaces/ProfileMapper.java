package com.arrow.Arrow.interfaces;

import com.arrow.Arrow.dto.ProfileDTO;
import com.arrow.Arrow.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileDTO toDTO(UserProfile userProfile) {
        if (userProfile == null) {
            return null;
        }
        return new ProfileDTO(
                userProfile.getFullName(),
                userProfile.getEmail(),
                userProfile.getPhone(),
                userProfile.getPan(),
                userProfile.getBankAccountNumber(),
                userProfile.getIfsc(),
                userProfile.getAadhaar(),
                userProfile.getUsername()
        );
    }

    public UserProfile toEntity(ProfileDTO profileDTO, String username) {
        if (profileDTO == null) {
            return null;
        }
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName(profileDTO.getFullName());
        userProfile.setEmail(profileDTO.getEmail());
        userProfile.setPhone(profileDTO.getPhone());
        userProfile.setPan(profileDTO.getPan());
        userProfile.setBankAccountNumber(profileDTO.getBankAccountNumber());
        userProfile.setIfsc(profileDTO.getIfsc());
        userProfile.setAadhaar(profileDTO.getAadhaar());
        userProfile.setUsername(username);
        return userProfile;
    }
    public void updateEntityFromDTO(ProfileDTO profileDTO, UserProfile userProfile){
        if (profileDTO == null || userProfile == null) {
            return;
        }
        userProfile.setFullName(profileDTO.getFullName());
        userProfile.setEmail(profileDTO.getEmail());
        userProfile.setPhone(profileDTO.getPhone());
        userProfile.setPan(profileDTO.getPan());
        userProfile.setBankAccountNumber(profileDTO.getBankAccountNumber());
        userProfile.setIfsc(profileDTO.getIfsc());
        userProfile.setAadhaar(profileDTO.getAadhaar());
        userProfile.setUsername(profileDTO.getUsername());
    }

}

