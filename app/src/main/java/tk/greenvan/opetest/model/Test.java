package tk.greenvan.opetest.model;

import androidx.annotation.NonNull;

import java.util.TreeMap;


public class Test {
    private String id;
    private String name;
    private String photoUrl;
    private String file;
    private TreeMap<Integer,Question> questions;

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

    public Test(String id, String name, String photoUrl, String fileName) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
        this.file = fileName;
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

    public void setfileName(String fileName) {
        this.file = fileName;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String fileName) {
        this.file = fileName;
    }

    public String getfileName() {
        return file;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public TreeMap<Integer,Question> getQuestions() {
        return questions;
    }

    public void setQuestions(TreeMap<Integer,Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getName();
    }
}
