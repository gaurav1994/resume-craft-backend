package com.craftsol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactInformationDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String website;
    private String address;
}
