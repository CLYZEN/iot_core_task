package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.services.iot.model.ThingAttribute;

import java.util.Map;

@Getter
@Setter
@ToString
public class ThingInfoDTO {

    private String thingName;

    private String thingArn;

    private String thingTypeName;

    private Map<String,String> thingAttr;

    public ThingInfoDTO(ThingAttribute thing, Map<String, String> attributes) {
        this.thingArn = thing.thingArn();
        this.thingName = thing.thingName();
        this.thingTypeName = thing.thingTypeName();
        this.thingAttr = attributes;
    }

}
