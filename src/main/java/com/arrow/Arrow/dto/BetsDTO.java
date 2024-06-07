package com.arrow.Arrow.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BetsDTO {



    private Long bet_number;
    private String selected_numbers;
    private double bet_amount;
    private String username;
    private String slot;

}
