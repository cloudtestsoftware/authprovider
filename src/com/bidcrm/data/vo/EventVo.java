package com.bidcrm.data.vo;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import com.bidcrm.data.vo.IcalUnit.RecurrenceInMonth;
import com.bidcrm.data.vo.IcalUnit.RecurrenceInWeek;
import com.bidcrm.data.vo.IcalUnit.RecurrenceType;
import com.bidcrm.data.vo.IcalUnit.RecurrenceUnit;


public class EventVo {

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(Date reminderDate) {
		this.reminderDate = reminderDate;
	}

	public boolean isAllDay() {
		return allDay;
	}

	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUnknownParticipiants() {
		return unknownParticipiants;
	}

	public void setUnknownParticipiants(String unknownParticipiants) {
		this.unknownParticipiants = unknownParticipiants;
	}

	public RecurrenceType getRecurrenceType() {
		return recurrenceType;
	}

	public void setRecurrenceType(RecurrenceType recurrenceType) {
		this.recurrenceType = recurrenceType;
	}

	public Date getRecurrenceStartDate() {
		return recurrenceStartDate;
	}

	public void setRecurrenceStartDate(Date recurrenceStartDate) {
		this.recurrenceStartDate = recurrenceStartDate;
	}

	public Date getRecurrenceEndDate() {
		return recurrenceEndDate;
	}

	public void setRecurrenceEndDate(Date recurrenceEndDate) {
		this.recurrenceEndDate = recurrenceEndDate;
	}

	public Integer getRecurrenceNumberOfTimes() {
		return recurrenceNumberOfTimes;
	}

	public void setRecurrenceNumberOfTimes(Integer recurrenceNumberOfTimes) {
		this.recurrenceNumberOfTimes = recurrenceNumberOfTimes;
	}

	public Integer getRecurrenceCycle() {
		return recurrenceCycle;
	}

	public void setRecurrenceCycle(Integer recurrenceCycle) {
		this.recurrenceCycle = recurrenceCycle;
	}

	public RecurrenceUnit getRecurrenceCycleUnit() {
		return recurrenceCycleUnit;
	}

	public void setRecurrenceCycleUnit(RecurrenceUnit recurrenceCycleUnit) {
		this.recurrenceCycleUnit = recurrenceCycleUnit;
	}

	public Set<RecurrenceInWeek> getRecurrenceInWeek() {
		return recurrenceInWeek;
	}

	public void setRecurrenceInWeek(Set<RecurrenceInWeek> recurrenceInWeek) {
		this.recurrenceInWeek = recurrenceInWeek;
	}

	public RecurrenceInMonth getRecurrenceInMonth() {
		return recurrenceInMonth;
	}

	public void setRecurrenceInMonth(RecurrenceInMonth recurrenceInMonth) {
		this.recurrenceInMonth = recurrenceInMonth;
	}

	public Date getRecurrenceUntilDate() {
		return recurrenceUntilDate;
	}

	public void setRecurrenceUntilDate(Date recurrenceUntilDate) {
		this.recurrenceUntilDate = recurrenceUntilDate;
	}
	 public Date getCreateDate() {
			return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	 public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	private String title;

    private String description;

    private Date startDate;

    private Date endDate;
    
    private Date createDate;

    private Date modifyDate;

	private Date reminderDate;

    private boolean allDay;

    private boolean occupied;

    private String location;
    
    private String unknownParticipiants;
    
    private RecurrenceType recurrenceType;

    private Date recurrenceStartDate;

    private Date recurrenceEndDate;

    private Integer recurrenceNumberOfTimes;

    private Integer recurrenceCycle;

    private RecurrenceUnit recurrenceCycleUnit;

    private Set<RecurrenceInWeek> recurrenceInWeek = new LinkedHashSet<RecurrenceInWeek>();

    private RecurrenceInMonth recurrenceInMonth;
    
    private Date recurrenceUntilDate;
    
	private Integer Id;


}
