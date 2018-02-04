package net.cfiet.quandl.proxy

import groovy.transform.CompileStatic
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.util.concurrent.Future
import java.util.stream.Collectors
import java.util.stream.Stream

@CompileStatic
@RestController
@CrossOrigin
@RequestMapping('database')
class DatabaseController {
    private final CodeFilesDownloader downloader
    private final CodeFilesParser parser

    DatabaseController(CodeFilesDownloader downloader, CodeFilesParser parser) {
        this.parser = parser
        this.downloader = downloader
    }

    @Async
    @RequestMapping('{databaseName}')
    Future<List> get(@PathVariable() String databaseName) {
        return downloader.getCodeFile(databaseName)
            .thenApply { Optional<File> mf ->
                return mf.map { File f -> parser.parseFile(f) }
                    .orElseGet{ -> Stream.empty() as Stream<DataCodeDto> }
                    .collect(Collectors.toList())
            }
    }
}