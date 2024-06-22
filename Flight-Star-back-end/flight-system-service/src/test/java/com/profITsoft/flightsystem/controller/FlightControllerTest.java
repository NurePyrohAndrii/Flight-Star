package com.profITsoft.flightsystem.controller;

import com.profITsoft.flightsystem.entity.Flight;
import com.profITsoft.flightsystem.repository.FlightRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightRepository flightRepository;

    @Order(1)
    @Test
    void saveFlight() throws Exception {
        String request = """
                {
                  "flightNumber": "AC19",
                  "departureAirport": "JFK",
                  "arrivalAirport": "LAX",
                  "departureTime": "2024-05-15T14:00:00Z",
                  "arrivalTime": "2024-05-15T17:00:00Z",
                  "services": ["Wi-Fi", "Meals", "Extra Legroom"]
                }
                """;

        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.data.flightNumber").value("AC19"))
                .andExpect(jsonPath("$.data.departureAirport.airportCode").value("JFK"))
                .andExpect(jsonPath("$.data.arrivalAirport.airportCode").value("LAX"))
                .andExpect(jsonPath("$.data.departureTime").value("2024-05-15T14:00:00Z"))
                .andExpect(jsonPath("$.data.arrivalTime").value("2024-05-15T17:00:00Z"))
                .andExpect(jsonPath("$.data.services", hasSize(3)));

        Flight savedFlight = flightRepository.findByFlightNumber("AC19").orElseThrow();
        assertEquals("JFK", savedFlight.getDepartureAirport().getAirportCode());
        assertEquals("LAX", savedFlight.getArrivalAirport().getAirportCode());
        assertEquals("2024-05-15T14:00:00Z", savedFlight.getDepartureTime().toString());
        assertEquals("2024-05-15T17:00:00Z", savedFlight.getArrivalTime().toString());

        assertEquals(flightRepository.findAll().size(), 1);
    }

    @Order(2)
    @Test
    void updateFlight() throws Exception {
        String request = """
                {
                  "flightNumber": "AC20",
                  "departureAirport": "JFK",
                  "arrivalAirport": "LAX",
                  "departureTime": "2024-05-15T14:30:00Z",
                  "arrivalTime": "2024-05-15T17:30:00Z",
                  "services": ["Wi-Fi", "Meals", "Extra Legroom", "In-flight Shopping"]
                }
                """;

        mockMvc.perform(put("/api/flights/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());

        Flight savedFlight = flightRepository.findByFlightNumber("AC20").orElseThrow();
        assertEquals("JFK", savedFlight.getDepartureAirport().getAirportCode());
        assertEquals("LAX", savedFlight.getArrivalAirport().getAirportCode());
        assertEquals("2024-05-15T14:30:00Z", savedFlight.getDepartureTime().toString());
        assertEquals("2024-05-15T17:30:00Z", savedFlight.getArrivalTime().toString());

        assertEquals(flightRepository.findAll().size(), 1);
    }

    @Order(3)
    @Test
    void getFlightDetails() throws Exception {
        mockMvc.perform(get("/api/flights/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.flightNumber").value("AC20"))
                .andExpect(jsonPath("$.data.departureAirport.airportCode").value("JFK"))
                .andExpect(jsonPath("$.data.arrivalAirport.airportCode").value("LAX"))
                .andExpect(jsonPath("$.data.departureTime").value("2024-05-15T14:30:00Z"))
                .andExpect(jsonPath("$.data.arrivalTime").value("2024-05-15T17:30:00Z"))
                .andExpect(jsonPath("$.data.services", hasSize(4)));
    }

    @Order(4)
    @Test
    void deleteFlight() throws Exception {
        mockMvc.perform(delete("/api/flights/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertTrue(flightRepository.findAll().isEmpty());
    }

    @Order(5)
    @Test
    void uploadFlights() throws Exception {
        Resource fileResource = new ClassPathResource("jsondata/flights.json");
        assertNotNull(fileResource);

        MockMultipartFile firstFile = new MockMultipartFile(
                "file", fileResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                fileResource.getInputStream());

        mockMvc.perform(multipart("/api/flights/upload")
                        .file(firstFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.succeeded").value(100))
                .andExpect(jsonPath("$.data.failed").value(0));

        assertEquals(flightRepository.findAll().size(), 100);
    }

    @Order(6)
    @Test
    void getFlightReport() throws Exception {
        String request = """
            {
              "departureAirport": "JFK",
              "arrivalAirport": "LAX",
              "services": ["Wi-Fi", "Meals", "Extra Legroom"]
            }
            """;

        String expectedHeader = "Flight Number,Departure Airport,Arrival Airport,Departure Time,Arrival Time";
        String expectedFlight1 = "AC001,JFK,LAX,2025-05-03T08:00:00Z,2025-05-03T11:00:00Z";
        String expectedFlight2 = "AC021,JFK,LAX,2025-05-08T10:00:00Z,2025-05-08T13:00:00Z";
        String expectedFlight3 = "AC041,JFK,LAX,2025-05-13T10:00:00Z,2025-05-13T13:00:00Z";
        String expectedFlight4 = "AC061,JFK,LAX,2025-05-18T10:00:00Z,2025-05-18T13:00:00Z";
        String expectedFlight5 = "AC081,JFK,LAX,2025-05-23T10:00:00Z,2025-05-23T13:00:00Z";

        String expectedContent = String.join("\r\n",
                expectedHeader,
                expectedFlight1,
                expectedFlight2,
                expectedFlight3,
                expectedFlight4,
                expectedFlight5,
                ""
        );

        mockMvc.perform(post("/api/flights/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=flights.csv"))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(content().string(expectedContent));
    }

    @Order(7)
    @Test
    void listFlights() throws Exception {
        String request = """
                {
                  "departureAirport": "JFK",
                  "arrivalAirport": "LAX",
                  "services": ["Wi-Fi", "Meals", "Extra Legroom"],
                  "page": 0,
                  "size": 1
                }
                """;

        mockMvc.perform(post("/api/flights/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].flightNumber").value("AC001"))
                .andExpect(jsonPath("$.data.content[0].departureAirport").value("JFK"))
                .andExpect(jsonPath("$.data.content[0].arrivalAirport").value("LAX"))
                .andExpect(jsonPath("$.data.content[0].departureTime").value("2025-05-03T08:00:00Z"))
                .andExpect(jsonPath("$.data.content[0].arrivalTime").value("2025-05-03T11:00:00Z"))
                .andExpect(jsonPath("$.data.content[0].services", hasSize(5)))
                .andExpect(jsonPath("$.data.totalElements").value(5))
                .andExpect(jsonPath("$.data.totalPages").value(5));
    }
}