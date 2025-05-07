package com.example.Jobify.DTO;

import lombok.Data;
import java.util.List;

@Data
public class JobStatsDTO {
    private DefaultStats defaultStats;
    private List<MonthlyApplication> monthlyApplications;

    @Data
    public static class DefaultStats {
        private long pending;
        private long interview;
        private long declined;
    }

    @Data
    public static class MonthlyApplication {
        private String date;
        private long count;
    }
} 