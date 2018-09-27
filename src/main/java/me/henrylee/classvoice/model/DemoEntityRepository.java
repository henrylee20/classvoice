package me.henrylee.classvoice.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemoEntityRepository extends MongoRepository<DemoEntity, String> {
    List<DemoEntity> findByKey(String key);

    List<DemoEntity> findByVal(String key);
}
