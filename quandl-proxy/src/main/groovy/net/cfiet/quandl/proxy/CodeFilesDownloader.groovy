package net.cfiet.quandl.proxy

import groovy.transform.CompileStatic
import groovy.util.logging.Log
import org.apache.tomcat.util.http.fileupload.IOUtils
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.Dsl
import org.asynchttpclient.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture
import java.util.zip.ZipInputStream

@CompileStatic
@Component
class CodeFilesDownloader {
    private static Logger Logger = LoggerFactory.getLogger(CodeFilesDownloader.class)

    private final URI apiRoot
    private final Path csvDirectory
    private final AsyncHttpClient httpClient = Dsl.asyncHttpClient()

    CodeFilesDownloader(ApiConfig apiConfig, CodeFilesConfig codeFilesConfig) {
        this.apiRoot = URI.create(apiConfig.apiRoot)
        this.csvDirectory = Paths.get(codeFilesConfig.csvDirectory);

        if(!Files.exists(csvDirectory)) {
            Logger.info("Creating directory: $csvDirectory")
            Files.createDirectories(csvDirectory);
        }
    }

    CompletableFuture<Optional<File>> getCodeFile(String databaseName) {
        Path targetFilePath = csvDirectory.resolve(databaseName + ".csv")
        if (Files.exists(targetFilePath)) {
            Logger.info("Codes file $targetFilePath already exists")
            return CompletableFuture.completedFuture(
                Optional.of(
                    targetFilePath.toFile()
                )
            )
        }

        Logger.info("Fetching codes for database $databaseName to $targetFilePath")
        def targetUri = UriComponentsBuilder.fromUri(this.apiRoot)
            .pathSegment("databases", databaseName, "codes")
            .build()

        Logger.debug("Requesting codes for $databaseName from $targetUri")
        return httpClient.prepareGet(targetUri.toUriString())
            .setFollowRedirect(true)
            .execute()
            .toCompletableFuture()
            .thenApply({ Response response ->
                Logger.debug("Request to $targetUri returned $response.statusCode $response.statusText")
                if (response.statusCode >= 400) {
                    return Optional.empty() as Optional<File>
                }

                Logger.debug("Unzipping response from $targetUri to $targetFilePath")
                return new ZipInputStream(response.getResponseBodyAsStream()).withCloseable{ ZipInputStream sourceZipStream ->
                    def entry = sourceZipStream.getNextEntry()
                    if (!entry) {
                        Logger.warn("Zip stream returned from $targetUri is empty")
                        return Optional.empty() as Optional<File>
                    }

                    new FileOutputStream(targetFilePath.toFile()).withCloseable{ FileOutputStream targetFileStream ->
                        IOUtils.copy(sourceZipStream, targetFileStream);
                    }

                    Logger.info("Successfully unpacked codes from $targetUri to $targetFilePath")
                    return Optional.of(targetFilePath.toFile())
                }
            })
    }
}