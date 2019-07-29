package tk.greenvan.opetest.model;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private int id = 0;
    private String text = "";
    private String clue = "";
    private List<Option> options = new ArrayList<>();

    public Question() {
    }

    public Question(int id, String text, String clue, List<Option> options) {
        this.id = id;
        this.text = text;
        this.clue = clue;
        this.options = options;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getClue() {
        return clue;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }

    public List<Option> getOptionList() {
        return options;
    }

    public void setOptionList(List<Option> options) {
        this.options = options;
    }
}
