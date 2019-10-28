package com.cisco.cx.training.models;

import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuccesstalkUserRegEsSchema implements HasId {
    private final static Logger LOG = LoggerFactory.getLogger(SuccesstalkUserRegEsSchema.class);
    private final static String SMARTSHEET_DATE_FORMAT = "EEEEE MMMMM dd yyyy HH:mm:ss.SSSZ";

    @JsonAlias("Email")
    private String email;
    
    @JsonAlias("FirstName")
    private String firstname;

    @JsonAlias("LastName")
    private String lastname;

    @JsonAlias("Attended")
    private String attended;
    
    @JsonAlias("Registration Date/Time")
    private Long registrationDateTime;       
    
    @JsonAlias("Event Start Date")
    private Long eventStartDate;

    @JsonAlias("Event Start Date Formatted")
    private String eventStartDateFormatted;
    
    @JsonAlias("Event Name")
    private String title;

    @JsonAlias("Registered")
    private RegistrationStatusEnum registrationStatus=RegistrationStatusEnum.PENDING;
    

    private Long created = System.currentTimeMillis();

    private Long updated;


    public SuccesstalkUserRegEsSchema() { }

    public SuccesstalkUserRegEsSchema(String title, String email, RegistrationStatusEnum registrationStatus) {
        this.email = email;
        this.title = title;
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

	public String getAttended() {
		return attended;
	}

	public void setAttended(String attended) {
		this.attended = attended;
	}

	public Long getRegistrationDateTime() {
		return registrationDateTime;
	}

	public void setRegistrationDateTime(Long registrationDateTime) {
		this.registrationDateTime = registrationDateTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public RegistrationStatusEnum getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatusEnum registrationStatus) {
        this.registrationStatus = registrationStatus;
    }


    public Long getCreated() { return created; }

    public Long getUpdated() { return updated; }

    public void setUpdated(Long updated) { this.updated = updated; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SuccesstalkUserRegEsSchema that = (SuccesstalkUserRegEsSchema) o;
        return getEmail().equals(that.getEmail()) && getTitle().equals(that.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getTitle());
    }

    @Override
    public String getDocId() {
        return String.valueOf(this.hashCode());
    }

    @Override
    public void setDocId(String id) {
        //ignore. id is calculated from hashCode
    }

    public enum RegistrationStatusEnum {
        PENDING("Pending"),
        REGISTERED("Registered"),
        CANCELLED("Cancelled"),
        REGISTERFAILED("Register_Failed"),
        CANCELFAILED("Cancel_Failed");

        private String value;

        RegistrationStatusEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static SuccesstalkUserRegEsSchema.RegistrationStatusEnum fromValue(String text) {
            for (SuccesstalkUserRegEsSchema.RegistrationStatusEnum b : SuccesstalkUserRegEsSchema.RegistrationStatusEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

	public Long getEventStartDate() {
		return eventStartDate;
	}

	public void setEventStartDate(Long eventStartDate) {
		this.eventStartDate = eventStartDate;
		this.eventStartDateFormatted = (eventStartDate != null) ? new SimpleDateFormat(SMARTSHEET_DATE_FORMAT).format(new Date(eventStartDate)) : null;
	}

	public String getEventStartDateFormatted() {
		return eventStartDateFormatted;
	}

	public void setEventStartDateFormatted(String eventStartDateFormatted) {
        this.eventStartDateFormatted = eventStartDateFormatted;
        try {
            this.eventStartDate = StringUtils.isNotBlank(eventStartDateFormatted) ? new SimpleDateFormat(SMARTSHEET_DATE_FORMAT).parse(eventStartDateFormatted).getTime() : null;
        } catch (Exception e) {
            LOG.warn("Could not parse pattern {} from date value {}", SMARTSHEET_DATE_FORMAT, eventStartDateFormatted);
        }
	}
}
