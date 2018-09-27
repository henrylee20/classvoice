package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.DemoEntity;
import me.henrylee.classvoice.model.DemoEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DemoServiceImpl implements DemoService {

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
}
