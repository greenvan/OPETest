package tk.greenvan.opetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import dmax.dialog.SpotsDialog;
import tk.greenvan.opetest.adapter.QuestionGridAdapter;
import tk.greenvan.opetest.db.Common;
import tk.greenvan.opetest.model.Answer;
import tk.greenvan.opetest.model.Option;
import tk.greenvan.opetest.model.Question;
import tk.greenvan.opetest.model.UserTest;
import tk.greenvan.opetest.util.SpaceDecoration;

public class TestOverviewActivity extends AppCompatActivity {

    TextView tv_progress, tv_result, tv_right_answer_and_total;
    Button btn_filter_total, btn_filter_right, btn_filter_wrong, btn_filter_no_answer;
    Button btn_show_questions, btn_do_test, btn_random_test;

    RecyclerView rv_question_list_grid;
    QuestionGridAdapter questionGridAdapter;
    QuestionGridAdapter filteredQuestionGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_overview);

        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Common.selectedTest.getName());
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
        rv_question_list_grid.setLayoutManager(new GridLayoutManager(this,4));



        //Buscamos el test en la lista de test del usuario. mejor si estuvieran ordenados
        Common.selectedUserTest=new UserTest();
        for(int i=0;i<Common.userTestList.size();i++){
            UserTest u = Common.userTestList.get(i);
            if (Common.selectedTest.getId().equals(u.getTestID())) {
                Common.selectedUserTest = u;

            }
        }

        Common.answerList = Common.selectedUserTest.getAnswerList();

        if (Common.answerList.isEmpty()) {
            loadEmptyAnswerList(); //Cargar lista de preguntas y las respuestas en estado NO_ANSWER
        } else {
            // Si no está vacía, solo cargamos la lista de preguntas
            Common.right_answer_count = Common.selectedUserTest.getRightAnswerCount();
            loadQuestionList();
        }


        //Set Adapter
        questionGridAdapter = new QuestionGridAdapter(this, Common.answerList);
        rv_question_list_grid.addItemDecoration(new SpaceDecoration(4));
        rv_question_list_grid.setAdapter(questionGridAdapter);



        //Inicialmente el filtro no está activado
        Common.filteredAnswerList = Common.answerList;

        //Event filter
        btn_filter_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.filteredAnswerList = Common.answerList;
                if(questionGridAdapter==null)
                    questionGridAdapter = new QuestionGridAdapter(TestOverviewActivity.this, Common.answerList);
                rv_question_list_grid.setAdapter(questionGridAdapter);
            }
        });

        btn_filter_no_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.filteredAnswerList=getFilteredAnswerList(Common.ANSWER_STATE.NO_ANSWER,Common.answerList);
                filteredQuestionGridAdapter = new QuestionGridAdapter(TestOverviewActivity.this, Common.filteredAnswerList);
                rv_question_list_grid.setAdapter(filteredQuestionGridAdapter);
            }
        });

        btn_filter_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.filteredAnswerList=getFilteredAnswerList(Common.ANSWER_STATE.RIGHT_ANSWER,Common.answerList);
                filteredQuestionGridAdapter = new QuestionGridAdapter(TestOverviewActivity.this, Common.filteredAnswerList);
                rv_question_list_grid.setAdapter(filteredQuestionGridAdapter);

            }
        });

        btn_filter_wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.filteredAnswerList=getFilteredAnswerList(Common.ANSWER_STATE.WRONG_ANSWER,Common.answerList);
                filteredQuestionGridAdapter = new QuestionGridAdapter(TestOverviewActivity.this, Common.filteredAnswerList);
                rv_question_list_grid.setAdapter(filteredQuestionGridAdapter);

            }
        });


        //Show filtered questions
        btn_show_questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position=0;
                if (Common.filteredAnswerList.size()>0) {
                    Answer answer = (new ArrayList<Answer>(Common.filteredAnswerList.values())).get(position);
                    Common.selectedQuestion = Common.questionList.get(answer.getQuestionId());  //Assign current questionCommon.selectedIndex = position;
                    Common.viewMode = Common.VIEW_MODE.VIEW;
                    Intent intent = new Intent(v.getContext(), QuestionActivity.class);
                    v.getContext().startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(),R.string.no_questions_selected,Toast.LENGTH_SHORT).show();
                }


            }
        });


        //Do Test with selected questions
        btn_do_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position=0;
                if (Common.filteredAnswerList.size()>0) {
                    Answer answer = (new ArrayList<Answer>(Common.filteredAnswerList.values())).get(position);
                    Common.selectedQuestion = Common.questionList.get(answer.getQuestionId());  //Assign current questionCommon.selectedIndex = position;
                    Common.viewMode = Common.VIEW_MODE.TEST;
                    Intent intent = new Intent(v.getContext(), QuestionActivity.class);
                    v.getContext().startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(),R.string.no_questions_selected,Toast.LENGTH_SHORT).show();
                }


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

    public void loadEmptyAnswerList(){

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
                            Answer answer = new Answer(question.getId(), Common.ANSWER_STATE.NO_ANSWER);
                            Common.answerList.put(answer.getQuestionId(),answer);
                            Common.mUserTestReference.child(Common.selectedTest.getId()).child(String.valueOf(question.getId())).setValue(answer);


                            TreeMap<String,Option> optionList= new TreeMap<>();
                            for (DataSnapshot optionsDataSnapshot:questionDataSnapshot.child("answer").getChildren()) {
                                Option option = optionsDataSnapshot.getValue(Option.class);
                                optionList.put(option.getId(),option);
                            }

                            question.setOptionList(optionList);
                            Common.questionList.put(question.getId(),question);

                        }
                        if (dialog.isShowing())
                            dialog.dismiss();

                        questionGridAdapter.notifyDataSetChanged();

                        //Todas las preguntas están sin contestar
                        Common.right_answer_count =0;

                        //Guardamos en la base de datos
                        UserTest blankTest = new UserTest(Common.username,Common.selectedTest.getId(),Common.answerList);

                        //TODO esto no esta bien eo eo
                        Common.userTestList.add(blankTest);


                        int percent = Common.right_answer_count * 100 / Common.answerList.size() ;
                        tv_progress.setText(percent + "%");
                        tv_result.setText(getResult(percent));
                        tv_right_answer_and_total.setText(Common.right_answer_count+"/"+Common.answerList.size());


                        btn_filter_total.setText(String.valueOf(Common.answerList.size()));
                        btn_filter_right.setText(String.valueOf(Common.right_answer_count));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(TestOverviewActivity.this,""+ databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

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
                for (DataSnapshot questionDataSnapshot:dataSnapshot.getChildren()) {
                    Question question = questionDataSnapshot.getValue(Question.class);

                    TreeMap<String,Option> optionList= new TreeMap<>();
                    for (DataSnapshot optionsDataSnapshot:questionDataSnapshot.child("answer").getChildren()) {
                        Option option = optionsDataSnapshot.getValue(Option.class);
                        optionList.put(option.getId(),option);
                    }

                    question.setOptionList(optionList);
                    Common.questionList.put(question.getId(),question);
                }
                if (dialog.isShowing())
                    dialog.dismiss();

                questionGridAdapter.notifyDataSetChanged();

                //TODO esto se hará fuera con answerListSize en lugar de questionList
                int percent = Common.right_answer_count * 100 / Common.questionList.size() ;
                tv_progress.setText(percent + "%");
                tv_result.setText(getResult(percent));
                tv_right_answer_and_total.setText(Common.right_answer_count+"/"+Common.questionList.size());


                btn_filter_total.setText(String.valueOf(Common.answerList.size()));
                btn_filter_right.setText(String.valueOf(Common.right_answer_count));
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

        return this.getString(R.string.keep_trying);
    }

    public TreeMap<Integer,Answer> getFilteredAnswerList(Common.ANSWER_STATE state, TreeMap<Integer,Answer> originalList){

        TreeMap<Integer,Answer> filteredList = new TreeMap<>();
        Set<Integer> keys = originalList.keySet();

        for (Integer key: keys
        ) {
            Answer a = originalList.get(key);
            if (a.getState() == state) {
                filteredList.put(a.getQuestionId(), a);
            }else{
            }
        }
        return filteredList;
    }

}
