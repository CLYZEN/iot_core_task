package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceInfoDTO {

    private String maker;

    private String model;

    private String serial_number;

    @JsonProperty("userInfo")
    private UserInfoDTO userInfoDTO;
}
