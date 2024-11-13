package com.web.gilproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinateDto {
    private String latitude;
    private String longitude;
    @JsonIgnore
    private String timestamp;
}
