package tk.greenvan.opetest.ui.question;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.TreeMap;

import tk.greenvan.opetest.QuestionActivity;
import tk.greenvan.opetest.R;
import tk.greenvan.opetest.db.Common;
import tk.greenvan.opetest.model.Answer;
import tk.greenvan.opetest.model.Option;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "question_id";
    private PageViewModel pageViewModel;

    private TreeMap<String, Option> options = new TreeMap<String, Option>();


    public static QuestionFragment newInstance(int index) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_question, container, false);

        final QuestionActivity questionActivity = (QuestionActivity) getActivity();

        final TextView textView = rootView.findViewById(R.id.tv_question_text);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        Integer questionID = pageViewModel.getIndex().getValue();
        final TreeMap<String, Option> currentOptionList = Common.questionList.get(questionID).getOptionList();
        final Answer currentAnswer = Common.filteredAnswerList.get(questionID);

        final TextView tv_A = rootView.findViewById(R.id.tv_option_a);
        final TextView tv_B = rootView.findViewById(R.id.tv_option_b);
        final TextView tv_C = rootView.findViewById(R.id.tv_option_c);
        final TextView tv_D = rootView.findViewById(R.id.tv_option_d);
        final TextView tv_E = rootView.findViewById(R.id.tv_option_e);


        //TODO en todas las opciones (A-B-C-D-E), si se clicka y es correcta pasar al siguiente frame,
        // si es incorrecta aún tengo que pensar si pasar al siguiente o no.

        //TextView tv_A
        pageViewModel.getOptionA().observe(this, new Observer<Option>() {
            @Override
            public void onChanged(@Nullable final Option o) {
                if (o != null) {

                    tv_A.setText(o.getId() + ". " + o.getText());
                    tv_A.setVisibility(View.VISIBLE);

                    if (Common.viewMode == Common.VIEW_MODE.VIEW) {
                        if (o.isCorrect())
                            tv_A.setBackgroundColor(Color.parseColor("#99cc00"));
                        else //Por si acaso volvemos a poner en modo no_answer
                            tv_A.setBackgroundColor(Color.parseColor("#00ddff"));
                    } else {
                        //Estamos en modo TEST o QUICKTEST

                        //Si la pregunta no ha sido contestada añadimos un clicklistener
                        if (currentAnswer.getState() == Common.ANSWER_STATE.NO_ANSWER) {

                            tv_A.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //Desactivar los 5 onClickListeners
                                    tv_A.setClickable(false);
                                    tv_B.setClickable(false);
                                    tv_C.setClickable(false);
                                    tv_D.setClickable(false);
                                    tv_E.setClickable(false);

                                    // Si es correcta, la marcamos en verde y actualizamos contador
                                    if (o.isCorrect()) {
                                        tv_A.setBackgroundColor(Color.parseColor("#99cc00"));
                                        currentAnswer.setState(Common.ANSWER_STATE.RIGHT_ANSWER);
                                        //Actualizar contador
                                        Common.right_answer_count++;
                                        questionActivity.tv_right_answer_count.setText(String.valueOf(Common.right_answer_count));

                                    }
                                    // Si no es correcta, la marcamos en rojo y actualizamos contaado
                                    else {
                                        tv_A.setBackgroundColor(Color.parseColor("#ff0000"));
                                        currentAnswer.setState(Common.ANSWER_STATE.WRONG_ANSWER);
                                        //Actualizar contador
                                        Common.wrong_answer_count++;
                                        questionActivity.tv_wrong_answer_count.setText(String.valueOf(Common.wrong_answer_count));

                                        //Buscamos la correcta para marcarla en verde
                                        if (currentOptionList.containsKey("B") && currentOptionList.get("B").isCorrect())
                                            tv_B.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("C") && currentOptionList.get("C").isCorrect())
                                            tv_C.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("D") && currentOptionList.get("D").isCorrect())
                                            tv_D.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("E") && currentOptionList.get("E").isCorrect())
                                            tv_E.setBackgroundColor(Color.parseColor("#99cc00"));

                                    }


                                    Common.filteredAnswerList.put(currentAnswer.getQuestionId(), currentAnswer);
                                    Common.currentAnswerSheet.put(currentAnswer.getQuestionId(), o.getId());

                                }
                            });

                        } else {
                            //La pregunta ya ha sido contestada
                            String currentSelectedOption = Common.currentAnswerSheet.get(currentAnswer.getQuestionId());
                            //La correcta la marcamos siempre en verde
                            if (o.isCorrect())
                                tv_A.setBackgroundColor(Color.parseColor("#99cc00"));
                            else if (currentSelectedOption.equals(o.getId())) {
                                //Si la opción no es correcta pero es la elegida la marcamos en rojo
                                tv_A.setBackgroundColor(Color.parseColor("#ff0000"));
                            }
                        }
                    }


                } else {
                    tv_A.setVisibility(View.GONE);
                }
            }
        });


        //TextView tv_B
        pageViewModel.getOptionB().observe(this, new Observer<Option>() {
            @Override
            public void onChanged(@Nullable final Option o) {
                if (o != null) {
                    tv_B.setText(o.getId() + ". " + o.getText());
                    tv_B.setVisibility(View.VISIBLE);

                    if (Common.viewMode == Common.VIEW_MODE.VIEW) {
                        if (o.isCorrect())
                            tv_B.setBackgroundColor(Color.parseColor("#99cc00"));
                        else //Por si acaso volvemos a poner en modo no_answer
                            tv_B.setBackgroundColor(Color.parseColor("#00ddff"));
                    } else {
                        //Estamos en modo TEST o QUICKTEST

                        //Si la pregunta no ha sido contestada añadimos un clicklistener
                        if (currentAnswer.getState() == Common.ANSWER_STATE.NO_ANSWER) {

                            tv_B.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //Desactivar los 5 onClickListeners
                                    tv_A.setClickable(false);
                                    tv_B.setClickable(false);
                                    tv_C.setClickable(false);
                                    tv_D.setClickable(false);
                                    tv_E.setClickable(false);

                                    // Si es correcta, la marcamos en verde y actualizamos contador
                                    if (o.isCorrect()) {
                                        tv_B.setBackgroundColor(Color.parseColor("#99cc00"));
                                        currentAnswer.setState(Common.ANSWER_STATE.RIGHT_ANSWER);
                                        //Actualizar contador
                                        Common.right_answer_count++;
                                        questionActivity.tv_right_answer_count.setText(String.valueOf(Common.right_answer_count));

                                    }
                                    // Si no es correcta, buscamos la correcta y la marcamos en rojo y actualizamos contaado
                                    else {
                                        tv_B.setBackgroundColor(Color.parseColor("#ff0000"));
                                        currentAnswer.setState(Common.ANSWER_STATE.WRONG_ANSWER);
                                        //Actualizar contador
                                        Common.wrong_answer_count++;
                                        questionActivity.tv_wrong_answer_count.setText(String.valueOf(Common.wrong_answer_count));

                                        //Buscamos la correcta para marcarla en verde
                                        if (currentOptionList.containsKey("A") && currentOptionList.get("A").isCorrect())
                                            tv_A.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("C") && currentOptionList.get("C").isCorrect())
                                            tv_C.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("D") && currentOptionList.get("D").isCorrect())
                                            tv_D.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("E") && currentOptionList.get("E").isCorrect())
                                            tv_E.setBackgroundColor(Color.parseColor("#99cc00"));
                                    }

                                    Common.filteredAnswerList.put(currentAnswer.getQuestionId(), currentAnswer);
                                    Common.currentAnswerSheet.put(currentAnswer.getQuestionId(), o.getId());

                                }
                            });

                        } else {
                            //La pregunta ya ha sido contestada
                            String currentSelectedOption = Common.currentAnswerSheet.get(currentAnswer.getQuestionId());
                            //La correcta la marcamos siempre en verde
                            if (o.isCorrect())
                                tv_B.setBackgroundColor(Color.parseColor("#99cc00"));
                            else if (currentSelectedOption.equals(o.getId())) {
                                //Si la opción no es correcta pero es la elegida la marcamos en rojo
                                tv_B.setBackgroundColor(Color.parseColor("#ff0000"));
                            }
                        }
                    }


                } else {
                    tv_B.setVisibility(View.GONE);
                }
            }
        });

        //TextView tv_C
        pageViewModel.getOptionC().observe(this, new Observer<Option>() {
            @Override
            public void onChanged(@Nullable final Option o) {
                if (o != null) {
                    tv_C.setText(o.getId() + ". " + o.getText());
                    tv_C.setVisibility(View.VISIBLE);

                    if (Common.viewMode == Common.VIEW_MODE.VIEW) {
                        if (o.isCorrect())
                            tv_C.setBackgroundColor(Color.parseColor("#99cc00"));
                        else //Por si acaso volvemos a poner en modo no_answer
                            tv_C.setBackgroundColor(Color.parseColor("#00ddff"));
                    } else {
                        //Estamos en modo TEST o QUICKTEST

                        //Si la pregunta no ha sido contestada añadimos un clicklistener
                        if (currentAnswer.getState() == Common.ANSWER_STATE.NO_ANSWER) {

                            tv_C.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //Desactivar los 5 onClickListeners
                                    tv_A.setClickable(false);
                                    tv_B.setClickable(false);
                                    tv_C.setClickable(false);
                                    tv_D.setClickable(false);
                                    tv_E.setClickable(false);

                                    // Si es correcta, la marcamos en verde y actualizamos contador
                                    if (o.isCorrect()) {
                                        tv_C.setBackgroundColor(Color.parseColor("#99cc00"));
                                        currentAnswer.setState(Common.ANSWER_STATE.RIGHT_ANSWER);
                                        //Actualizar contador
                                        Common.right_answer_count++;
                                        questionActivity.tv_right_answer_count.setText(String.valueOf(Common.right_answer_count));

                                    }
                                    // Si no es correcta, buscamos la correcta y la marcamos en rojo y actualizamos contaado
                                    else {
                                        tv_C.setBackgroundColor(Color.parseColor("#ff0000"));
                                        currentAnswer.setState(Common.ANSWER_STATE.WRONG_ANSWER);
                                        //Actualizar contador
                                        Common.wrong_answer_count++;
                                        questionActivity.tv_wrong_answer_count.setText(String.valueOf(Common.wrong_answer_count));

                                        //Buscamos la correcta para marcarla en verde
                                        if (currentOptionList.containsKey("A") && currentOptionList.get("A").isCorrect())
                                            tv_A.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("B") && currentOptionList.get("B").isCorrect())
                                            tv_B.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("D") && currentOptionList.get("D").isCorrect())
                                            tv_D.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("E") && currentOptionList.get("E").isCorrect())
                                            tv_E.setBackgroundColor(Color.parseColor("#99cc00"));
                                    }

                                    Common.filteredAnswerList.put(currentAnswer.getQuestionId(), currentAnswer);
                                    Common.currentAnswerSheet.put(currentAnswer.getQuestionId(), o.getId());

                                }
                            });

                        } else {
                            //La pregunta ya ha sido contestada
                            String currentSelectedOption = Common.currentAnswerSheet.get(currentAnswer.getQuestionId());
                            //La correcta la marcamos siempre en verde
                            if (o.isCorrect())
                                tv_C.setBackgroundColor(Color.parseColor("#99cc00"));
                            else if (currentSelectedOption.equals(o.getId())) {
                                //Si la opción no es correcta pero es la elegida la marcamos en rojo
                                tv_C.setBackgroundColor(Color.parseColor("#ff0000"));
                            }
                        }
                    }


                } else {
                    tv_C.setVisibility(View.GONE);
                }
            }
        });


        //TextView tv_D
        pageViewModel.getOptionD().observe(this, new Observer<Option>() {
            @Override
            public void onChanged(@Nullable final Option o) {
                if (o != null) {
                    tv_D.setText(o.getId() + ". " + o.getText());
                    tv_D.setVisibility(View.VISIBLE);

                    if (Common.viewMode == Common.VIEW_MODE.VIEW) {
                        if (o.isCorrect())
                            tv_D.setBackgroundColor(Color.parseColor("#99cc00"));
                        else //Por si acaso volvemos a poner en modo no_answer
                            tv_D.setBackgroundColor(Color.parseColor("#00ddff"));
                    } else {
                        //Estamos en modo TEST o QUICKTEST

                        //Si la pregunta no ha sido contestada añadimos un clicklistener
                        if (currentAnswer.getState() == Common.ANSWER_STATE.NO_ANSWER) {

                            tv_D.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //Desactivar los 5 onClickListeners
                                    tv_A.setClickable(false);
                                    tv_B.setClickable(false);
                                    tv_C.setClickable(false);
                                    tv_D.setClickable(false);
                                    tv_E.setClickable(false);

                                    // Si es correcta, la marcamos en verde y actualizamos contador
                                    if (o.isCorrect()) {
                                        tv_D.setBackgroundColor(Color.parseColor("#99cc00"));
                                        currentAnswer.setState(Common.ANSWER_STATE.RIGHT_ANSWER);
                                        //Actualizar contador
                                        Common.right_answer_count++;
                                        questionActivity.tv_right_answer_count.setText(String.valueOf(Common.right_answer_count));

                                    }
                                    // Si no es correcta, buscamos la correcta y la marcamos en VERDE y actualizamos contador
                                    else {
                                        tv_D.setBackgroundColor(Color.parseColor("#ff0000"));
                                        currentAnswer.setState(Common.ANSWER_STATE.WRONG_ANSWER);
                                        //Actualizar contador
                                        Common.wrong_answer_count++;
                                        questionActivity.tv_wrong_answer_count.setText(String.valueOf(Common.wrong_answer_count));

                                        //Buscamos la correcta para marcarla en verde
                                        if (currentOptionList.containsKey("A") && currentOptionList.get("A").isCorrect())
                                            tv_A.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("B") && currentOptionList.get("B").isCorrect())
                                            tv_B.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("C") && currentOptionList.get("C").isCorrect())
                                            tv_C.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("E") && currentOptionList.get("E").isCorrect())
                                            tv_E.setBackgroundColor(Color.parseColor("#99cc00"));
                                    }

                                    Common.filteredAnswerList.put(currentAnswer.getQuestionId(), currentAnswer);
                                    Common.currentAnswerSheet.put(currentAnswer.getQuestionId(), o.getId());

                                }
                            });

                        } else {
                            //La pregunta ya ha sido contestada
                            String currentSelectedOption = Common.currentAnswerSheet.get(currentAnswer.getQuestionId());
                            //La correcta la marcamos siempre en verde
                            if (o.isCorrect())
                                tv_D.setBackgroundColor(Color.parseColor("#99cc00"));
                            else if (currentSelectedOption.equals(o.getId())) {
                                //Si la opción no es correcta pero es la elegida la marcamos en rojo
                                tv_D.setBackgroundColor(Color.parseColor("#ff0000"));
                            }
                        }
                    }


                } else {
                    tv_D.setVisibility(View.GONE);
                }
            }
        });


        //TextView tv_E
        pageViewModel.getOptionE().observe(this, new Observer<Option>() {
            @Override
            public void onChanged(@Nullable final Option o) {
                if (o != null) {
                    tv_E.setText(o.getId() + ". " + o.getText());
                    tv_E.setVisibility(View.VISIBLE);

                    if (Common.viewMode == Common.VIEW_MODE.VIEW) {
                        if (o.isCorrect())
                            tv_E.setBackgroundColor(Color.parseColor("#99cc00"));
                        else //Por si acaso volvemos a poner en modo no_answer
                            tv_E.setBackgroundColor(Color.parseColor("#00ddff"));
                    } else {
                        //Estamos en modo TEST o QUICKTEST

                        //Si la pregunta no ha sido contestada añadimos un clicklistener
                        if (currentAnswer.getState() == Common.ANSWER_STATE.NO_ANSWER) {

                            tv_E.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //Desactivar los 5 onClickListeners
                                    tv_A.setClickable(false);
                                    tv_B.setClickable(false);
                                    tv_C.setClickable(false);
                                    tv_D.setClickable(false);
                                    tv_E.setClickable(false);

                                    // Si es correcta, la marcamos en verde y actualizamos contador
                                    if (o.isCorrect()) {
                                        tv_E.setBackgroundColor(Color.parseColor("#99cc00"));
                                        currentAnswer.setState(Common.ANSWER_STATE.RIGHT_ANSWER);
                                        //Actualizar contador
                                        Common.right_answer_count++;
                                        questionActivity.tv_right_answer_count.setText(String.valueOf(Common.right_answer_count));

                                    }
                                    // Si no es correcta, buscamos la correcta y la marcamos en rojo y actualizamos contaado
                                    else {
                                        tv_E.setBackgroundColor(Color.parseColor("#ff0000"));
                                        currentAnswer.setState(Common.ANSWER_STATE.WRONG_ANSWER);
                                        //Actualizar contador
                                        Common.wrong_answer_count++;
                                        questionActivity.tv_wrong_answer_count.setText(String.valueOf(Common.wrong_answer_count));

                                        //Buscamos la correcta para marcarla en verde
                                        if (currentOptionList.containsKey("A") && currentOptionList.get("A").isCorrect())
                                            tv_A.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("B") && currentOptionList.get("B").isCorrect())
                                            tv_B.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("C") && currentOptionList.get("C").isCorrect())
                                            tv_C.setBackgroundColor(Color.parseColor("#99cc00"));
                                        if (currentOptionList.containsKey("D") && currentOptionList.get("D").isCorrect())
                                            tv_D.setBackgroundColor(Color.parseColor("#99cc00"));
                                    }

                                    Common.filteredAnswerList.put(currentAnswer.getQuestionId(), currentAnswer);
                                    Common.currentAnswerSheet.put(currentAnswer.getQuestionId(), o.getId());

                                }
                            });

                        } else {
                            //La pregunta ya ha sido contestada
                            String currentSelectedOption = Common.currentAnswerSheet.get(currentAnswer.getQuestionId());
                            //La correcta la marcamos siempre en verde
                            if (o.isCorrect())
                                tv_E.setBackgroundColor(Color.parseColor("#99cc00"));
                            else if (currentSelectedOption.equals(o.getId())) {
                                //Si la opción no es correcta pero es la elegida la marcamos en rojo
                                tv_E.setBackgroundColor(Color.parseColor("#ff0000"));
                            }
                        }
                    }


                } else {
                    tv_E.setVisibility(View.GONE);
                }
            }
        });


        return rootView;
    }
}