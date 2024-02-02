package com.task10.response;

import com.task10.model.Reservation;
import java.util.ArrayList;
import java.util.List;

public class GetReservationsResponse {

    private List<Reservation> reservations = new ArrayList<>();

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

}
