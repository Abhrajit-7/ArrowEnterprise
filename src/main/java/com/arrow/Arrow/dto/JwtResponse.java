package com.arrow.Arrow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class JwtResponse  {

    private final Long id;
    private  final String jwttoken;
    private final String username;
    private final double accountBal;
    private final Role role;


}
