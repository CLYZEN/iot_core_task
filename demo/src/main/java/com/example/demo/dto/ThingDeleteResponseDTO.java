package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ThingDeleteResponseDTO {

    private String message;

    private Integer responseCode;

    private Integer code;
}
