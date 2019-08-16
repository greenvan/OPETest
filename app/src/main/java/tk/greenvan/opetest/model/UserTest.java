package tk.greenvan.opetest.model;

import java.util.TreeMap;

public class UserTest {
    private String username = "anonymous";
    private String testID = "";
    private TreeMap<Integer, Answer> answerList = new TreeMap<>();

    public UserTest() {
    }
    public UserTest(String username, String testID) {
        this.username = username;
        this.testID = testID;
    }

    public UserTest(String username, String testID, TreeMap<Integer,Answer> answerList) {
        this.username = username;
        this.testID = testID;
        this.answerList = answerList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public TreeMap<Integer,Answer>  getAnswerList() {
        return answerList;
    }

    public void setAnswerList(TreeMap<Integer,Answer>  answerList) {
        this.answerList = answerList;
    }

}
