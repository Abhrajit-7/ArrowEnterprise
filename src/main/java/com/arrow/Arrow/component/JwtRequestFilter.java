package com.arrow.Arrow.component;

import com.arrow.Arrow.entity.User;
import com.arrow.Arrow.services.UserServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtTokenUtil jwtHelper;


    @Autowired
    private UserServiceImpl userDetailsService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //Authorization
        // Check if there is already an authenticated user
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            // User is already authenticated, skip the filter
            logger.info("skipping filter..");
            filterChain.doFilter(request, response);
        }
        else {

            String requestHeader = request.getHeader("Authorization");
            logger.info(" Header :  {} ", requestHeader);

            String username = null;
            String token = null;
            if (requestHeader != null && requestHeader.startsWith("Bearer")) {
                //looking good
                token = requestHeader.substring(7);
                logger.info(token);
                try {
                    username = this.jwtHelper.getUsernameFromToken(token);
                    logger.info(username);
                } catch (IllegalArgumentException e) {
                    logger.error("Illegal Argument while fetching the username !!");
                    e.printStackTrace();
                } catch (ExpiredJwtException e) {
                    logger.error("Given jwt token is expired !!");
                    e.printStackTrace();
                } catch (MalformedJwtException e) {
                    logger.error("Some changed has done in token !! Invalid Token");
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                logger.info("Invalid Header Value !! ");
            }
            //
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                //fetch user detail from username
                User userDetails = userDetailsService.findUserByUsername(username);
                Boolean validateToken = this.jwtHelper.validateToken(token, userDetails.getUsername());
                Collection<GrantedAuthority> authorities = userDetailsService.getAuthoritiesForUser(username);
                if (validateToken) {
                    //set the authentication
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.info("Validation fails !!");
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}