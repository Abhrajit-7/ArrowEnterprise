package com.arrow.Arrow.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profile_details")
public class UserProfile  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(insertable=false, updatable=false)
    private String username;

    @NotBlank
    private String fullName;

    @NotBlank
    private String email;

    @NotBlank
    private Long phone;

    @NotBlank
    private String pan;

    @NotBlank
    private String bankAccountNumber;
    
    @NotBlank
    private String ifsc;

    @NotBlank
    private String aadhaar;

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username", unique = true) // Join on the unique username column
    @JsonManagedReference
    private User user;
}
