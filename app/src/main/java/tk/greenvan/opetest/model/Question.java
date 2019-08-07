package tk.greenvan.opetest.model;



import java.util.TreeMap;

public class Question {
    private int id = 0;
    private String text = "";
    private String clue = "";
    private TreeMap<String,Option> options = new TreeMap<>();

    public Question() {
    }

    public Question(int id, String text, String clue, TreeMap<String,Option> options) {
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

    public TreeMap<String,Option> getOptionList() {
        return options;
    }

    public void setOptionList(TreeMap<String,Option> options) {
        this.options = options;
    }


}
