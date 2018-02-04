package net.cfiet.quandl.proxy

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.builder.Builder

@Canonical
@CompileStatic
@Builder
class DataCodeDto {
    String database
    String code
}