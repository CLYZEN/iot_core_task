package com.example.demo.service;

import com.example.demo.dto.DeviceInfoDTO;
import com.example.demo.dto.ErrorMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ThingServiceImpl implements ThingService{

    private final CertificateService certificateService;

    public boolean validationThing(String thingName, IotClient iotClient) {
        log.info("Validation Check Start");
        try {
            DescribeThingResponse describeThingResponse = iotClient.describeThing(DescribeThingRequest.builder().thingName(thingName).build());
            log.info("validation thing ok");
            return describeThingResponse.thingId().isEmpty();
        } catch (ResourceNotFoundException e) {
            return true;
        }
    }

    public CreateThingResponse createThing(DeviceInfoDTO deviceInfoDTO, String thingName,IotClient iotClient) {

        log.info("Thing Create Start");
        CreateThingResponse createThingResponse =
                iotClient
                        .createThing(
                                CreateThingRequest.builder()
                                        .thingName(thingName)
                                        //.thingTypeName(deviceInfoDTO.getMaker())
                                        .build());

        return createThingResponse;
    }

    @Override
    public void deleteThing(IotClient iotClient, String thingName) {
        log.info("thing delete start");
        // Thing 삭제 요청 생성 및 전송
        DeleteThingRequest deleteThingRequest = DeleteThingRequest.builder()
                .thingName(thingName)
                .build();
        iotClient.deleteThing(deleteThingRequest);
        log.info("thing delete ok");
    }

}
