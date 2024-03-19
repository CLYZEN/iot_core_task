package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;

@Getter
@Setter
@ToString
public class AttrUpdateRequestDTO {
    private String thingName;

    private String userId;

    private HashMap<String,String> thingAttr;
}
