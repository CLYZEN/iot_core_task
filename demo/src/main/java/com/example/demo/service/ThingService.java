package com.example.demo.service;

import com.example.demo.dto.request.DeviceInfoDTO;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.CreateThingResponse;
import software.amazon.awssdk.services.iot.model.DescribeThingResponse;

public interface ThingService {

    boolean validationThing(String thingName, IotClient IotClient);

    CreateThingResponse createThing(DeviceInfoDTO deviceInfoDTO, String thingName,IotClient iotClient);

    void deleteThing(IotClient iotClient, String thingName);

    DescribeThingResponse getThingForName(String thingName, IotClient iotClient);

}
