package me.henrylee.classvoice.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    List<Student> findByName(String name);

    List<Student> findByOpenid(String name);

    List<Student> findByNick(String nick);
}
