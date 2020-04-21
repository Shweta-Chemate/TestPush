package com.cisco.cx.training.app.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.cx.training.app.entities.SuccessAcademyLearningEntity;

public interface SuccessAcademyDAO extends JpaRepository<SuccessAcademyLearningEntity, String>{
		
}