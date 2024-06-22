package com.profITsoft.flightsystem.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profITsoft.flightsystem.dto.FlightDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * FlightJsonParser class to parse flights from JSON file.
 */
@Service
@RequiredArgsConstructor
public class FlightJsonParser {

    private final ObjectMapper objectMapper;

    /**
     * Parse flights from the provided JSON file.
     *
     * @param file the JSON file
     * @return the parse result
     * @throws Exception if an error occurs
     */
    public ParseResult parseFlights(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        JsonNode rootNode = objectMapper.readTree(inputStream);
        List<FlightDto> flights = new ArrayList<>();

        int succeeded = 0;
        int failed = 0;

        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                try {
                    FlightDto flight = objectMapper.treeToValue(node, FlightDto.class);
                    flights.add(flight);
                    succeeded++;
                } catch (Exception e) {
                    failed++;
                }
            }
        }

        return new ParseResult(flights, succeeded, failed);
    }

    /**
     * Parse result class to hold the parse result.
     */
    public record ParseResult(List<FlightDto> flights, int succeeded, int failed) {
    }
}
