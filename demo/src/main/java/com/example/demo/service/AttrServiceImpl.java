package com.example.demo.service;

import com.example.demo.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.AttributePayload;
import software.amazon.awssdk.services.iot.model.UpdateThingRequest;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttrServiceImpl implements AttrService {
    @Override
    public AttributePayload getAttrPayload(Object obj) {

        AttributePayload.Builder attributePayloadBuilder = AttributePayload.builder();

        Map<String, String> attributesMap = new HashMap<>();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String fieldName = field.getName();
                String fieldValue = getArgsFromParam(fieldName, obj);
                attributesMap.put(fieldName, fieldValue);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        attributePayloadBuilder.attributes(attributesMap);
        return attributePayloadBuilder.build();
    }

    @Override
    public void setThingAttr(AttributePayload attributePayload, String thingName, IotClient iotClient) {
        UpdateThingRequest request = UpdateThingRequest.builder()
                .thingName(thingName)
                .attributePayload(attributePayload)
                .build();

        iotClient.updateThing(request);
    }


    private static String getArgsFromParam(String fieldName, Object... args) {
        String resArgs = "";
        try {
            for (Object arg : args) {
                if (arg != null) {
                    if (arg instanceof Object) {
                        Field field = ReflectionUtils.findField(arg.getClass(), fieldName);
                        field.setAccessible(true);
                        if (ReflectionUtils.getField(field, arg) instanceof Integer) {
                            resArgs = ReflectionUtils.getField(field, arg).toString();
                        } else {
                            resArgs = (String) ReflectionUtils.getField(field, arg);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resArgs;
    }
}
