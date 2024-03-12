package com.example.demo.service;

import com.example.demo.dto.DeviceInfoDTO;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.CreateThingResponse;

public interface ThingService {

    boolean validationThing(String thingName,IotClient IotClient);

    CreateThingResponse createThing(DeviceInfoDTO deviceInfoDTO, String thingName,IotClient iotClient);

    void deleteThing(IotClient iotClient, String thingName);

}
