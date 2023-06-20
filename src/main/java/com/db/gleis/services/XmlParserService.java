package com.db.gleis.services;

import com.db.gleis.utils.XmlReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for parsing XML files and retrieving sections based on train and wagon numbers.
 */
@Service
@CacheConfig(cacheNames = "xmlSectionsCache")
public class XmlParserService {

    private final XmlReader xmlReader;
    private final String xmlDirectory;

    private final String XML_FILE_EXTENSION = ".xml";

    /**
     * Constructs an XmlParserService instance.
     *
     * @param xmlReader    the XmlReader instance for parsing XML files
     * @param xmlDirectory the directory path where the XML files are located
     */
    public XmlParserService(XmlReader xmlReader, @Value("${xml.file.path}") String xmlDirectory) {
        this.xmlReader = xmlReader;
        this.xmlDirectory = xmlDirectory;
    }

    /**
     * Retrieves the sections that match the given train and wagon numbers from the XML files
     * with the specified RIL100 prefix.
     *
     * @param ril100             the RIL100 prefix of the XML files
     * @param targetTrainNumber  the target train number
     * @param targetWagonNumber  the target wagon number
     * @return a list of sections matching the given train and wagon numbers
     * @throws XMLStreamException if an error occurs during XML parsing
     * @throws IOException        if an I/O error occurs while accessing the XML files
     */
    @Cacheable
    public List<String> getSectionsForTrainAndWagon(String ril100, int targetTrainNumber, int targetWagonNumber)
            throws XMLStreamException, IOException {
        File[] files = getFilesMatchingPrefixAndExtension(ril100);

        if (files != null && files.length > 0) {
            for (File file : files) {
                List<String> sections = xmlReader.parseXmlAndGetSections(file, targetTrainNumber, targetWagonNumber);
                if (!sections.isEmpty()) {
                    return sections;
                }
            }
        }

        return new ArrayList<>();
    }

    /**
     * Retrieves the files in the XML directory that match the specified RIL100 prefix
     * and have the .xml file extension.
     *
     * @param ril100 the RIL100 prefix
     * @return an array of files matching the prefix and extension
     */
    private File[] getFilesMatchingPrefixAndExtension(String ril100) {
        File directory = new File(xmlDirectory);
        return directory.listFiles((dir, name) -> name.startsWith(ril100) && name.endsWith(XML_FILE_EXTENSION));
    }
}
