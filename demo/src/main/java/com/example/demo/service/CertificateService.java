package com.example.demo.service;

import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.CreateKeysAndCertificateResponse;

import java.util.ArrayList;

public interface CertificateService {

    CreateKeysAndCertificateResponse attachKey(IotClient iotClient, String thingName);

    ArrayList<String> deleteKey(IotClient iotClient , String thingName);
}
