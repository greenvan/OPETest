package tk.greenvan.opetest.model;

public class Option {
    private String id = "";
    private String text = "";
    private boolean correct = false;

    public Option() {
    }

    public Option(String id, String text, boolean correct) {
        this.id = id;
        this.text = text;
        this.correct = correct;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
