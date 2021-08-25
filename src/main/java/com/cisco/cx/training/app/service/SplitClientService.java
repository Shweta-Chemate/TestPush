package com.cisco.cx.training.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cisco.cx.training.constants.Constants;

import io.split.client.SplitClient;

@Service
public class SplitClientService {

    private static final String BACKEND_SPLIT_IO_KEY = Constants.BE_SPLIT_IO_FLAG;
    private static final String SPLIT_IO_USER = "username"; //TODO : Need to know username

    @Autowired
    private SplitClient splitClient;

    public boolean useAuthZ() {
        String response = splitClient.getTreatment(SPLIT_IO_USER, BACKEND_SPLIT_IO_KEY);
        return (response.equalsIgnoreCase("on") || response.equalsIgnoreCase("true")) ? true : false;
    }
}