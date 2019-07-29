package tk.greenvan.opetest.model;

public class Option {
    private String id = "";
    private String text = "";
    private boolean correcto = false;

    public Option() {
    }

    public Option(String id, String text, boolean correcto) {
        this.id = id;
        this.text = text;
        this.correcto = correcto;
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

    public boolean isCorrecto() {
        return correcto;
    }

    public void setCorrecto(boolean correcto) {
        this.correcto = correcto;
    }
}
