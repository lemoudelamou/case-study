package com.db.gleis.controller;

import com.db.gleis.exception.PathValidationException;
import com.db.gleis.services.XmlParserService;
import com.db.gleis.response.Response;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Controller class for handling section-related requests.
 */
@CrossOrigin
@RestController
public class SectionController {

    private final XmlParserService xmlParserService;

    /**
     * Constructs a SectionController instance.
     *
     * @param xmlParserService the XmlParserService instance for retrieving sections
     */
    public SectionController(XmlParserService xmlParserService) {
        this.xmlParserService = xmlParserService;
    }

    /**
     * Handles the request to retrieve sections for a specific train and wagon.
     *
     * @param ril100      the RIL100 code of the station
     * @param trainNumber the train number
     * @param number      the wagon number
     * @return the response entity containing the sections
     */
    @GetMapping("/station/{ril100}/train/{trainNumber}/waggon/{number}")
    public ResponseEntity<Response> getSectionsForTrainAndWagon(
            @PathVariable String ril100,
            @PathVariable int trainNumber,
            @PathVariable int number
    ) {
        validatePathParameters(ril100, trainNumber, number);

        try {
            List<String> sections = xmlParserService.getSectionsForTrainAndWagon(ril100, trainNumber, number);
            return ResponseEntity.ok(new Response(sections));
        } catch (FileNotFoundException e) {
            throw new PathValidationException("File not found");
        } catch (XMLStreamException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Validates the path parameters for station, train number, and wagon number.
     *
     * @param ril100      the RIL100 code of the station
     * @param trainNumber the train number
     * @param number      the wagon number
     * @throws PathValidationException if any of the path parameters are invalid
     */
    private void validatePathParameters(String ril100, int trainNumber, int number) {
        if (ril100.length() < 2 || ril100.length() > 5) {
            String ERROR_MESSAGE_TRAINNUMBER = "Invalid station shortcode";
            throw new PathValidationException(ERROR_MESSAGE_TRAINNUMBER);
        }

        if (trainNumber <= 10 || trainNumber >= 9999) {
            throw new PathValidationException("Invalid train Number");
        }

        if (number < 0 || number >= 99) {
            throw new PathValidationException("Invalid number");
        }
    }
}
