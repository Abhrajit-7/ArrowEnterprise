package com.arrow.Arrow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BetListDTO {

    private Long id;
    private String selected_numbers;
    private double amount;
}
