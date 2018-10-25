package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.DemoEntity;
import me.henrylee.classvoice.model.DemoEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class DemoServiceImpl implements DemoService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DemoEntityRepository repository;

    @Autowired
    public DemoServiceImpl(DemoEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public DemoEntity addDemoEntity(DemoEntity demoEntity) {
        return repository.insert(demoEntity);
    }

    @Override
    public DemoEntity getDemoEntity(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public DemoEntity updateDemoEntity(String id, DemoEntity demoEntity) {
        demoEntity.setId(id);
        return repository.save(demoEntity);
    }

    @Override
    public DemoEntity delDemoEntity(String id) {
        Optional<DemoEntity> deletedDemo = repository.findById(id);
        deletedDemo.ifPresent(demoEntity -> repository.delete(demoEntity));
        return deletedDemo.orElse(null);
    }

    @Override
    public List<DemoEntity> getDemoByKey(String key) {
        return repository.findByKey(key);
    }

    @Override
    public List<DemoEntity> getDemoByVal(String val) {
        return repository.findByVal(val);
    }

    @Override
//  @Async("testTaskPool")
    public CompletableFuture<String> getNameAsync(String name) {
        logger.info("get name: {}", name);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            logger.error("sleep err");
        }
        return CompletableFuture.completedFuture(name);
    }
}
