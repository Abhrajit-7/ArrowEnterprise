package com.arrow.Arrow.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BetsDTO {



    private Long bet_number;
    private String selected_numbers;
    private int numberCount;
    private double bet_amount;
    private String username;
    private String slot;

}
