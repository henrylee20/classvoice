package me.henrylee.classvoice.model;

import org.springframework.data.annotation.Id;

public class Question {

    @Id
    private String id;
    private String seq;
    private String chapId;
    private String chap;
    private String knowledgeId;
    private String knowledge;
    private Integer type;
    private String desc;
    private String option;
    private String answer;
    private Double diffi;
    private String source;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getChapId() {
        return chapId;
    }

    public void setChapId(String chapId) {
        this.chapId = chapId;
    }

    public String getChap() {
        return chap;
    }

    public void setChap(String chap) {
        this.chap = chap;
    }

    public String getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(String knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Double getDiffi() {
        return diffi;
    }

    public void setDiffi(Double diffi) {
        this.diffi = diffi;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean checkBaseInfo() {
        if (desc == null && answer == null) {
            return false;
        }
        if (desc.equals("") && answer.equals("")) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("id: %s, desc: %s, answer: %s", id, desc, answer);
    }
}
