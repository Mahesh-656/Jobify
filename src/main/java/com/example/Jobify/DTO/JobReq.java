package com.example.Jobify.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobReq {

    private String company;
    private String position;
    private String jobStatus;
    private String jobType;
    private String jobLocation;
    private String userId;
}
