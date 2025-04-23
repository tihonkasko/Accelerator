package com.accelerator.facades.implementations;

import com.accelerator.dto.PentUNFOLDModel;
import com.accelerator.facades.XlsxFillingFacade;
import com.accelerator.services.FileProcessingService;
import com.accelerator.services.XlsxService;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

@Service("xlsxFillingFacade")
public class XlsxFillingFacadeImpl implements XlsxFillingFacade {

    @Resource
    XlsxService xlsxService;
    @Resource
    FileProcessingService fileProcessingService;

    private static final String FILE_1D_PATH = "src/main/resources/user-files/%s1D.xlsx";
    private static final String FILE_2D_PATH = "src/main/resources/user-files/%s2D.xlsx";
    private static final String FILE_3D_PATH = "src/main/resources/user-files/%s3D.xlsx";
    private static final String MOTHER_FILE_1D_PATH = "src/main/resources/PentUNFOLD1D.xlsx";
    private static final String MOTHER_FILE_2D_PATH = "src/main/resources/PentUNFOLD2D.xlsx";
    private static final String MOTHER_FILE_3D_PATH = "src/main/resources/PentUNFOLD3D.xlsx";
    private static final String ROW_ELEMENT = "row";
    private static final String VALUE_ELEMENT = "v";
    private static final String CELL_ELEMENT = "c";

    private XMLEventFactory eventFactory;
    private SharedStringsTable sharedstringstable;
    private PackagePart sharedStringsTablePart;
    private XMLEventWriter writer;
    private int rowsCount;
    private int columnCount;
    private boolean isFillPic = false;
    private boolean isFillPdb = false;
    private boolean isFillDssp = false;
    private boolean isFillSequence = false;

    @Override
    public void fill1DFile(PentUNFOLDModel pentUNFOLDModel, String fileName) throws Exception {
        fileProcessingService.copyFile(fileName + "1D", MOTHER_FILE_1D_PATH);
        try {
            fillAminoAcidSequence(pentUNFOLDModel.getSequence(), fileName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new NoSuchFieldException("File is incorrect");
        } finally {
            fileProcessingService.removeFile(format(FILE_1D_PATH, fileName));
        }
    }

    @Override
    public void fill2DFile(PentUNFOLDModel pentUNFOLDModel, String fileName) throws Exception {
        fileProcessingService.copyFile(fileName + "2D", MOTHER_FILE_2D_PATH);
        try {
            fillPdb(pentUNFOLDModel, fileName, FILE_2D_PATH);
            fillDssp(pentUNFOLDModel, fileName, FILE_2D_PATH);
        } catch (Exception e) {
            System.err.println("Failed to fill file: " + fileName);
            throw new NoSuchFieldException("File is incorrect");
        } finally {
            fileProcessingService.removeFile(format(FILE_2D_PATH, fileName));
        }
    }

    @Override
    public void fill3DFile(PentUNFOLDModel pentUNFOLDModel, String fileName) throws Exception {
        fileProcessingService.copyFile(fileName + "3D", MOTHER_FILE_3D_PATH);
        try {
            fillPdb(pentUNFOLDModel, fileName, FILE_3D_PATH);
            fillDssp(pentUNFOLDModel, fileName, FILE_3D_PATH);
            fillPic(pentUNFOLDModel.getPic(), 4, format(FILE_3D_PATH, fileName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new NoSuchFieldException("File is incorrect");
        } finally {
            fileProcessingService.removeFile(format(FILE_3D_PATH, fileName));
        }
    }

    private void fillAminoAcidSequence(String aminoAcidSequence, String fileName) throws Exception {
        isFillSequence = true;
        processOneSheet(singletonList(aminoAcidSequence), 2, format(FILE_1D_PATH, fileName));
        isFillSequence = false;
    }

    private void fillPdb(PentUNFOLDModel pentUNFOLDModel, String fileName, String path) throws Exception {
        isFillPdb = true;
        fillPic(pentUNFOLDModel.getPdb(), 2, format(path, fileName));
        isFillPdb = false;
    }

    private void fillDssp(PentUNFOLDModel pentUNFOLDModel, String fileName, String path) throws Exception {
        isFillDssp = true;
        fillPic(pentUNFOLDModel.getDssp(), 3, format(path, fileName));
        isFillDssp = false;
    }

    private void fillPic(List<String> values, int sheet, String filePath) throws Exception {
        isFillPic = true;
        processOneSheet(values, sheet, filePath);
        isFillPic = false;
    }

    public void processOneSheet(List<String> values, int sheet, String filePath) throws Exception {
        OPCPackage opcpackage = OPCPackage.open(filePath);
        XMLEventReader reader = startFillingProcess(opcpackage, sheet);

        while(reader.hasNext()){
            XMLEvent event = (XMLEvent)reader.next();
            if(event.isStartElement() && values.size() >= rowsCount - 1) {
                event = putNewValuesInOldRows(reader, event, values);
            }
            writer.add(event);
            if(event.isEndElement()  && values.size() >= rowsCount - 1 && isNeedAllValuesToFill()){
                putOtherValuesToNewRows(reader, event, values);
            }
        }
        closeFillingProcess();
        opcpackage.close();
    }

    private boolean isNeedAllValuesToFill() {
        return !isFillSequence;
    }

    private XMLEventReader startFillingProcess(OPCPackage opcpackage, int sheet)
            throws IOException, XMLStreamException {
        sharedStringsTablePart = opcpackage.getPartsByName(Pattern.compile("/xl/sharedStrings.xml")).get(0);
        sharedstringstable = new SharedStringsTable();
        sharedstringstable.readFrom(sharedStringsTablePart.getInputStream());
        PackagePart sheetpart = opcpackage.getPartsByName(Pattern.compile("/xl/worksheets/sheet" + sheet + ".xml")).get(0);
        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(sheetpart.getInputStream());
        writer = XMLOutputFactory.newInstance().createXMLEventWriter(sheetpart.getOutputStream());
        eventFactory = XMLEventFactory.newInstance();
        rowsCount = 0;
        columnCount = 0;
        return reader;
    }

    private void closeFillingProcess() throws XMLStreamException, IOException {
        writer.flush();
        OutputStream out = sharedStringsTablePart.getOutputStream();
        sharedstringstable.writeTo(out);
        out.close();
    }

    private XMLEvent putNewValuesInOldRows(XMLEventReader reader, XMLEvent event, List<String> values) throws XMLStreamException {
        StartElement startElement = (StartElement)event;
        QName startElementName = startElement.getName();
        if(startElementName.getLocalPart().equalsIgnoreCase(ROW_ELEMENT)) {
            rowsCount++;
            columnCount = 0;
        }
        if (startElementName.getLocalPart().equalsIgnoreCase(CELL_ELEMENT) && !isNeedReplaceOldValue()) {
            columnCount++;
            event = fillCellIfNeeded(reader, event, values);
        }
        if (startElementName.getLocalPart().equalsIgnoreCase(VALUE_ELEMENT) && isNeedReplaceOldValue()) {
            event = replaceOldValue(reader, values, event);
        }
        return event;
    }

    private XMLEvent fillCellIfNeeded(XMLEventReader reader, XMLEvent event,
                                      List<String> values) throws XMLStreamException {
        if (isNeedCellFilling(reader)) {
            return putValueInEmptyCell(reader, values.get(rowsCount - 2));
        } else if (isFillPic && rowsCount > 1 && values.size() >= (rowsCount - 1) * 2 && !isFillDssp && !isFillPdb && !isFillSequence) {
            return putPicValues(reader, event, values);
        } else if (isFillPic && rowsCount > 1 && values.size() >= (rowsCount - 1) * 2 && isFillPdb) {
            return putPdbValues(reader, event, values);
        } else if (isFillPic && rowsCount > 1 && values.size() >= (rowsCount - 1) * 3 && isFillDssp) {
            return putDsspValues(reader, event, values);
        }
        return event;
    }

    private XMLEvent putPicValues(XMLEventReader reader, XMLEvent event, List<String> values) throws XMLStreamException {
        if(columnCount == 1) {
            return putPicInEmptyCell(reader, values);
        }
        return event;
    }

    private XMLEvent putPdbValues(XMLEventReader reader, XMLEvent event, List<String> values) throws XMLStreamException {
        if(columnCount == 1) {
            return putPdbInEmptyCell(reader, values);
        }
        return event;
    }

    private XMLEvent putDsspValues(XMLEventReader reader, XMLEvent event, List<String> values) throws XMLStreamException {
        if(columnCount == 1) {
            return putDsspInEmptyCell(reader, values);
        }
        return event;
    }

    private XMLEvent putPdbInEmptyCell(XMLEventReader reader, List<String> values) throws XMLStreamException {
        int picPairNumber = rowsCount - 1;
        if(columnCount == 1) {
            writer = xlsxService.writeValueInNewCell(eventFactory, sharedstringstable, writer, values.get(picPairNumber * 2 - 2));
            writer = xlsxService.writeValueInNewCell(eventFactory, sharedstringstable, writer, values.get(picPairNumber * 2 - 1));
        }
        while (!isNextRow(reader)) {
            reader.next();
        }
        return (XMLEvent)reader.next();
    }

    private XMLEvent putPicInEmptyCell(XMLEventReader reader, List<String> values) throws XMLStreamException {
        int picPairNumber = rowsCount - 1;
        if(columnCount == 1) {
            writer = xlsxService.writeValueInNewCell(eventFactory, sharedstringstable, writer, values.get(picPairNumber * 2 - 2));
            if(!isFillPdb){
                writer = xlsxService.writeValueInNewCell(eventFactory, sharedstringstable, writer, null);
                writer = xlsxService.writeValueInNewCell(eventFactory, sharedstringstable, writer, null);
            }
            writer = xlsxService.writeValueInNewCell(eventFactory, sharedstringstable, writer, values.get(picPairNumber * 2 - 1));
        }
        while (!isNextRow(reader)) {
            reader.next();
        }
        return (XMLEvent)reader.next();
    }

    private XMLEvent putDsspInEmptyCell(XMLEventReader reader, List<String> values) throws XMLStreamException {
        int picPairNumber = rowsCount - 1;
        if(columnCount == 1) {
            writer = xlsxService.writeValueInNewCell(eventFactory, sharedstringstable, writer, values.get(picPairNumber * 3 - 3));
            writer = xlsxService.writeValueInNewCell(eventFactory, sharedstringstable, writer, values.get(picPairNumber * 3 - 2));
            writer = xlsxService.writeValueInNewCell(eventFactory, sharedstringstable, writer, values.get(picPairNumber * 3 - 1));
        }
        while (!isNextRow(reader)) {
            reader.next();
        }
        return (XMLEvent)reader.next();
    }


    private XMLEvent putValueInEmptyCell(XMLEventReader reader, String value) throws XMLStreamException {
        writer = xlsxService.writeValueInNewCell(eventFactory, sharedstringstable, writer, value);
        reader.next();
        return (XMLEvent)reader.next();
    }

    private void putOtherValuesToNewRows(XMLEventReader reader, XMLEvent event, List<String> values)
            throws XMLStreamException {
        EndElement endElement = (EndElement)event;
        QName endElementName = endElement.getName();
        if(endElementName.getLocalPart().equalsIgnoreCase(ROW_ELEMENT)) {
            putValuesInNew(reader, values);
        }
    }

    private void putValuesInNew(XMLEventReader reader, List<String> values) throws XMLStreamException {
        XMLEvent nextElement = (XMLEvent)reader.peek();
        QName nextElementName = defineNextElementName(nextElement);
        if(!nextElementName.getLocalPart().equalsIgnoreCase(ROW_ELEMENT)) {
            if(isFillPic && !isFillDssp && !isFillPdb && !isFillSequence) {
                List<String> residueValues = values.subList(rowsCount * 2 - 2, values.size());
                writer = xlsxService.writePicInNewRows(eventFactory, sharedstringstable, writer, residueValues);
            } else if(isFillDssp) {
                List<String> residueValues = values.subList(rowsCount * 3 - 3, values.size());
                writer = xlsxService.writeDsspInNewRows(eventFactory, sharedstringstable, writer, residueValues);
            } else if(isFillPdb) {
                List<String> residueValues = values.subList(rowsCount * 2 - 2, values.size());
                writer = xlsxService.writePdbInNewRows(eventFactory, sharedstringstable, writer, residueValues);
            } else {
                List<String> residueValues = values.subList(rowsCount - 1, values.size());
                writer = xlsxService.writeValuesInNewRows(eventFactory, sharedstringstable, writer, residueValues);
            }
        }
    }

    private XMLEvent replaceOldValue(XMLEventReader reader, List<String> values, XMLEvent event) throws XMLStreamException {
        writer.add(event);
        event = (XMLEvent)reader.next();
        if (event.isCharacters()) {
            String value = values.get(rowsCount - 1);
            event = xlsxService.prepareFillingValueEvent(eventFactory, sharedstringstable, value);
        }
        return event;
    }

    private QName defineNextElementName(XMLEvent nextElement) {
        if (nextElement.isStartElement()) {
            return ((StartElement)nextElement).getName();
        } else if (nextElement.isEndElement()) {
            return  ((EndElement)nextElement).getName();
        }
        return null;
    }

    private boolean isNeedReplaceOldValue() {
        return  rowsCount >= 1
                && isFillSequence;
    }

    private boolean isNeedCellFilling(XMLEventReader reader) throws XMLStreamException {
        return rowsCount > 1
                && columnCount == 1
                && !isFillPic
                && !isFillDssp
                && noValue(reader);
    }

    private boolean noValue(XMLEventReader reader) throws XMLStreamException {
        XMLEvent nextElement = (XMLEvent)reader.peek();
        QName nextElementName = defineNextElementName(nextElement);
        return !nextElementName.getLocalPart().equalsIgnoreCase(VALUE_ELEMENT);
    }

    private boolean isNextRow(XMLEventReader reader) throws XMLStreamException {
        XMLEvent nextElement = (XMLEvent)reader.peek();
        QName nextElementName = defineNextElementName(nextElement);
        return nextElementName != null && nextElementName.getLocalPart().equalsIgnoreCase(ROW_ELEMENT);
    }
}
