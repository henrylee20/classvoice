package me.henrylee.classvoice.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoiceInfoRepository extends MongoRepository<VoiceInfo, String> {
    List<VoiceInfo> findByStudentId(String studentId);

    List<VoiceInfo> findByQuestionId(String questionId);
}
