package com.cisco.cx.training.models;

import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuccesstalkUserRegEsSchema implements HasId {
    private final static Logger LOG = LoggerFactory.getLogger(SuccesstalkUserRegEsSchema.class);
    private final static String SMARTSHEET_DATE_FORMAT = "MM-dd-yy";

    @JsonAlias("Ccoid")
    private String ccoid;

    @JsonAlias("Attended")
    private AttendedStatusEnum attendedStatus;
    
    @JsonAlias("Registration Date/Time")
    private Long registrationDate;
    
    @JsonAlias("Registration Date Formatted")
    private String registrationDateFormatted;
    
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

    public SuccesstalkUserRegEsSchema(String title, Long eventStartDate, String ccoid, RegistrationStatusEnum registrationStatus) {
        this.ccoid = ccoid;
        this.eventStartDate = eventStartDate;
        this.title = title;
        this.registrationStatus = registrationStatus;
    }


	public AttendedStatusEnum getAttendedStatus() {
        return attendedStatus;
    }

    public void setAttendedStatus(AttendedStatusEnum attendedStatus) {
        this.attendedStatus = attendedStatus;
    }

	public String getCcoid() {
		return ccoid;
	}

	public void setCcoid(String ccoid) {
		this.ccoid = ccoid;
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
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        SuccesstalkUserRegEsSchema that = (SuccesstalkUserRegEsSchema) o;
        return getCcoid().equals(that.getCcoid()) && getTitle().equals(that.getTitle()) && getEventStartDate().equals(that.eventStartDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCcoid(), getTitle(),getEventStartDate());
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
    
    public enum AttendedStatusEnum {
        YES("Yes"),
        NO("No");

        private String value;

        AttendedStatusEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static SuccesstalkUserRegEsSchema.AttendedStatusEnum fromValue(String text) {
            for (SuccesstalkUserRegEsSchema.AttendedStatusEnum b : SuccesstalkUserRegEsSchema.AttendedStatusEnum.values()) {
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
        } catch (ParseException | NullPointerException | IllegalArgumentException e) {
            LOG.warn("Could not parse pattern {} from date value {}", SMARTSHEET_DATE_FORMAT, eventStartDateFormatted);
        }
	}

	public Long getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Long registrationDate) {
		this.registrationDate = registrationDate;
		this.registrationDateFormatted = (registrationDate != null) ? new SimpleDateFormat(SMARTSHEET_DATE_FORMAT).format(new Date(registrationDate)) : null;
	}
	

	public String getRegistrationDateFormatted() {
		return registrationDateFormatted;
	}

	public void setRegistrationDateFormatted(String registrationDateFormatted) {
		this.registrationDateFormatted = registrationDateFormatted;
		try {
			this.registrationDate = StringUtils.isNotBlank(registrationDateFormatted)
					? new SimpleDateFormat(SMARTSHEET_DATE_FORMAT).parse(registrationDateFormatted).getTime()
					: null;
		} catch (ParseException | NullPointerException | IllegalArgumentException e)  {
			LOG.warn("Could not parse pattern {} from date value {}", SMARTSHEET_DATE_FORMAT,
					registrationDateFormatted);
		}
	}
}
