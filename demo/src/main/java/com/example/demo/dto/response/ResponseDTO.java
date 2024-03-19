package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonTypeName("response")
public class ResponseDTO {

    private Integer responseCode;

    private String responseMessage;

    public ResponseDTO setResponse(Integer responseCode, String responseMessage) {
        this.responseMessage = responseMessage;
        this.responseCode = responseCode;

        return this;
    }
}
