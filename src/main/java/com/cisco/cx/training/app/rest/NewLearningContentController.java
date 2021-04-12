package com.cisco.cx.training.app.rest;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Validated
@RequestMapping("/v1/partner/new/learning")
@Api(value = "New Learning Content APIs")
public class NewLearningContentController {

}
