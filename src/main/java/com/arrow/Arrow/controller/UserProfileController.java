package com.arrow.Arrow.controller;

import com.arrow.Arrow.dto.ProfileDTO;
import com.arrow.Arrow.entity.UserProfile;
import com.arrow.Arrow.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/{username}")
    public Object getUserProfileByUsername(@PathVariable String username) {
        return userProfileService.getUserProfileByUsername(username);
    }

    @PostMapping("/{username}")
    public UserProfile createUserProfile(@RequestBody UserProfile userProfile, @PathVariable String username) {
        return userProfileService.createUserProfile(userProfile, username);
    }

    @PutMapping("/{username}")
    public ProfileDTO updateUserProfileByUsername(@PathVariable String username, @RequestBody UserProfile userProfile) {
        return userProfileService.updateUserProfileByUsername(username, userProfile);
    }
}

