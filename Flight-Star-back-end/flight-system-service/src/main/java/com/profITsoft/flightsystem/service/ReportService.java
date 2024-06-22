package com.profITsoft.flightsystem.service;

import com.profITsoft.flightsystem.dto.ListFlightDto;
import org.springframework.stereotype.Service;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Report service class to generate reports
 */
@Service
public class ReportService {

    /**
     * Generate CSV report for the provided flights.
     *
     * @param flights list of flights
     * @return CSV report
     * @throws IOException if an I/O error occurs
     */
    public byte[] generateCsvReport(List<ListFlightDto> flights) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter printer = new PrintWriter(out);

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Flight Number", "Departure Airport", "Arrival Airport", "Departure Time", "Arrival Time")
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(printer, format)) {
            for (ListFlightDto flight : flights) {
                csvPrinter.printRecord(
                        flight.getId(),
                        flight.getFlightNumber(),
                        flight.getDepartureAirport(),
                        flight.getArrivalAirport(),
                        flight.getDepartureTime(),
                        flight.getArrivalTime()
                );
            }
        }
        printer.flush();
        return out.toByteArray();
    }
}
