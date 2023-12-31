package com.api.rest.repository;

import com.api.rest.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChargingStationRepositoryTests {

    @Autowired
    private ChargingStationRepository chargingStationRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    private ChargingStation chargingStation;

    private ChargingStationLocation chargingStationLocation;

    private Set<ChargingStationPoint> chargingStationPoints;

    @BeforeEach
    void setup(){

        /*        chargingStationPoints                */
        chargingStationPoints = new HashSet<>();
        chargingStationPoints.add(ChargingStationPoint.builder()
                .chargingPointId(UUID.randomUUID())
                .powerLevel(20000)
                .status(ChargingStatus.IN_USE.getValue())
                .build()
        );
        chargingStationPoints.add(ChargingStationPoint.builder()
                .chargingPointId(UUID.randomUUID())
                .powerLevel(100000)
                .status(ChargingStatus.AVAILABLE.getValue())
                .build()
        );
        /*        chargingStationPoints                */

        /*        chargingStationLocation                */
        List<ChargingStationLocation> chargingLocations = new ArrayList<>();
        chargingLocations.add(new ChargingStationLocation());
        try {
            Gson gson = new Gson();
            File dataFile = resourceLoader.getResource("classpath:data/JsonPrueba.json").getFile();

            Type listType = new TypeToken<ArrayList<ChargingStationLocation>>() { }.getType();
            Reader reader1 = new FileReader(dataFile);
            chargingLocations = gson.fromJson(reader1, listType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        chargingStationLocation = chargingLocations.get(0);
        /*        chargingStationLocation                */

        chargingStation = ChargingStation.builder()
                .chargingStationPoints(chargingStationPoints)
                .chargingPointsAmount(chargingStationPoints.size())
                .chargingStatus((chargingStationPoints.size() > 0) ? ChargingStatus.AVAILABLE.getValue() : ChargingStatus.UNAVAILABLE.getValue())
                .chargingStationType(ChargingStationType.AC.getValue())
                .chargingPointsAmount(0)
                .chargingStationLocation(chargingStationLocation)
                .build();
    }

    @DisplayName("Save ChargingStation")
    @Test
    void testSaveChargingStation(){

        //given - dado o condición previa o configuración
        ChargingStation chargingStationForSave = ChargingStation.builder()
                .chargingStationPoints(chargingStationPoints)
                .chargingPointsAmount(chargingStationPoints.size())
                .chargingStatus((chargingStationPoints.size() > 0) ? ChargingStatus.AVAILABLE.getValue() : ChargingStatus.UNAVAILABLE.getValue())
                .chargingStationType(ChargingStationType.AC.getValue())
                .chargingPointsAmount(0)
                .chargingStationLocation(chargingStationLocation)
                .build();

        //when - acción o el comportamiento que vamos a probar
        ChargingStation savedChargingStation = chargingStationRepository.save(chargingStationForSave);

        //then - verificar la salida
        assertThat(savedChargingStation).isNotNull();
        assertThat(savedChargingStation.getChargingStationId()).isNotNull();

    }

    @DisplayName("Get ChargingStation by ID")
    @Test
    void testGetChargingStationByID(){
        chargingStationRepository.save(chargingStation);

        //when - comportamiento o accion que vamos a probar
        ChargingStation chargingStationDB = chargingStationRepository.findById(chargingStation.getChargingStationId()).get();

        //then
        assertThat(chargingStationDB).isNotNull();
    }

    @DisplayName("GET :: Status of ChargingStation by ID")
    @Test
    void testGetChargingStationStatusByID(){
        chargingStationRepository.save(chargingStation);

        //when - comportamiento o accion que vamos a probar
        String chargingStationStatusDB = String.valueOf(chargingStationRepository.findBychargingStationId(chargingStation.getChargingStationId()));

        //then
        assertThat(chargingStationStatusDB).isNotNull();
    }

    @DisplayName("Update ChargingStation")
    @Test
    void testForUpdateChargingStation(){
        //given - dado o condición previa o configuración
        ChargingStationLocationAddress chargingStationLocationAddress = ChargingStationLocationAddress.builder()
                .state(chargingStationLocation.getAddress().getState())
                .stateCode(chargingStationLocation.getAddress().getStateCode())
                .city(chargingStationLocation.getAddress().getCity())
                .county(chargingStationLocation.getAddress().getCounty())
                .countryCode(chargingStationLocation.getAddress().getCountryCode())
                .countryName(chargingStationLocation.getAddress().getCountryName())
                .district(chargingStationLocation.getAddress().getDistrict())
                .postalCode(chargingStationLocation.getAddress().getPostalCode())
                .label(chargingStationLocation.getAddress().getLabel())
                .build();

        ChargingStation chargingStationForUpdate = ChargingStation.builder()
                .chargingStationPoints(chargingStationPoints)
                .chargingPointsAmount(chargingStationPoints.size())
                .chargingStatus((chargingStationPoints.size() > 0) ? ChargingStatus.AVAILABLE.getValue() : ChargingStatus.UNAVAILABLE.getValue())
                .chargingStationType(ChargingStationType.AC.getValue())
                .chargingPointsAmount(0)
                .chargingStationLocation(chargingStationLocation)
                .build();
        chargingStationRepository.save(chargingStationForUpdate);

        //when
        ChargingStation chargingStationUpdated = chargingStationRepository.findById(chargingStationForUpdate.getChargingStationId()).get();
        chargingStationUpdated.setChargingStationType(ChargingStationType.DC.getValue());
        chargingStationUpdated.setChargingStatus(ChargingStatus.IN_USE.getValue());
        ChargingStation chargingStationResult = chargingStationRepository.save(chargingStationUpdated);

        //then
        assertThat(chargingStationResult.getChargingStationType()).isEqualTo(ChargingStationType.DC.getValue());
        assertThat(chargingStationResult.getChargingStatus()).isEqualTo(ChargingStatus.IN_USE.getValue());
    }

    @DisplayName("Get all ChargingStations")
    @Test
    void testListChargingStations(){
        //given
        ChargingStationLocation ChargingStationLocationCopy = ChargingStationLocation.builder()
                .id(chargingStationLocation.getId())
                .title(chargingStationLocation.getTitle())
                .address(chargingStationLocation.getAddress())
                .distance(chargingStationLocation.getDistance())
                .language(chargingStationLocation.getLanguage())
                .position(chargingStationLocation.getPosition())
                .resultType(chargingStationLocation.getResultType())
                .build();

        ChargingStation chargingStationForList = ChargingStation.builder()
                .chargingStationPoints(chargingStationPoints)
                .chargingPointsAmount(chargingStationPoints.size())
                .chargingStatus((chargingStationPoints.size() > 0) ? ChargingStatus.AVAILABLE.getValue() : ChargingStatus.UNAVAILABLE.getValue())
                .chargingStationType(ChargingStationType.AC.getValue())
                .chargingPointsAmount(0)
                .chargingStationLocation(ChargingStationLocationCopy)
                .build();
        chargingStationRepository.save(chargingStationForList);
        chargingStationRepository.save(chargingStation);

        //when
        List<ChargingStation> chargingStationList = chargingStationRepository.findAll();

        //then
        assertThat(chargingStationList).isNotNull();
        assertThat(chargingStationList.size()).isEqualTo(2);
    }

    @DisplayName("Get all ChargingStations by Status")
    @Test
    void testListChargingStationsbyStatus(){
        //given
        ChargingStationLocation ChargingStationLocationCopy = ChargingStationLocation.builder()
                .id(chargingStationLocation.getId())
                .title(chargingStationLocation.getTitle())
                .address(chargingStationLocation.getAddress())
                .distance(chargingStationLocation.getDistance())
                .language(chargingStationLocation.getLanguage())
                .position(chargingStationLocation.getPosition())
                .resultType(chargingStationLocation.getResultType())
                .build();

        ChargingStation chargingStationForList = ChargingStation.builder()
                .chargingStationPoints(chargingStationPoints)
                .chargingPointsAmount(chargingStationPoints.size())
                .chargingStatus((chargingStationPoints.size() > 0) ? ChargingStatus.AVAILABLE.getValue() : ChargingStatus.UNAVAILABLE.getValue())
                .chargingStationType(ChargingStationType.AC.getValue())
                .chargingPointsAmount(0)
                .chargingStationLocation(ChargingStationLocationCopy)
                .build();
        chargingStationRepository.save(chargingStationForList);
        chargingStationRepository.save(chargingStation);

        //when
        List<ChargingStation> chargingStationList = chargingStationRepository.findAllBychargingStatus(ChargingStatus.UNAVAILABLE.getValue());

        //then
        assertThat(chargingStationList).isNotNull();
        assertThat(chargingStationList.size()).isEqualTo(0);
    }

    @DisplayName("Remove ChargingStation by ID")
    @Test
    void testRemoveChargingStationByID(){
        //given - dado o condición previa o configuración
        ChargingStationLocationPosition chargingStationLocationPosition = ChargingStationLocationPosition.builder()
                .lat(chargingStationLocation.getPosition().getLat())
                .lng(chargingStationLocation.getPosition().getLng())
                .build();

        ChargingStation chargingStationForRemove = ChargingStation.builder()
                .chargingStationPoints(chargingStationPoints)
                .chargingPointsAmount(chargingStationPoints.size())
                .chargingStatus((chargingStationPoints.size() > 0) ? ChargingStatus.AVAILABLE.getValue() : ChargingStatus.UNAVAILABLE.getValue())
                .chargingStationType(ChargingStationType.AC.getValue())
                .chargingPointsAmount(0)
                .chargingStationLocation(chargingStationLocation)
                .build();
        chargingStationForRemove = chargingStationRepository.save(chargingStationForRemove);

        //when
        chargingStationRepository.deleteById(chargingStationForRemove.getChargingStationId());
        Optional<ChargingStation> chargingStationOptional = chargingStationRepository.findById(chargingStationForRemove.getChargingStationId());

        //then
        assertThat(chargingStationOptional).isEmpty();
    }
}
