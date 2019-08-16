package tk.greenvan.opetest;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import tk.greenvan.opetest.common.Common;
import tk.greenvan.opetest.model.Answer;
import tk.greenvan.opetest.model.Option;
import tk.greenvan.opetest.model.Question;
import tk.greenvan.opetest.ui.question.QuestionFragment;
import tk.greenvan.opetest.ui.question.QuestionFragmentAdapter;

public class QuestionActivity extends AppCompatActivity {

    public TextView tv_right_answer_count, tv_wrong_answer_count, tv_timer;
    public TextView tv_bottom_sheet_title, tv_bottom_sheet_detail;
    int time_play = Common.TOTAL_TIME_QUICK_TEST;
    private BottomSheetDialog mBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        final TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(Common.selectedTest.getName());


        tv_right_answer_count = findViewById(R.id.tv_right_answer_count);
        tv_wrong_answer_count = findViewById(R.id.tv_wrong_answer_count);
        tv_timer = findViewById(R.id.tv_timer);

        final ViewPager viewPager = findViewById(R.id.vp_question_view_pager);
        final TabLayout tabs = findViewById(R.id.tl_question_tabs);
        tabs.setupWithViewPager(viewPager);


        /* Bottom sheet */
        final View bottomSheetLayout = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        tv_bottom_sheet_title = bottomSheetLayout.findViewById(R.id.tv_bottom_sheet_title);
        tv_bottom_sheet_detail = bottomSheetLayout.findViewById(R.id.tv_bottom_sheet_detail);

        (bottomSheetLayout.findViewById(R.id.btn_bottom_sheet_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(bottomSheetLayout);
        /* END of bottom sheet*/

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TabLayout.Tab currentTab = tabs.getTabAt(tabs.getSelectedTabPosition());
                Question q = Common.questionList.get(new Integer(currentTab.getText().toString()));


                String clue = q.getClue();

                if(clue.isEmpty()){
                    Set<String> keys = q.getOptionList().keySet();
                    for (String key: keys
                    ) {
                        Option o = q.getOptionList().get(key);
                        if (o.isCorrect())
                            clue = o.getId() + ". " + o.getText();
                    }
                }

                tv_bottom_sheet_title.setText(getString(R.string.question) + " " + q.getId());
                tv_bottom_sheet_detail.setText(clue);


                Snackbar.make(view, clue, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.more), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mBottomSheetDialog.show();
                            }
                        }).show();
            }
        });

        final View check_finish_game = findViewById(R.id.check_finish_game);
        check_finish_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishGame();
            }
        });

        if (Common.viewMode == Common.VIEW_MODE.VIEW) {
            //Escondemos los contadores
            LinearLayout ll_counters = findViewById(R.id.ll_counters);
            ll_counters.setVisibility(View.GONE);
        } else {

            Common.right_answer_count = 0;
            Common.wrong_answer_count = 0;

            if (Common.viewMode == Common.VIEW_MODE.TEST) {
                //Escondemos el timer
                tv_timer.setVisibility(View.GONE);
                loadAnswerListForTest(this.getIntent());
            } else {
                //Estamos en quicktestmod
                //Escondemos el fab
                fab.hide();
                loadAnswerListForQuickTest(this.getIntent());
            }
        }

        if (Common.filteredAnswerList.size() < 1) {

            new MaterialStyledDialog.Builder(QuestionActivity.this)
                    .setTitle(getString(R.string.no_questions_selected))
                    .setCancelable(false)// Si el usuario pulsa fuera del diálogo no termina la actividad.
                    .setIcon(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                    .setDescription(getString(R.string.no_questions_selected_message))
                    .setPositiveText(getString(R.string.ok))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();

        }

        genFragmentList();


        QuestionFragmentAdapter questionFragmentAdapter = new QuestionFragmentAdapter(this, getSupportFragmentManager(),Common.fragmentList);
        viewPager.setAdapter(questionFragmentAdapter);


        //En modo VIEW Hay que activar como seleccionado el fragment que coincide con selected question.
        if (Common.viewMode == Common.VIEW_MODE.VIEW)
            viewPager.setCurrentItem(Common.selectedIndex); //TODO Esto no funciona bien, arreglar
        else
            viewPager.setCurrentItem(0);

        //Si es un test rápido ponemos en marcha la cuenta atrás
        if (Common.viewMode == Common.VIEW_MODE.QUICKTEST) {
            countTimer();

        }


    }

    private void genFragmentList() {
        Common.fragmentList.clear();
        Set<Integer> keys = Common.filteredAnswerList.keySet();
        for (Integer key: keys){
            Bundle bundle = new Bundle();
            bundle.putInt("question_id",key);
            QuestionFragment fragment = new QuestionFragment();
            fragment.setArguments(bundle);
            Common.fragmentList.add(fragment);
        }
    }

    public void loadAnswerListForTest(Intent data) {

        Common.filteredAnswerList = new TreeMap<>();

        Common.selectedQuestion = null;
        //La lista de respuestas está ordenada porque es un TreeMap
        Set<Integer> keys = Common.answerList.keySet();

        int start_question = data.getIntExtra("start_question", 1);
        int num_questions = data.getIntExtra("num_questions", Common.answerList.size());

        boolean include_no_answer = data.getBooleanExtra("include_no_answer", true);
        boolean include_right = data.getBooleanExtra("include_right", true);
        boolean include_wrong = data.getBooleanExtra("include_wrong", true);


        int counter = 0;
        for (Integer key : keys
        ) {
            //Si el índice es mayor a start_question y aún hay preguntas por añadir añadimos la Answer a la lista en función de los checkbox marcados

            if (key >= start_question && counter < num_questions) {
                if (Common.selectedQuestion == null)
                    Common.selectedQuestion = Common.questionList.get(key);
                Answer a = Common.answerList.get(key);
                Common.ANSWER_STATE state = a.getState();

                if ((state == Common.ANSWER_STATE.NO_ANSWER) && include_no_answer) {
                    Common.filteredAnswerList.put(key, a);
                    counter++;
                }
                if ((state == Common.ANSWER_STATE.RIGHT_ANSWER) && include_right) {
                    //Limpiamos el estado porque el test empieza en blanco
                    a.setState(Common.ANSWER_STATE.NO_ANSWER);
                    Common.filteredAnswerList.put(key, a);
                    counter++;
                }
                if ((state == Common.ANSWER_STATE.WRONG_ANSWER) && include_wrong) {
                    //Limpiamos el estado porque el test empieza en blanco
                    a.setState(Common.ANSWER_STATE.NO_ANSWER);
                    Common.filteredAnswerList.put(key, a);
                    counter++;
                }

            }
        }


    }


    public void loadAnswerListForQuickTest(Intent data) {

        Common.filteredAnswerList = new TreeMap<>();
        ArrayList<Answer> answerArrayList = new ArrayList<>();
        Common.selectedQuestion = null;
        //La lista de respuestas está ordenada porque es un TreeMap
        Set<Integer> keys = Common.answerList.keySet();


        boolean include_no_answer = data.getBooleanExtra("include_no_answer", true);
        boolean include_right = data.getBooleanExtra("include_right", true);
        boolean include_wrong = data.getBooleanExtra("include_wrong", true);


        for (Integer key : keys
        ) {
            Answer a = Common.answerList.get(key);
            Common.ANSWER_STATE state = a.getState();

            if ((state == Common.ANSWER_STATE.NO_ANSWER) && include_no_answer)
                answerArrayList.add(a);
            if ((state == Common.ANSWER_STATE.RIGHT_ANSWER) && include_right) {
                answerArrayList.add(a);
            }
            if ((state == Common.ANSWER_STATE.WRONG_ANSWER) && include_wrong) {
                answerArrayList.add(a);
            }

        }

        //Si el array no está vacío, continuamos
        if (answerArrayList.size() > 0) {

            //Ya tenemos el array lleno. Lo mezclamos de manera aleatoria
            Collections.shuffle(answerArrayList);

            //Tomamos los Common.NUM_QUESTIONS_QUICK_TEST (30) primeros elmentos si es que hay

            for (int i = 0; i < Common.NUM_QUESTIONS_QUICK_TEST && i < answerArrayList.size(); i++) {
                Answer a = answerArrayList.get(i);

                //Limpiamos el estado porque el test empieza en blanco
                a.setState(Common.ANSWER_STATE.NO_ANSWER);
                Common.filteredAnswerList.put(a.getQuestionId(), a);
            }


        }

    }


    public void finishGame() {


        if (Common.viewMode == Common.VIEW_MODE.VIEW)
            finish();
        else {
            new MaterialStyledDialog.Builder(this)
                    .setTitle(getString(R.string.finish_game))
                    .setIcon(R.drawable.ic_mood_black_24dp)
                    .setDescription(getString(R.string.really_finish_game))
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
                            if (Common.countDownTimer != null)
                                Common.countDownTimer.cancel();
                            saveData();
                            finish();
                        }
                    }).show();


        }
    }

    public void saveData() {
        //TODO mostrar un cartel de "Saving..."

        // Guardar los datos de filteredList en answerList y la base de datos
        Set<Integer> keys = Common.filteredAnswerList.keySet();
        for (Integer key : keys) {
            Answer answer = Common.filteredAnswerList.get(key);
            Common.mUserTestReference.child(Common.selectedTest.getId()).child(String.valueOf(answer.getQuestionId())).setValue(answer);
        }


    }

    private void countTimer() {
        if (Common.countDownTimer != null)
            Common.countDownTimer.cancel();

        Common.countDownTimer = new CountDownTimer(Common.TOTAL_TIME_QUICK_TEST, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_timer.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds((TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))));
                time_play -= 1000;
            }

            @Override
            public void onFinish() {
                new MaterialStyledDialog.Builder(QuestionActivity.this)
                        .setTitle(getString(R.string.time_up_title))
                        .setCancelable(false)//si el usuario no pulsa ok (pulsa fuera del diálogo) se queda en un estado en que no funciona el timer pero sigue funcionando el test
                        .setIcon(R.drawable.ic_access_alarm_black_24dp)
                        .setDescription(getString(R.string.time_up_message))
                        .setPositiveText(getString(R.string.ok))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                saveData();
                                finish();
                            }
                        }).show();

            }
        };
        Common.countDownTimer.start();


    }


    @Override
    public void onBackPressed() {
        if (Common.viewMode == Common.VIEW_MODE.VIEW)
            super.onBackPressed();
        else
            finishGame();
    }

    //TODO Si se rota la pantalla en modo test no se guarda las respuestas obtenidas en cada momento currentAnswersheet y filtered habría que guardarlas al pause y recuperar al resume etc o bien no permitir rotar.

    //TODO Si se rota en quicktest se muere la actividad.

    @Override
    protected void onDestroy() {

        if (Common.countDownTimer != null)
            Common.countDownTimer.cancel();

        super.onDestroy();
    }
}