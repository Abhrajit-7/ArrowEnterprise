package com.arrow.Arrow.services;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityService {
    public boolean isUsernameEqualToAuthenticatedUser(String username){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        //SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null || !authentication.isAuthenticated()){
            return false;
        }
        String authUsername=authentication.getName();
        System.out.println(authUsername);

        return username.equals(authUsername);
    }
}
