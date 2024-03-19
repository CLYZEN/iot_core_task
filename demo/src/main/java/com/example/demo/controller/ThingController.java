package com.example.demo.controller;

import com.example.demo.dto.request.DeviceInfoDTO;
import com.example.demo.dto.request.ThingDeleteRequestDTO;
import com.example.demo.dto.response.*;
import com.example.demo.service.AttrService;
import com.example.demo.service.CertificateService;
import com.example.demo.service.ThingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/thing")
public class ThingController {

    private final ThingService thingService;
    private final CertificateService certificateService;
    private final AttrService attrService;

    @PostMapping(value = "/create")
    @Transactional
    public ResponseEntity<Object> createThing(@RequestBody DeviceInfoDTO deviceInfoDTO) {

        if (deviceInfoDTO == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO().setResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Device Info is null"));
        }

        StringBuffer errorMessage = new StringBuffer();
        String thingName = deviceInfoDTO.getMaker() + "_" + deviceInfoDTO.getModel() + "_" + deviceInfoDTO.getSerial_number();
        ThingCreateResponseDTO thingCreateResponseDTO = new ThingCreateResponseDTO();
        IotClient iotClient = createIotClient();

        log.info("Iot Core Register Task Start");
        log.info("---------Get Device Info ----------");
        log.info(deviceInfoDTO.toString());
        log.info("---------Get Device Info ----------");

        try {
            boolean validation = thingService.validationThing(thingName, iotClient);
            if (!validation) {
                errorMessage.append("Thing with name")
                            .append(thingName)
                            .append(" already exists.");

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO().setResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),errorMessage.toString()));
            }

            CreateThingResponse createThingResponse = thingService.createThing(deviceInfoDTO, thingName,iotClient);
            CreateKeysAndCertificateResponse createKeysResponse = certificateService.attachKey(iotClient, thingName);

            log.info("Thing Attr Set Start");
            // 속성 설정
            AttributePayload attributePayload = attrService.getAttrPayload(deviceInfoDTO.getUserInfoDTO());
            attrService.setThingAttr(attributePayload,thingName,iotClient);
            log.info("Thing Attr Set Finish");

            thingCreateResponseDTO = (ThingCreateResponseDTO) new ThingCreateResponseDTO().setResponse(HttpStatus.OK.value(),"Success");
            thingCreateResponseDTO.setThingResponseAttr(createThingResponse);
            thingCreateResponseDTO.setCertificateResponseAttr(createKeysResponse);
            thingCreateResponseDTO.setCode(0);

            log.info("-------------------------------------------------");
            log.info("| 사물, 인증서 등록 완료");
            log.info("| 사물 이름: {}", thingName);
            log.info("| 인증서 ID: {}", thingCreateResponseDTO.getCertificate_id());
            log.info("-------------------------------------------------");

            log.info("Topic Initialize Start");
            // 토픽 규칙 관련
            log.info("Topic Initialize Start");

            log.info("DB Insert Start");
            // DB Insert 관련
            log.info("DB Insert Finish");

            log.info("Iot Core Register Task Finish");
            return ResponseEntity.status(HttpStatus.OK).body(thingCreateResponseDTO);
        } catch (Exception e) {
            certificateService.deleteKey(iotClient,thingName);
            thingService.deleteThing(iotClient,thingName);
            errorMessage.append("Error occurred while creating thing or certificate.");
            log.error(errorMessage.toString(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO().setResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),errorMessage.toString()));
        } finally {
            iotClient.close();
        }

    }


    @PostMapping(value = "/delete")
    @Transactional
    public ResponseEntity<Object> deleteThing(@RequestBody ThingDeleteRequestDTO thingDeleteRequestDTO) {
        String thingName = thingDeleteRequestDTO.getThingName();

        log.info("Thing Delete Task Start");
        try(IotClient iotClient = IotClient.create()){

            ArrayList<String> certificateIdList = certificateService.deleteKey(iotClient,thingName);
            String certificateIds = String.join(", ", certificateIdList);

            thingService.deleteThing(iotClient,thingName);

            log.info("Thing Delete Task Finish");
            log.info("-------------------------------------------------");
            log.info("| Thing, Certificate Delete OK");
            log.info("| ThingName: {}", thingName);
            log.info("| certificateId: {}", certificateIds);
            log.info("-------------------------------------------------");

            log.info("DB Delete Start");
            // DB Delete 관련
            log.info("DB Delete Finish");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO().setResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Certificate, Thing Delete Failed"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO().setResponse(HttpStatus.OK.value(), "Thing, Certificated Delete Success!"));
    }

    @GetMapping(value = "/list")
    public ResponseEntity<Object> getThingList(@RequestParam(required = false) String userId) {
        try (IotClient iotClient = createIotClient()) {
            ListThingsRequest listThingsRequest = ListThingsRequest.builder().build();
            ListThingsResponse listThingsResponse = iotClient.listThings(listThingsRequest);

            ArrayList<ThingInfoDTO> thingInfoDTOS = new ArrayList<>();

            for (ThingAttribute thing : listThingsResponse.things()) {
                DescribeThingRequest describeThingRequest = DescribeThingRequest.builder()
                        .thingName(thing.thingName())
                        .build();

                DescribeThingResponse describeThingResponse = iotClient.describeThing(describeThingRequest);

                Map<String, String> attributes = describeThingResponse.attributes();

                if (userId != null && attributes.containsKey("userId") && attributes.get("userId").equals(userId)) {
                    ThingInfoDTO thingInfoDTO = new ThingInfoDTO(thing, attributes);
                    thingInfoDTOS.add(thingInfoDTO);
                } else if (userId == null) {
                    ThingInfoDTO thingInfoDTO = new ThingInfoDTO(thing, attributes);
                    thingInfoDTOS.add(thingInfoDTO);
                }
            }

            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setResponseCode(HttpStatus.OK.value());
            responseDTO.setResponseMessage("List is retrieved successfully");

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("thingInfo", thingInfoDTOS);
            responseBody.put("response", responseDTO);

            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            responseDTO.setResponseMessage("List Is Null");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }


    public IotClient createIotClient() {
        return IotClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

}
