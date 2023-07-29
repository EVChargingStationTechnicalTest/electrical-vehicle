package com.api.rest.service;

import com.api.rest.model.ChargingStation;

import java.util.List;
import java.util.Optional;

public interface ChargingStationService {

    ChargingStation saveChargingStation(ChargingStation chargingStation);

    List<ChargingStation> getAllChargingStations();

    Optional<ChargingStation> getChargingStationById(String id);

    ChargingStation updateChargingStation(ChargingStation chargingStationUpdated);

    void deleteChargingStation(String id);

}
