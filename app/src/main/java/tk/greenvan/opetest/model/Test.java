package tk.greenvan.opetest.model;

import androidx.annotation.NonNull;

import java.util.List;

public class Test {
    private String id;
    private String name;
    private String photoUrl;
    private List<Question> questions;

    public Test(){}

    public Test(String name) {
        this.id = name;
        this.name = name;
    }

    public Test(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Test(String id, String name, String photoUrl) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getName();
    }
}
