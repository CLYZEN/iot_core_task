package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.*;

import java.util.ArrayList;

@Service
@Slf4j
public class CertificateServiceImpl implements CertificateService{
    @Override
    public CreateKeysAndCertificateResponse attachKey(IotClient iotClient, String thingName) {
        CreateKeysAndCertificateResponse createKeysResponse = iotClient.createKeysAndCertificate(CreateKeysAndCertificateRequest.builder().setAsActive(true).build());

        AttachThingPrincipalRequest attachThingPrincipalRequest = AttachThingPrincipalRequest.builder()
                .thingName(thingName)
                .principal(createKeysResponse.certificateArn())
                .build();
        iotClient.attachThingPrincipal(attachThingPrincipalRequest);

        return createKeysResponse;
    }

    @Override
    public ArrayList<String> deleteKey(IotClient iotClient, String thingName) {
        ArrayList<String> certificateIdList = new ArrayList<>();

        log.info("principal delete start");
        // Thing에 연결된 인증서 확인
        ListThingPrincipalsRequest listThingPrincipalsRequest = ListThingPrincipalsRequest.builder()
                .thingName(thingName)
                .build();
        ListThingPrincipalsResponse listThingPrincipalsResponse = iotClient.listThingPrincipals(listThingPrincipalsRequest);

        log.info("principal delete for loop");
        // 모든 연결된 인증서 제거
        for (String principal : listThingPrincipalsResponse.principals()) {
            String certificateId = principal.substring(principal.lastIndexOf("/") + 1);
            DetachThingPrincipalRequest detachThingPrincipalRequest = DetachThingPrincipalRequest.builder()
                    .thingName(thingName)
                    .principal(principal)
                    .build();
            iotClient.detachThingPrincipal(detachThingPrincipalRequest);
        }
        log.info("principal delete end");

        log.info("certificate delete start");
        // 모든 연결된 인증서 삭제
        ListCertificatesRequest listCertificatesRequest = ListCertificatesRequest.builder()
                .build();
        ListCertificatesResponse listCertificatesResponse = iotClient.listCertificates(listCertificatesRequest);
        for (String principal : listThingPrincipalsResponse.principals()) {
            String certificateId = principal.substring(principal.lastIndexOf("/") + 1);
            UpdateCertificateRequest updateCertificateRequest = UpdateCertificateRequest.builder()
                    .certificateId(certificateId)
                    .newStatus("INACTIVE")
                    .build();
            certificateIdList.add(certificateId);
            iotClient.updateCertificate(updateCertificateRequest);

            // 인증서 삭제 작업 수행
            DeleteCertificateRequest deleteCertificateRequest = DeleteCertificateRequest.builder()
                    .certificateId(certificateId)
                    .build();
            iotClient.deleteCertificate(deleteCertificateRequest);
        }
        log.info("certificate delete ok");

        return certificateIdList;
    }
}
