package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ErrorMessageDTO {
    private String errorMessage;

    public ErrorMessageDTO(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
