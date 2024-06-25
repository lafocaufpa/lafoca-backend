package com.ufpa.lafocabackend.domain.model.dto.input;

public class RecordCountDTO {

    private int articles;
    private int tccs;
    private int projects;
    private int members;

    public int getArticles() {
        return articles;
    }

    public void setArticles(int articles) {
        this.articles = articles;
    }

    public int getTccs() {
        return tccs;
    }

    public void setTccs(int tccs) {
        this.tccs = tccs;
    }

    public int getProjects() {
        return projects;
    }

    public void setProjects(int projects) {
        this.projects = projects;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }
}
