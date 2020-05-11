package com.vegga.airline.service;

import com.vegga.airline.entity.AirlineReservation;
import com.vegga.airline.repository.AirlineReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AirlineService {

    @Autowired
    private AirlineReservationRepository airlineReservationRepository;

    public AirlineService(AirlineReservationRepository airlineReservationRepository) {
        this.airlineReservationRepository = airlineReservationRepository;
    }

    public void save(Long flightId, Long clientId, Long seatId) {

        AirlineReservation reservation = new AirlineReservation();
        reservation.setFlightId(flightId);
        reservation.setClientId(clientId);
        reservation.setSeatId(seatId);

        airlineReservationRepository.save(reservation);
    }

    public void abortSaving(Long id) {

        airlineReservationRepository.delete(airlineReservationRepository.getOne(id));
    }
}
