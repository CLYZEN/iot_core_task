package com.example.demo.service;

import com.example.demo.dto.DeviceInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.DescribeThingRequest;
import software.amazon.awssdk.services.iot.model.DescribeThingResponse;
import software.amazon.awssdk.services.iot.model.ResourceNotFoundException;

@Service
@Slf4j
public class ThingService {

    public boolean validationThing(String thingName) {
        IotClient iotClient = IotClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        try {
            DescribeThingResponse describeThingResponse = iotClient.describeThing(DescribeThingRequest.builder().thingName(thingName).build());
            log.debug("validation thing ok");
            return describeThingResponse.thingId().isEmpty();
        } catch (ResourceNotFoundException e) {
            return true;
        } finally {
            iotClient.close();
        }

    }

}
