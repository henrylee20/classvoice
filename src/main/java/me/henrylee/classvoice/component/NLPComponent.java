package me.henrylee.classvoice.component;

import me.henrylee.classvoice.nlp.SegmenterFactory;
import me.henrylee.classvoice.service.NLPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class NLPComponent implements ApplicationRunner {

    @Value("${henrylee.nlp.className}")
    private String segmenterClassName;

    private static final Logger logger = LoggerFactory.getLogger(NLPComponent.class);
    private NLPService nlpService;

    @Autowired
    public NLPComponent(NLPService nlpService) {
        this.nlpService = nlpService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Start nlp init thread");
        nlpService.initNLP();
    }
}
