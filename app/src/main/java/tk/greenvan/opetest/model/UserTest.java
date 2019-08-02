package tk.greenvan.opetest.model;

import java.util.ArrayList;
import java.util.List;

import tk.greenvan.opetest.db.Common;

public class UserTest {
    private String username = "anonymous";
    private String testID = "";
    private List<Answer> answerList = new ArrayList();

    public UserTest() {
    }
    public UserTest(String username, String testID) {
        this.username = username;
        this.testID = testID;
    }

    public UserTest(String username, String testID, List<Answer> answerList) {
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

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public int getRightAnswerCount() {
        int counter=0;
        for (Answer a: this.getAnswerList()
             ) {
            if (a.getState() == Common.ANSWER_STATE.RIGHT_ANSWER)
                counter++;
        }
        return counter;
    }
}
