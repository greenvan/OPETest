package tk.greenvan.opetest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import dmax.dialog.SpotsDialog;
import tk.greenvan.opetest.adapter.QuestionGridAdapter;
import tk.greenvan.opetest.db.Common;
import tk.greenvan.opetest.db.OfflineDB;
import tk.greenvan.opetest.model.Answer;
import tk.greenvan.opetest.model.Option;
import tk.greenvan.opetest.model.Question;
import tk.greenvan.opetest.model.UserTest;
import tk.greenvan.opetest.util.SpaceDecoration;

public class TestOverviewActivity extends AppCompatActivity {

    private static final int CODE_GET_RESULT = 9999;

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


        //Display home arrow in actionBar
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Common.selectedTest.getName());
        }

        // Find all GUI components
        //1. Header
        tv_progress = findViewById(R.id.tv_progress);
        tv_result = findViewById(R.id.tv_result);
        tv_right_answer_and_total = findViewById(R.id.tv_right_answer_and_total);

        //2. Button bar
        btn_show_questions = findViewById(R.id.btn_show_questions);
        btn_do_test = findViewById(R.id.btn_do_test);
        btn_random_test = findViewById(R.id.btn_random_test);

        //3. Filter buttons for question grid
        btn_filter_total = findViewById(R.id.btn_filter_total);
        btn_filter_right = findViewById(R.id.btn_filter_right_answer);
        btn_filter_wrong = findViewById(R.id.btn_filter_wrong_answer);
        btn_filter_no_answer = findViewById(R.id.btn_filter_no_answer);

        //4. Question grid
        rv_question_list_grid = findViewById(R.id.rv_question_list_grid);


        if (Common.Mode == Common.MODE.ONLINE) {//TODO: En modo mixto también sería esta parte online
            //Buscamos el test en la lista de test del usuario. mejor si estuvieran ordenados
            Common.selectedUserTest = new UserTest();
            for (int i = 0; i < Common.userTestList.size(); i++) {
                UserTest u = Common.userTestList.get(i);
                if (Common.selectedTest.getId().equals(u.getTestID())) {
                    Common.selectedUserTest = u;
                }
            }
        } else {

            //TODO en modo OFFLINE mostrar una barra de progreso hasta que se cargue la interfaz

            //Cargamos el test si es OFFLINE desde la base de datos del usuario
            Common.selectedUserTest = new UserTest();

            Common.selectedUserTest.setTestID(Common.selectedTest.getId());
            Common.selectedUserTest.setUsername(Common.username);
            TreeMap<Integer, Answer> answerTreeMap =
                    OfflineDB.getUserAnswers(TestOverviewActivity.this, Common.username, Common.selectedTest.getId());
            Common.selectedUserTest.setAnswerList(answerTreeMap);


        }

        //Cargamos la lista de respuestas del usuario para este test
        Common.answerList = Common.selectedUserTest.getAnswerList();

        //Si no existen respuestas para este test cargamos una lista vacía junto con las preguntas
        if (Common.answerList.isEmpty()) {
            loadQuestionsWithEmptyAnswerList(); //Cargar lista de preguntas y las respuestas en estado NO_ANSWER

            //Todas las preguntas están sin contestar
            Common.right_answer_count = 0;
            Common.wrong_answer_count = 0;
        } else {
            // Si no está vacía, solo cargamos la lista de preguntas
            loadQuestions();
            loadCounters(Common.selectedUserTest);
        }


        //Configuramos el Question grid
        rv_question_list_grid.setHasFixedSize(true);
        rv_question_list_grid.setLayoutManager(new GridLayoutManager(this, 4));
        //Set Adapter
        questionGridAdapter = new QuestionGridAdapter(this, Common.answerList);
        rv_question_list_grid.addItemDecoration(new SpaceDecoration(4));
        rv_question_list_grid.setAdapter(questionGridAdapter);


        // Event filter ---------------------
        //Inicialmente el filtro no está activado
        Common.filteredAnswerList = Common.answerList;

        // No filter, all questions active
        btn_filter_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.filteredAnswerList = Common.answerList;
                if(questionGridAdapter==null)
                    questionGridAdapter = new QuestionGridAdapter(TestOverviewActivity.this, Common.answerList);
                rv_question_list_grid.setAdapter(questionGridAdapter);
            }
        });

        // Filter questions with no answer
        btn_filter_no_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.filteredAnswerList=getFilteredAnswerList(Common.ANSWER_STATE.NO_ANSWER,Common.answerList);
                filteredQuestionGridAdapter = new QuestionGridAdapter(TestOverviewActivity.this, Common.filteredAnswerList);
                rv_question_list_grid.setAdapter(filteredQuestionGridAdapter);
            }
        });

        // Filter right answers
        btn_filter_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.filteredAnswerList=getFilteredAnswerList(Common.ANSWER_STATE.RIGHT_ANSWER,Common.answerList);
                filteredQuestionGridAdapter = new QuestionGridAdapter(TestOverviewActivity.this, Common.filteredAnswerList);
                rv_question_list_grid.setAdapter(filteredQuestionGridAdapter);

            }
        });

        //Filter wrong answers
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
                doTest();
            }
        });

        //Do Quick Test with selected questions
        btn_random_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doQuickTest();
            }
        });

        loadGUIdata();
        if (Common.Mode == Common.MODE.OFFLINE) questionGridAdapter.notifyDataSetChanged();


    }

    private void loadGUIdata() {
        int total = Common.answerList.size();
        int percent = 0;
        if (total != 0)
            percent = Common.right_answer_count * 100 / total;
        tv_progress.setText(percent + "%");
        tv_result.setText(getResult(percent));
        tv_right_answer_and_total.setText(Common.right_answer_count + "/" + Common.answerList.size());


        btn_filter_total.setText(String.valueOf(Common.answerList.size()));
        btn_filter_right.setText(String.valueOf(Common.right_answer_count));
        btn_filter_wrong.setText(String.valueOf(Common.wrong_answer_count));
        btn_filter_no_answer.setText(String.valueOf(Common.answerList.size() - Common.right_answer_count - Common.wrong_answer_count));
    }

    private void doTest() {

        final Context context = TestOverviewActivity.this;
        View test_dialog_layout = LayoutInflater.from(this)
                .inflate(R.layout.new_test_dialog, null);

        //Find elements of the dialog
        final EditText et_num_question = test_dialog_layout.findViewById(R.id.et_num_question);
        final EditText et_start_question = test_dialog_layout.findViewById(R.id.et_start_question);
        final CheckBox ckb_no_answer = test_dialog_layout.findViewById(R.id.ckb_no_answer);
        final CheckBox ckb_right = test_dialog_layout.findViewById(R.id.ckb_right);
        final CheckBox ckb_wrong = test_dialog_layout.findViewById(R.id.ckb_wrong);

        et_num_question.setText("" + Common.answerList.size());

        //Show dialog
        new MaterialStyledDialog.Builder(context)
                .setTitle(getString(R.string.do_quiz))
                .setIcon(R.drawable.ic_mood_black_24dp)
                .setDescription(getString(R.string.do_quiz_and_delete))
                .setCustomView(test_dialog_layout)
                .setNegativeText(getString(R.string.no))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText(getString(R.string.yes))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();

                        Common.viewMode = Common.VIEW_MODE.TEST;
                        //Nueva actividad Question con parámetros
                        //Filtros: num_questions, start_question, include_no_answer, include_right, include_wrong.

                        int num_questions = new Integer(et_num_question.getText().toString()).intValue();
                        int start_question = new Integer(et_start_question.getText().toString()).intValue();

                        boolean include_no_answer = ckb_no_answer.isChecked();
                        boolean include_right = ckb_right.isChecked();
                        boolean include_wrong = ckb_wrong.isChecked();

                        Intent intent = new Intent(context, QuestionActivity.class);
                        intent.putExtra("num_questions", num_questions);
                        intent.putExtra("start_question", start_question);
                        intent.putExtra("include_no_answer", include_no_answer);
                        intent.putExtra("include_right", include_right);
                        intent.putExtra("include_wrong", include_wrong);

                        //Debe obtener un resultado, si al volver del test hay que actualizar campos, lista y etc.
                        //context.startActivity(intent);
                        ((TestOverviewActivity) context).startActivityForResult(intent, CODE_GET_RESULT);

                    }
                }).show();


    }

    private void doQuickTest() {

        final Context context = TestOverviewActivity.this;
        View test_dialog_layout = LayoutInflater.from(this)
                .inflate(R.layout.new_quick_test_dialog, null);

        //Find elements of the dialog
        final CheckBox ckb_no_answer = test_dialog_layout.findViewById(R.id.ckb_no_answer);
        final CheckBox ckb_right = test_dialog_layout.findViewById(R.id.ckb_right);
        final CheckBox ckb_wrong = test_dialog_layout.findViewById(R.id.ckb_wrong);

        //Show dialog
        new MaterialStyledDialog.Builder(context)
                .setTitle(getString(R.string.do_quiz))
                .setIcon(R.drawable.ic_mood_black_24dp)
                .setDescription(getString(R.string.do_quiz_and_delete))
                .setCustomView(test_dialog_layout)
                .setNegativeText(getString(R.string.no))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText(getString(R.string.yes))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();

                        Common.viewMode = Common.VIEW_MODE.QUICKTEST;
                        //Nueva actividad Question con parámetros
                        //Filtros:  include_no_answer, include_right, include_wrong.

                        boolean include_no_answer = ckb_no_answer.isChecked();
                        boolean include_right = ckb_right.isChecked();
                        boolean include_wrong = ckb_wrong.isChecked();

                        Intent intent = new Intent(context, QuestionActivity.class);
                        intent.putExtra("include_no_answer", include_no_answer);
                        intent.putExtra("include_right", include_right);
                        intent.putExtra("include_wrong", include_wrong);

                        //Debe obtener un resultado, si al volver del test hay que actualizar campos, lista y etc.
                        //context.startActivity(intent);
                        ((TestOverviewActivity) context).startActivityForResult(intent, CODE_GET_RESULT);

                    }
                }).show();


    }


    private void loadCounters(UserTest userTest) {
        Common.right_answer_count = 0;
        Common.wrong_answer_count = 0;
        Set<Integer> keys = userTest.getAnswerList().keySet();
        for (Integer key : keys
        ) {
            switch (userTest.getAnswerList().get(key).getState()) {
                case RIGHT_ANSWER:
                    Common.right_answer_count++;
                    break;
                case WRONG_ANSWER:
                    Common.wrong_answer_count++;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public void loadQuestionsWithEmptyAnswerList() {
        if (Common.Mode == Common.MODE.ONLINE) {
            onlineLoadQuestionsWithEmptyAnswerList();
        } else {
            OfflineDB.loadQuestions(TestOverviewActivity.this, Common.selectedTest.getfileName(), true);
            //Debemos guardar los datos con la lista de preguntas vacía en la base de datos para que al terminar
            //el test sólo guarde las preguntas modificadas.
            OfflineDB.saveData(TestOverviewActivity.this, Common.username, Common.selectedTest.getId(), Common.answerList);
        }
    }

    public void loadQuestions() {
        if (Common.Mode == Common.MODE.ONLINE) {
            onlineLoadQuestions();
        } else {
            OfflineDB.loadQuestions(TestOverviewActivity.this, Common.selectedTest.getfileName(), false);

        }
    }


    public void onlineLoadQuestionsWithEmptyAnswerList() {

        //TODO si cancela y por ejemplo no tiene conexión ver qué podemos hacer...
        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(TestOverviewActivity.this)
                .setCancelable(true)
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
                            //Guardar en la base de datos
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

                        //Actualizar la interfaz gráfica
                        questionGridAdapter.notifyDataSetChanged();
                        loadGUIdata();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(TestOverviewActivity.this,""+ databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void onlineLoadQuestions() {

        //TODO si cancela y por ejemplo no tiene conexión ver qué podemos hacer...
        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(TestOverviewActivity.this)
                .setCancelable(true)
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
            }
        }
        return filteredList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Common.filteredAnswerList = Common.answerList;
        loadCounters(Common.selectedUserTest);
        loadGUIdata();
        questionGridAdapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
