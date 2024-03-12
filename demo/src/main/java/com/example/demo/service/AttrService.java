package com.example.demo.service;

import com.example.demo.dto.UserInfoDTO;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.AttributePayload;

public interface AttrService {

    AttributePayload getAttrPayload(Object obj);

    void setThingAttr(AttributePayload attributePayload, String thingName, IotClient iotClient);
}
