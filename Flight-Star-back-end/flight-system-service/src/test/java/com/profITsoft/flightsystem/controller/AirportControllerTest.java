package com.profITsoft.flightsystem.controller;

import com.profITsoft.flightsystem.entity.Airport;
import com.profITsoft.flightsystem.repository.AirportRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AirportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AirportRepository airportRepository;

    @Test
    @Order(1)
    void successfullySaveAirport() throws Exception {
        String airportRequest =
                """
                {
                    "airportCode":"KBP",
                    "name":"Boryspil International Airport",
                    "city":"Kiev",
                    "country":"Ukraine",
                    "timezone":"Europe/Kyiv"
                }
                """;

        String actualResponse = mockMvc.perform(post("/api/airports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(airportRequest))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = """
                {"status":"success","statusCode":201,"errors":null,"data":{"id":21,"airportCode":"KBP","name":"Boryspil International Airport","city":"Kiev","country":"Ukraine","timezone":"Europe/Kyiv"}}""";

        assertEquals(actualResponse, expectedResponse);

        Airport savedAirport = airportRepository.findByAirportCode("KBP").orElseThrow();
        assertEquals("Boryspil International Airport", savedAirport.getName());
        assertEquals("Kiev", savedAirport.getCity());
        assertEquals("Ukraine", savedAirport.getCountry());
        assertEquals("Europe/Kyiv", savedAirport.getTimezone());
    }

    @Order(2)
    @Test
    void updateAirport() throws Exception {
        String airportRequest =
                """
                {
                    "airportCode":"KBC",
                    "name":"International Airport",
                    "city":"Kiev1",
                    "country":"Ukraine1",
                    "timezone":"Europe/Kyiv1"
                }
                """;

        String actualResponse = mockMvc.perform(put("/api/airports/21")
                .contentType(MediaType.APPLICATION_JSON)
                .content(airportRequest))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = """
                {"status":"success","statusCode":200,"errors":null,"data":{"id":21,"airportCode":"KBC","name":"International Airport","city":"Kiev1","country":"Ukraine1","timezone":"Europe/Kyiv1"}}""";

        assertEquals(actualResponse, expectedResponse);

        Airport savedAirport = airportRepository.findByAirportCode("KBC").orElseThrow();
        assertEquals("International Airport", savedAirport.getName());
        assertEquals("Kiev1", savedAirport.getCity());
        assertEquals("Ukraine1", savedAirport.getCountry());
        assertEquals("Europe/Kyiv1", savedAirport.getTimezone());
    }

    @Order(3)
    @Test
    void deleteAirport() throws Exception {
        mockMvc.perform(delete("/api/airports/21")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertFalse(airportRepository.existsByAirportCode("KBC"));
    }

    @Order(4)
    @Test
    void getAirports() throws Exception {
        mockMvc.perform(get("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(20)));
    }
}