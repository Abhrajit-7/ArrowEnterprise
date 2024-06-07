package com.arrow.Arrow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WinnerDTO {

   private String username;
   private double totalInvested;
   private String selectedNumbers;

}
