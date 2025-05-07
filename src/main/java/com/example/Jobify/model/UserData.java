package com.example.Jobify.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "users")
public class UserData {
    @Id
    private String id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String location;
    private Role role;
    private String url;
    private String publicId;

}
