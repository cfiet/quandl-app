package net.cfiet.quandl.proxy

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Canonical
@CompileStatic
@Component
@ConfigurationProperties("quandl.codeFiles")
class CodeFilesConfig {
    String zipDirectory;
    String csvDirectory;
}