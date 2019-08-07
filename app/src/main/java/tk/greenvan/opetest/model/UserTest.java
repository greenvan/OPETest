package tk.greenvan.opetest.model;

import java.util.Set;
import java.util.TreeMap;

import tk.greenvan.opetest.db.Common;

public class UserTest {
    private String username = "anonymous";
    private String testID = "";
    private TreeMap<Integer,Answer> answerList = new TreeMap<Integer,Answer> ();

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

    public int getRightAnswerCount() {
        int counter=0;
        Set<Integer> keys = this.getAnswerList().keySet();
        for (Integer key: keys
             ) {
            if (this.getAnswerList().get(key).getState() == Common.ANSWER_STATE.RIGHT_ANSWER)
                counter++;
        }
        return counter;
    }
}
