package com.task10.response;

import java.util.List;

public class ReservationsResponse {
	private List<ReservationResponse> reservations;

	public ReservationsResponse() {
	}

	public ReservationsResponse(List<ReservationResponse> reservations) {
		this.reservations = reservations;
	}

	public List<ReservationResponse> getReservations() {
		return reservations;
	}

	public void setReservations(List<ReservationResponse> reservations) {
		this.reservations = reservations;
	}
}
