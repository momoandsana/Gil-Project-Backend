package com.web.gilproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointConfirmDTO {
    private Integer time;
    private Double distance;
}
