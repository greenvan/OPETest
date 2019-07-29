package tk.greenvan.opetest.model;

import android.util.TimeUtils;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import tk.greenvan.opetest.db.Common;

public class Answer {
    private int questionId = 0;
    private Common.ANSWER_STATE state = Common.ANSWER_STATE.NO_ANSWER;
    private Date lastAcess = new Date(System.currentTimeMillis());

    public Answer() {
    }

    public Answer(int questionId,Common.ANSWER_STATE state) {
        this.questionId = questionId;
        this.state = state;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public Common.ANSWER_STATE getState() {
        return state;
    }

    public void setState(Common.ANSWER_STATE state) {
        this.state = state;
    }

    public Date getLastAcess() {
        return lastAcess;
    }

    public void setLastAcess(Date lastAcess) {
        this.lastAcess = lastAcess;
    }
}
