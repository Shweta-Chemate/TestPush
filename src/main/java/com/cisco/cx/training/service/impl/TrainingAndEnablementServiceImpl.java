package com.cisco.cx.training.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cisco.cx.training.models.SuccessTrackAndUseCases;
import com.cisco.cx.training.service.TrainingAndEnablementService;

@Service
public class TrainingAndEnablementServiceImpl implements TrainingAndEnablementService {

	@Override
	public SuccessTrackAndUseCases getUsecases() {
		Map<String, List<String>> useCases = new HashMap<String, List<String>>();
		useCases.put("IBN", new ArrayList<>(Arrays.asList("Campus Network Assurance", "Network Device Onboarding",
				"Campus Software Image management", "Campus Network Segmentation", "Scalable Access Policy")));
		SuccessTrackAndUseCases successTrackAndUseCases = new SuccessTrackAndUseCases();
		successTrackAndUseCases.setUseCases(useCases);
		return successTrackAndUseCases;
	}

}