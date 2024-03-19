package com.example.demo.service;

import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.AttributePayload;

import java.util.HashMap;

public interface AttrService {

    AttributePayload getAttrPayload(Object obj);

    void setThingAttr(AttributePayload attributePayload, String thingName, IotClient iotClient);

    void updateThingAttr(String thingName, HashMap<String,String> attributes, IotClient iotClient);
}
