package com.example.demo.controller;

import com.example.demo.dto.DeviceInfoDTO;
import com.example.demo.dto.IoTThingDTO;
import com.example.demo.service.ThingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.*;

@RestController
@RequiredArgsConstructor
public class IndexController {

    private final ThingService thingService;

    @PostMapping(value = "/api/v1/thing/create")
    public ResponseEntity<IoTThingDTO> createThing(@RequestBody DeviceInfoDTO deviceInfoDTO) {
        String thingname = deviceInfoDTO.getModel() + "/" + deviceInfoDTO.getMaker() + "/" + deviceInfoDTO.getSerial_number();

        IotClient iotClient = IotClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        boolean validation = thingService.validationThing(thingname);

        if (!validation) {
            // Thing이 이미 존재하는 경우
            String errorMessage = "Thing with name " + thingname + " already exists.";
            iotClient.close();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new IoTThingDTO(errorMessage));
        }

        // Thing이 없으면 등록
        CreateKeysAndCertificateResponse createKeysResponse = iotClient.createKeysAndCertificate(CreateKeysAndCertificateRequest.builder().setAsActive(true).build());
        CreateThingResponse createThingResponse =
                iotClient
                        .createThing(
                                CreateThingRequest.builder()
                                        .thingName(thingname)
                                        .thingTypeName(deviceInfoDTO.getMaker())
                                        .build());

        // Thing에 인증서 연결
        iotClient.attachThingPrincipal(builder ->
                builder.thingName(createThingResponse.thingName())
                        .principal(createKeysResponse.certificateArn()));

        // 등록된 Thing과 인증서의 정보 출력
        IoTThingDTO ioTThingDTO = new IoTThingDTO(createThingResponse, createKeysResponse);

        iotClient.close();

        return ResponseEntity.status(HttpStatus.OK).body(ioTThingDTO);

    }

}
