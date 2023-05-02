package com.general.etl.reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CSVReaderImpl extends AbstractFileReader<CSVRecord> {

    public CSVReaderImpl(String filePath) {
        super(filePath);
    }

    public CSVReaderImpl(File file) {
        super(file);
    }

    @Override
    protected Iterable<CSVRecord> getReader(File file) throws IOException {
        return CSVParser.parse(new FileReader(file), CSVFormat.DEFAULT);
    }
}
