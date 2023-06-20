package com.db.gleis.utils;

import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class XmlReader {

    private static final String ELEMENT_TRAIN_NUMBER = "trainNumber";
    private static final String ELEMENT_POSITION = "position";
    private static final String ELEMENT_SECTIONS = "sections";
    private static final String ELEMENT_IDENTIFIER = "identifier";

    private final XMLInputFactory xmlInputFactory;

    /**
     * Constructs an XmlReader instance.
     */
    public XmlReader() {
        xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    }

    /**
     * Parses the XML file and retrieves the sections for the given train number and wagon number.
     *
     * @param xmlFile           the XML file to parse
     * @param targetTrainNumber the target train number to match
     * @param targetWagonNumber the target wagon number to match
     * @return the list of sections for the matching train and wagon numbers
     */
    public List<String> parseXmlAndGetSections(File xmlFile, int targetTrainNumber, int targetWagonNumber) {
        List<String> sections = new ArrayList<>();

        try (InputStream input = new FileInputStream(xmlFile)) {
            XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(input);
            sections = parseSections(reader, targetTrainNumber, targetWagonNumber);
        } catch (IOException | XMLStreamException e) {
            // Handle or wrap the exceptions appropriately
            e.printStackTrace();
        }

        return sections;
    }

    private List<String> parseSections(XMLStreamReader reader, int targetTrainNumber, int targetWagonNumber)
            throws XMLStreamException {
        String currentTrainNumber = null;
        String currentWagonNumber = null;
        boolean isMatchingTrainAndWagon = false;
        List<String> sections = new ArrayList<>();

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                String elementName = reader.getLocalName();

                if (elementName.equalsIgnoreCase(ELEMENT_TRAIN_NUMBER)) {
                    currentTrainNumber = reader.getElementText();
                } else if (elementName.equalsIgnoreCase(ELEMENT_POSITION)) {
                    currentWagonNumber = reader.getElementText();
                } else if (elementName.equalsIgnoreCase(ELEMENT_SECTIONS)) {
                    if (isMatchingTrainAndWagon(currentTrainNumber, currentWagonNumber,
                            targetTrainNumber, targetWagonNumber)) {
                        isMatchingTrainAndWagon = true;
                    }
                } else if (elementName.equalsIgnoreCase(ELEMENT_IDENTIFIER) && isMatchingTrainAndWagon) {
                    String section = reader.getElementText();
                    sections.add(section);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (reader.getLocalName().equalsIgnoreCase(ELEMENT_SECTIONS)) {
                    if (isMatchingTrainAndWagon) {
                        return sections;
                    }
                }
            }
        }

        return sections;
    }

    private boolean isMatchingTrainAndWagon(String currentTrainNumber, String currentWagonNumber,
                                            int targetTrainNumber, int targetWagonNumber) {
        return currentTrainNumber != null && currentWagonNumber != null &&
                currentTrainNumber.equals(String.valueOf(targetTrainNumber)) &&
                currentWagonNumber.equals(String.valueOf(targetWagonNumber));
    }
}
