package com.arrow.Arrow.services;

import com.arrow.Arrow.ExceptionHandling.UserNotFoundException;
import com.arrow.Arrow.dto.ProfileDTO;
import com.arrow.Arrow.entity.User;
import com.arrow.Arrow.entity.UserProfile;
import com.arrow.Arrow.interfaces.ProfileMapper;
import com.arrow.Arrow.repository.ProfileRepository;
import com.arrow.Arrow.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;
    Logger logger=LoggerFactory.getLogger(UserProfileService.class);

    @Autowired
    private ProfileMapper profileMapper;

    @Cacheable(value = "profileCache", key = "#username")
    public ProfileDTO getUserProfileByUsername(String username) {
        UserProfile profile = profileRepository.findByUsername(username);
        if (profile == null) {
            logger.info("Profile not found for username: {}", username);
            return null;  // Return null if profile not found
        }
        logger.info("Profile found for username: {}", profile.getUsername());
        return profileMapper.toDTO(profile);
    }

    @Transactional
    public UserProfile createUserProfile(UserProfile userProfile, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.error("User not found with username: {}", username);
            throw new UserNotFoundException("User not found with username: " + username);
        }
        userProfile.setUser(user);
        logger.info("Creating profile for user: {}", userProfile);
        return profileRepository.save(userProfile);
    }

    @CacheEvict(value = "profileCache", key = "#username")
    @Transactional
    public ProfileDTO updateUserProfileByUsername(String username, UserProfile userProfile) {
        UserProfile existingProfile = profileRepository.findByUsername(username);
        if (existingProfile == null) {
            logger.info("No existing profile found for username: {}. Creating new profile.", username);
            return profileMapper.toDTO(createUserProfile(userProfile, username));
        }
        userProfile.setId(existingProfile.getId());
        userProfile.setUser(existingProfile.getUser());
        logger.info("Updating profile for username: {}", username);
        UserProfile updatedProfile = profileRepository.save(userProfile);
        return profileMapper.toDTO(updatedProfile);
    }
}

