package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SuccesstalkRegSmartSheetSchema {

    @JsonProperty("Event Name")
    private String title;

    @JsonProperty("Email") //mapped to ccoid
    private String email;

    @JsonProperty("FirstName")
    private String firstname;

    @JsonProperty("LastName")
    private String lastname;

    @JsonProperty("Registered")
    private SuccesstalkUserRegEsSchema.RegistrationStatusEnum registrationStatus;
    
    @JsonProperty("Attended")
    private SuccesstalkUserRegEsSchema.AttendedStatusEnum attendedStatus;
    
    @JsonProperty("Registration Date/Time")
    private Long registrationDateTime;    
    
    @JsonProperty("Event Start Date")
    private String eventStartDate;  
    
    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


    public SuccesstalkUserRegEsSchema.RegistrationStatusEnum getTransactionType() {
        return registrationStatus;
    }

    public void setTransactionType(SuccesstalkUserRegEsSchema.RegistrationStatusEnum registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public SuccesstalkUserRegEsSchema.AttendedStatusEnum getAttendedStatus() {
        return attendedStatus;
    }

    public void setAttendedStatus(SuccesstalkUserRegEsSchema.AttendedStatusEnum attendedStatus) {
        this.attendedStatus = attendedStatus;
    }

	public Long getRegistrationDateTime() {
		return registrationDateTime;
	}

	public void setRegistrationDateTime(Long registrationDateTime) {
		this.registrationDateTime = registrationDateTime;
	}

	public String getEventStartDate() {
		return eventStartDate;
	}

	public void setEventStartDate(String eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	
}
