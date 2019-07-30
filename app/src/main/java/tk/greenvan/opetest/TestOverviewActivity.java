package tk.greenvan.opetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
import tk.greenvan.opetest.adapter.QuestionGridAdapter;
import tk.greenvan.opetest.db.Common;
import tk.greenvan.opetest.model.Answer;
import tk.greenvan.opetest.model.Question;
import tk.greenvan.opetest.model.Test;
import tk.greenvan.opetest.util.SpaceDecoration;

public class TestOverviewActivity extends AppCompatActivity {

    TextView tv_progress, tv_result, tv_right_answer_and_total;
    Button btn_filter_total, btn_filter_right, btn_filter_wrong, btn_filter_no_answer;
    Button btn_show_questions, btn_do_test, btn_random_test;

    RecyclerView rv_question_list_grid;
    QuestionGridAdapter questionGridAdapter;
    QuestionGridAdapter fiteredQuestionGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_overview);

        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        tv_progress = (TextView)findViewById(R.id.tv_progress);
        tv_result = (TextView)findViewById(R.id.tv_result);
        tv_right_answer_and_total = (TextView)findViewById(R.id.tv_right_answer_and_total);

        btn_filter_total = (Button)findViewById(R.id.btn_filter_total);
        btn_filter_right = (Button)findViewById(R.id.btn_filter_right_answer);
        btn_filter_wrong = (Button)findViewById(R.id.btn_filter_wrong_answer);
        btn_filter_no_answer = (Button)findViewById(R.id.btn_filter_no_answer);

        btn_show_questions = (Button)findViewById(R.id.btn_show_questions);
        btn_do_test = (Button)findViewById(R.id.btn_do_test);
        btn_random_test = (Button)findViewById(R.id.btn_random_test);

        rv_question_list_grid = (RecyclerView)findViewById(R.id.rv_question_list_grid);
        rv_question_list_grid.setHasFixedSize(true);
        rv_question_list_grid.setLayoutManager(new GridLayoutManager(this,5));


        loadQuestionList();

        //Set Adapter
        questionGridAdapter = new QuestionGridAdapter(this, Common.answerList);
        rv_question_list_grid.addItemDecoration(new SpaceDecoration(4));
        rv_question_list_grid.setAdapter(questionGridAdapter);


        //Event filter
        btn_filter_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.filteredAnswerList.clear();
                Common.filteredQuestionList.clear();
                if(questionGridAdapter==null)
                    questionGridAdapter = new QuestionGridAdapter(TestOverviewActivity.this, Common.answerList);

                rv_question_list_grid.setAdapter(questionGridAdapter);
            }
        });

        btn_filter_no_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.filteredAnswerList.clear();
                Common.filteredQuestionList.clear();
                for(int i=0;i<Common.answerList.size();i++){
                    if(Common.answerList.get(i).getState()==Common.ANSWER_STATE.NO_ANSWER) {
                        Common.filteredAnswerList.add(Common.answerList.get(i));
                        Common.filteredQuestionList.add(Common.questionList.get(i));
                    }
                }
                fiteredQuestionGridAdapter = new QuestionGridAdapter(TestOverviewActivity.this, Common.filteredAnswerList);
                rv_question_list_grid.setAdapter(fiteredQuestionGridAdapter);
            }
        });

        btn_filter_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.filteredAnswerList.clear();
                Common.filteredQuestionList.clear();
                for(int i=0;i<Common.answerList.size();i++){
                    if(Common.answerList.get(i).getState()==Common.ANSWER_STATE.RIGHT_ANSWER) {
                        Common.filteredAnswerList.add(Common.answerList.get(i));
                        Common.filteredQuestionList.add(Common.questionList.get(i));
                    }
                }
                fiteredQuestionGridAdapter = new QuestionGridAdapter(TestOverviewActivity.this, Common.filteredAnswerList);
                rv_question_list_grid.setAdapter(fiteredQuestionGridAdapter);

            }
        });

        btn_filter_wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.filteredAnswerList.clear();
                Common.filteredQuestionList.clear();
                for(int i=0;i<Common.answerList.size();i++){
                    if(Common.answerList.get(i).getState()==Common.ANSWER_STATE.WRONG_ANSWER) {
                        Common.filteredAnswerList.add(Common.answerList.get(i));
                        Common.filteredQuestionList.add(Common.questionList.get(i));
                    }
                }
                fiteredQuestionGridAdapter = new QuestionGridAdapter(TestOverviewActivity.this, Common.filteredAnswerList);
                rv_question_list_grid.setAdapter(fiteredQuestionGridAdapter);

            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public void loadQuestionList(){

        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(TestOverviewActivity.this)
                .setCancelable(false)
                .build();

        if(!dialog.isShowing())
            dialog.show();

        Common.mTestReference
                .child(Common.selectedTest.getId())
                .child("question")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Common.questionList.clear();
                Common.answerList.clear();
                for (DataSnapshot questionDataSnapshot:dataSnapshot.getChildren()) {
                    Question question = questionDataSnapshot.getValue(Question.class);
                    Common.questionList.add(question);

                    //TODO get real answer List and not this dummy data
                    Common.ANSWER_STATE state = Common.ANSWER_STATE.NO_ANSWER;
                    if (question.getId() > 100)
                        state = Common.ANSWER_STATE.WRONG_ANSWER;
                    else if (question.getId() > 50)
                        state = Common.ANSWER_STATE.RIGHT_ANSWER;

                    Common.answerList.add(new Answer(question.getId(), state));
                }
                if (dialog.isShowing())
                    dialog.dismiss();

                questionGridAdapter.notifyDataSetChanged();

                int percent = Common.right_answer_count * 100 / Common.questionList.size() ;
                tv_progress.setText(percent + "%");
                tv_result.setText(getResult(percent));
                tv_right_answer_and_total.setText(Common.right_answer_count+"/"+Common.questionList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TestOverviewActivity.this,""+ databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getResult(int percent){
        if (percent> 90)
            return this.getString(R.string.excellent);
        if (percent> 60)
            return this.getString(R.string.good);
        if (percent> 50)
            return this.getString(R.string.fair);
        if (percent> 40)
            return this.getString(R.string.poor);

        return this.getString(R.string.bad);
    }
    public void loadDummyData(){
        Common.answerList.add(new Answer(10001, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(10002, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(10003, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(10004, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(100015, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(100016, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(100017, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(100018, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(100021, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(100031, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(100041, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(100051, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(10001, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(10002, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(3, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(4, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(15, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(16, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(17, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(18, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(21, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(31, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(41, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(51, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(1, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(2, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(3, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(4, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(15, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(16, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(17, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(18, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(21, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(31, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(41, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(51, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(1, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(2, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(3, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(4, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(15, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(16, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(17, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(18, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(21, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(31, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(41, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(51, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(1, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(2, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(3, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(4, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(15, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(16, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(17, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(18, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(21, Common.ANSWER_STATE.WRONG_ANSWER));
        Common.answerList.add(new Answer(31, Common.ANSWER_STATE.NO_ANSWER));
        Common.answerList.add(new Answer(41, Common.ANSWER_STATE.RIGHT_ANSWER));
        Common.answerList.add(new Answer(51, Common.ANSWER_STATE.RIGHT_ANSWER));


    }
}
