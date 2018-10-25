package me.henrylee.classvoice.service;

import me.henrylee.classvoice.model.DemoEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DemoService {
    DemoEntity addDemoEntity(DemoEntity demoEntity);

    DemoEntity getDemoEntity(String id);

    DemoEntity updateDemoEntity(String id, DemoEntity demoEntity);

    DemoEntity delDemoEntity(String id);

    List<DemoEntity> getDemoByKey(String key);

    List<DemoEntity> getDemoByVal(String val);

    CompletableFuture<String> getNameAsync(String name);
}
