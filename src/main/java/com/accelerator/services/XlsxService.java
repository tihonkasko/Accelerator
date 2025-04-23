package com.accelerator.services;

import org.apache.poi.xssf.model.SharedStringsTable;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.List;

public interface XlsxService {

    XMLEvent prepareFillingValueEvent(XMLEventFactory eventFactory,
                                      SharedStringsTable sharedstringstable,
                                      String value);

    XMLEventWriter writeValueInNewCell(XMLEventFactory eventFactory,
                                       SharedStringsTable sharedstringstable,
                                       XMLEventWriter writer,
                                       String value) throws XMLStreamException;

    XMLEventWriter writeValuesInNewRows(XMLEventFactory eventFactory,
                                        SharedStringsTable sharedstringstable,
                                        XMLEventWriter writer,
                                        List<String> values) throws XMLStreamException;

    XMLEventWriter writeDsspInNewRows(XMLEventFactory eventFactory,
                                      SharedStringsTable sharedstringstable,
                                      XMLEventWriter writer,
                                      List<String> values) throws XMLStreamException;

    XMLEventWriter writePicInNewRows(XMLEventFactory eventFactory,
                                     SharedStringsTable sharedstringstable,
                                     XMLEventWriter writer, List<String> values) throws XMLStreamException;

    XMLEventWriter writePdbInNewRows(XMLEventFactory eventFactory,
                                     SharedStringsTable sharedstringstable,
                                     XMLEventWriter writer, List<String> values) throws XMLStreamException;
}
