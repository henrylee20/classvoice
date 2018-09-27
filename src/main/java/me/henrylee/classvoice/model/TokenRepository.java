package me.henrylee.classvoice.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {
    List<Token> findByToken(String token);

    List<Token> findByOpenid(String openid);

    List<Token> findByUserId(String userId);
}
