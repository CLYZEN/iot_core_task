package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ThingDeleteRequestDTO {

    private String thingName;

    private String userId;
}
