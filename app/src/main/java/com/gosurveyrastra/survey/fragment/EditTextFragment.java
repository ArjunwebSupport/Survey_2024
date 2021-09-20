package com.gosurveyrastra.survey.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.gosurveyrastra.survey.R;
import com.gosurveyrastra.survey.feedbackform.AnswerOptions;
import com.gosurveyrastra.survey.feedbackform.AppDatabase;
import com.gosurveyrastra.survey.feedbackform.FeedbackformActivity;
import com.gosurveyrastra.survey.feedbackform.QuestionsItem;
import com.gosurveyrastra.survey.feedbackform.ThankYouScreen;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;
import static com.gosurveyrastra.survey.MainActivity.DATABASE_NAME;
import static com.gosurveyrastra.survey.feedbackform.FeedbackformActivity.formid;
import static com.gosurveyrastra.survey.service.DataService.finalpathstore;

public class EditTextFragment extends Fragment
{
    private final ArrayList<EditText> radioButtonArrayList = new ArrayList<>();
    private boolean screenVisible = false;
    private QuestionsItem radioButtonTypeQuestion;
    private FragmentActivity mContext;
    private Button nextOrFinishButton;
    private TextView questionRBTypeTextView;
    private EditText radioGroupForChoices;
    private boolean atLeastOneChecked = false;
    private AppDatabase appDatabase;
    private String questionId = "";
    private int currentPagePosition = 0;
    private int clickedRadioButtonPosition = 0;
    private String qState = "0";
    SQLiteDatabase mDatabase;

    public EditTextFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_radio_boxes1, container, false);
        appDatabase = AppDatabase.getAppDatabase(getActivity());
        nextOrFinishButton = rootView.findViewById(R.id.nextOrFinishButton);
        questionRBTypeTextView = rootView.findViewById(R.id.questionRBTypeTextView);
        radioGroupForChoices = rootView.findViewById(R.id.radioGroupForChoices);
        mDatabase = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        nextOrFinishButton.setOnClickListener(v -> {
            if (currentPagePosition == ((FeedbackformActivity) mContext).getTotalQuestionsSize())
            {
                if(TextUtils.isEmpty(radioGroupForChoices.getText().toString())){
                    radioGroupForChoices.setError("Required");
                }else {
                    saveActionsOfRadioBox();
                    try {
                        InputMethodManager inputManager = (InputMethodManager) rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputManager != null) {
                            inputManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                        }
                    }catch (Exception e){
                    }
                    Intent i=new Intent(getContext(), ThankYouScreen.class);
                    startActivity(i);
                    getActivity().finish();
                }
            } else
            {
                if(TextUtils.isEmpty(radioGroupForChoices.getText().toString())){
                    radioGroupForChoices.setError("Required");
                }else {
                    saveActionsOfRadioBox();
                    try {
                        InputMethodManager inputManager = (InputMethodManager) rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputManager != null) {
                            inputManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                        }
                    }catch (Exception e){
                    }

                    ((FeedbackformActivity) mContext).nextQuestion();
                }
            }
        });
        //previousButton.setOnClickListener(view -> mContext.onBackPressed());

        return rootView;
    }

    /*This method get called only when the fragment get visible, and here states of Radio Button(s) retained*/
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
        {
            screenVisible = true;
            for (int i = 0; i < radioButtonArrayList.size(); i++)
            {
                EditText radioButton = radioButtonArrayList.get(i);
                String cbPosition = String.valueOf(i);

                String[] data = new String[]{questionId, cbPosition};
                Observable.just(data)
                        .map(this::getTheStateOfRadioBox)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>()
                        {
                            @Override
                            public void onSubscribe(Disposable d)
                            {

                            }

                            @Override
                            public void onNext(String s)
                            {
                                qState = s;
                            }

                            @Override
                            public void onError(Throwable e)
                            {

                            }

                            @Override
                            public void onComplete()
                            {
                                if (qState.equals("1"))
                                {
                                } else
                                {
                                }
                            }
                        });
            }
        }
    }

    private String getTheStateOfRadioBox(String[] data)
    {
        return appDatabase.getQuestionChoicesDao().isChecked(data[0], data[1]);
    }

    private void saveActionsOfRadioBox()
    {
        String sql = "UPDATE surveryes \n" +
                "SET answer = ?\n" +
                "WHERE question_name = ?;\n";
        mDatabase.execSQL(sql, new String[]{radioGroupForChoices.getText().toString(), questionRBTypeTextView.getText().toString()});
        String[] data = new String[]{"1", questionId, radioGroupForChoices.getText().toString()};
        Log.e("strrrrrrrrrr","data  "+data[0]);
        Log.e("strrrrrrrrrr","data  "+data[1]);
        Log.e("strrrrrrrrrr","data  "+data[2]);
        insertChoiceInDatabase(data);
//
//        for (int i = 0; i < radioButtonArrayList.size(); i++)
//        {
//            if (i == clickedRadioButtonPosition)
//            {
//                EditText radioButton = radioButtonArrayList.get(i);
//                    atLeastOneChecked = true;
//                    String cbPosition = String.valueOf(radioButton.getText().toString());
//                    insertChoiceInDatabase(data);
//            }
//        }
//
//        if (atLeastOneChecked)
//        {
//            nextOrFinishButton.setEnabled(true);
//        } else
//        {
//            nextOrFinishButton.setEnabled(false);
//        }
    }

    private void insertChoiceInDatabase(String[] data)
    {
        Observable.just(data)
                .map(this::insertingInDb)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private String insertingInDb(String[] data)
    {
        appDatabase.getQuestionChoicesDao().updateQuestionWithChoice(data[0], data[1], data[2]);
        return "";
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        if (getArguments() != null)
        {
            radioButtonTypeQuestion = getArguments().getParcelable("question");
            questionId = String.valueOf(radioButtonTypeQuestion != null ? radioButtonTypeQuestion.getId() : 0);
            currentPagePosition = getArguments().getInt("page_position") + 1;
        }
        questionRBTypeTextView.setText(radioButtonTypeQuestion.getQuestionName());
        List<AnswerOptions> choices = radioButtonTypeQuestion.getAnswerOptions();
        radioButtonArrayList.clear();
        String insertSQL = "INSERT INTO surveryes \n" +
                "(questionid, question_type_name, question_name, answer,surveyids,salary)\n" +
                "VALUES \n" +
                "(?, ?, ?, ?, ?, ?);";
        //using the same method execsql for inserting values
        //this time it has two parameters
        //first is the sql string and second is the parameters that is to be binded with the query
        mDatabase.execSQL(insertSQL, new String[]{""+radioButtonTypeQuestion.getQuestionTypeId(), radioButtonTypeQuestion.getQuestionTypeName(), radioButtonTypeQuestion.getQuestionName(),
                "", finalpathstore,formid});
//        for (AnswerOptions choice : choices)
//        {
//            RadioButton rb = new RadioButton(mContext);
//            rb.setText(choice.getName());
//            rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//            rb.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
//            rb.setPadding(10, 40, 10, 40);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.leftMargin = 25;
//            rb.setLayoutParams(params);
//            View view = new View(mContext);
//            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.divider));
//            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
//            radioGroupForChoices.addView(rb);
//            radioGroupForChoices.addView(view);
//            radioButtonArrayList.add(rb);
//
//            rb.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                if (screenVisible)
//                {
//                    clickedRadioButtonPosition = radioButtonArrayList.indexOf(buttonView);
//                    saveActionsOfRadioBox();
//                }
//            });
//        }
        nextOrFinishButton.setEnabled(true);
        /* If the current question is last in the questionnaire then
        the "Next" button will change into "Finish" button*/
        if (currentPagePosition == ((FeedbackformActivity) mContext).getTotalQuestionsSize())
        {
            nextOrFinishButton.setText(R.string.finish);
        } else
        {
            nextOrFinishButton.setText(R.string.next);
        }
    }
}