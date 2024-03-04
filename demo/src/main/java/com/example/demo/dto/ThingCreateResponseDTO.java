package com.example.demo.dto;

import lombok.*;
import software.amazon.awssdk.services.iot.model.CreateKeysAndCertificateResponse;
import software.amazon.awssdk.services.iot.model.CreateThingResponse;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ThingCreateResponseDTO {

    private String thing_arn;
    private String certificate_arn;
    private String certificate_id;
    private String certificate_pem;
    private String key_private_pem;
    private String key_public_pem;
    private Integer responseCode;
    private Integer code;

    public void setThingAttr(CreateThingResponse createThingResponse) {
        this.thing_arn = createThingResponse.thingArn();
    }

    public void setCertificateAttr(CreateKeysAndCertificateResponse createKeysResponse) {
        this.certificate_arn = createKeysResponse.certificateArn();
        this.certificate_id = createKeysResponse.certificateId();
        this.certificate_pem = createKeysResponse.certificatePem();
        this.key_private_pem = createKeysResponse.keyPair().privateKey();
        this.key_public_pem = createKeysResponse.keyPair().publicKey();
    }
}