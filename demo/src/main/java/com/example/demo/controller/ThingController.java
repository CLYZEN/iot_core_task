package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.CertificateService;
import com.example.demo.service.ThingService;
import com.example.demo.service.ThingServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.*;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ThingController {

    private final ThingService thingService;
    private final CertificateService certificateService;

    @PostMapping(value = "/api/v1/thing/create")
    public ResponseEntity<Object> createThing(@RequestBody DeviceInfoDTO deviceInfoDTO) {

        String thingName = deviceInfoDTO.getMaker() + "_" + deviceInfoDTO.getModel() + "_" + deviceInfoDTO.getSerial_number();
        ThingCreateResponseDTO thingCreateResponseDTO = new ThingCreateResponseDTO();

        log.info("Iot Core Register Task Start");
        log.info("---------get device info ----------");
        log.info(deviceInfoDTO.toString());
        log.info("---------get device info ----------");

        try (IotClient iotClient = createIotClient()) {
            boolean validation = thingService.validationThing(thingName, iotClient);
            if (!validation) {
                String errorMessage = "Thing with name " + thingName + " already exists.";
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessageDTO(errorMessage));
            }

            CreateThingResponse createThingResponse = thingService.createThing(deviceInfoDTO, thingName);
            thingCreateResponseDTO.setThingAttr(createThingResponse);

            CreateKeysAndCertificateResponse createKeysResponse = certificateService.attachKey(iotClient, thingName);
            thingCreateResponseDTO.setCertificateAttr(createKeysResponse);
            thingCreateResponseDTO.setCode(0);
            thingCreateResponseDTO.setResponseCode(200);

            log.info("-------------------------------------------------");
            log.info("| 사물, 인증서 등록 완료");
            log.info("| 사물 이름: {}", thingName);
            log.info("| 인증서 ID: {}", thingCreateResponseDTO.getCertificate_id());
            log.info("-------------------------------------------------");

            log.info("DB Insert Start");
            // DB Insert 관련
            log.info("DB Insert Finish");

            log.info("Iot Core Register Task Finish");
        } catch (Exception e) {
            String errorMessage = "Error occurred while creating thing or certificate.";
            log.error(errorMessage, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessageDTO(errorMessage));
        }

        return ResponseEntity.status(HttpStatus.OK).body(thingCreateResponseDTO);

    }


    @PostMapping(value = "api/v1/thing/delete")
    public ResponseEntity<Object> deleteThing(@RequestBody ThingDeleteRequestDTO thingDeleteRequestDTO) {
        String thingName = thingDeleteRequestDTO.getThingName();
        ThingDeleteResponseDTO thingDeleteResponseDTO = new ThingDeleteResponseDTO();

        log.info("thing delete task start");

        try(IotClient iotClient = IotClient.create()){

            ArrayList<String> certificateIdList = certificateService.deleteKey(iotClient,thingName);
            String certificateIds = String.join(", ", certificateIdList);

            thingService.deleteThing(iotClient,thingName);

            thingDeleteResponseDTO.setCode(0);
            thingDeleteResponseDTO.setResponseCode(200);
            thingDeleteResponseDTO.setMessage(thingName + " Delete OK!!");
            log.info("thing delete task finish");
            log.info("-------------------------------------------------");
            log.info("| 사물, 인증서 삭제 완료");
            log.info("| 사물 이름: {}", thingName);
            log.info("| 인증서 ID: {}", certificateIds);
            log.info("-------------------------------------------------");

            log.info("DB Delete Start");
            // DB Delete 관련
            log.info("DB Delete Finish");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessageDTO("인증서, 사물 삭제에 실패하였습니다."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(thingDeleteResponseDTO);
    }


    private IotClient createIotClient() {
        return IotClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
