package net.cfiet.quandl.proxy

import groovy.transform.CompileStatic
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.nio.charset.Charset
import java.util.stream.Stream

@CompileStatic
@Component
class CodeFilesParser {
    private static Logger Logger = LoggerFactory.getLogger(CodeFilesParser.class)

    Stream<DataCodeDto> parseFile(File dataCodeFile) {
        Logger.info("Parsing file $dataCodeFile")
        return CSVParser.parse(dataCodeFile, Charset.forName("UTF-8"), CSVFormat.DEFAULT)
            .withCloseable(CodeFilesParser.&runParser);
    }

    private static Stream<DataCodeDto> runParser(CSVParser parser) {
        return parser.getRecords()
            .stream()
            .map (CodeFilesParser.&mapRecord)
            .filter{ Optional<?> i -> i.isPresent() }
            .map { Optional<DataCodeDto> i -> i.get() }
    }

    private static Optional<DataCodeDto> mapRecord(CSVRecord record) {
        def parsedRecord = record.get(0).split('/')
        if (parsedRecord.length < 2) return Optional.empty()

        return Optional.of(new DataCodeDto(parsedRecord[0], parsedRecord[1]))
    }
}