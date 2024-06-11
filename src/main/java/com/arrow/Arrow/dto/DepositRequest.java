package com.arrow.Arrow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepositRequest {
        private String amount;
        private String productInfo;
        private String firstname;
        private String email;
        private String phone;

        // Getters and Setters
}
