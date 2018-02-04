package net.cfiet.quandl.proxy

import groovy.transform.CompileStatic
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource

@CompileStatic
@SpringBootApplication
@PropertySource("classpath:application.properties")
class Application {
    public static void main(String[] argv) {
        def application = new SpringApplication(Application.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(argv);
    }
}
