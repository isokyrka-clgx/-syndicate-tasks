package com.task10.response;

public class SaveReservationResponse {

    private String reservationId;

    public SaveReservationResponse(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

}
