package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class ThingInfoDTO {

    private String thingName;

    private String thingArn;

    private String thingTypeName;

    private Map<String,String> thingAttr;
}
