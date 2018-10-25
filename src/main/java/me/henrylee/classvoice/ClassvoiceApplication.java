package me.henrylee.classvoice;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@SpringBootApplication
@EnableAsync
public class ClassvoiceApplication {

    @Value("${server.http.port}")
    private int httpPort;

    // Add HTTP Port
    @Bean
    public ServletWebServerFactory serverFactory() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createStandardConnector());
        return tomcat;
    }

    private Connector createStandardConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(httpPort);
        return connector;
    }

    // Config thread pool
    @Bean("ASRTaskPool")
    public Executor asrTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(2000);
        executor.setThreadNamePrefix("ASRTask-");
        executor.initialize();
        return executor;
    }

    @Bean("NLPTaskPool")
    public Executor nlpTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(2000);
        executor.setThreadNamePrefix("NLPTask-");
        executor.initialize();
        return executor;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClassvoiceApplication.class, args);
    }
}
