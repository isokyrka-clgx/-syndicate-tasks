package com.task10.response;

import com.task10.model.Reservation;

import java.util.List;
import java.util.stream.Collectors;

public class ReservationResponse {
	private Integer tableNumber;
	private String clientName;
	private String phoneNumber;
	private String date;
	private String slotTimeStart;
	private String slotTimeEnd;

	public ReservationResponse() {
	}

	public ReservationResponse(Integer tableNumber, String clientName, String phoneNumber, String date,
			String slotTimeStart, String slotTimeEnd) {
		this.tableNumber = tableNumber;
		this.clientName = clientName;
		this.phoneNumber = phoneNumber;
		this.date = date;
		this.slotTimeStart = slotTimeStart;
		this.slotTimeEnd = slotTimeEnd;
	}

	public static List<ReservationResponse> fromReservationModel(List<Reservation> reservations) {
		return reservations.stream()
				.map(r -> new ReservationResponse(r.getTableNumber(), r.getClientName(), r.getPhoneNumber(), r.getDate(), r.getSlotTimeStart(), r.getSlotTimeEnd()))
				.collect(Collectors.toList());
	}

	public Integer getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(Integer tableNumber) {
		this.tableNumber = tableNumber;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSlotTimeStart() {
		return slotTimeStart;
	}

	public void setSlotTimeStart(String slotTimeStart) {
		this.slotTimeStart = slotTimeStart;
	}

	public String getSlotTimeEnd() {
		return slotTimeEnd;
	}

	public void setSlotTimeEnd(String slotTimeEnd) {
		this.slotTimeEnd = slotTimeEnd;
	}
}
