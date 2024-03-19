package com.example.demo.controller;

import com.example.demo.dto.request.AttrUpdateRequestDTO;
import com.example.demo.dto.response.ResponseDTO;
import com.example.demo.service.AttrService;
import com.example.demo.service.ThingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.IotException;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/attr")
public class AttrController {

    private final ThingController thingController;
    private final AttrService attrService;
    private final ThingService thingService;
    @PostMapping("/update")
    public ResponseEntity<ResponseDTO> updateThingAttr(@RequestBody AttrUpdateRequestDTO attrUpdateRequestDTO) {

        try(IotClient iotClient = thingController.createIotClient()) {
            if (!thingService.validationThing(attrUpdateRequestDTO.getThingName(), iotClient)) {
                attrService.updateThingAttr(attrUpdateRequestDTO.getThingName(), attrUpdateRequestDTO.getThingAttr(),iotClient);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO().setResponse(HttpStatus.OK.value(), "Thing Attr Update Success!"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO().setResponse(HttpStatus.NOT_FOUND.value(), "Thing Not Found"));
            }

        } catch (IotException e) {
            log.error("ThingAttr Update Fail" + e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO().setResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "updateFail"));
        }
        //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error");
    }

}
