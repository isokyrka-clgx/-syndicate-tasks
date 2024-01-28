package com.task10.response;

public class ReservationCreatedResponse {
	private String reservationId;

	public ReservationCreatedResponse() {
	}

	public ReservationCreatedResponse(String reservationId) {
		this.reservationId = reservationId;
	}

	public String getReservationId() {
		return reservationId;
	}

	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}
}
