package com.example.Jobify.DTO;

import com.example.Jobify.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsercurrentDTO {

    private String id;
    private String name;
    private String email;
    private Role role;
    private String location;
    private String lastName;
    private String url;
}
