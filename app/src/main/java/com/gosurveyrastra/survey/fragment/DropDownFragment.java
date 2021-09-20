package com.gosurveyrastra.survey.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class DropDownFragment extends Fragment
{

    private final ArrayList<Spinner> radioButtonArrayList = new ArrayList<>();
    private boolean screenVisible = false;
    private QuestionsItem radioButtonTypeQuestion;
    private FragmentActivity mContext;
    private Button nextOrFinishButton;
    private TextView questionRBTypeTextView;
    private Spinner radioGroupForChoices;
    private boolean atLeastOneChecked = false;
    private AppDatabase appDatabase;
    private String questionId = "";
    private int currentPagePosition = 0;
    private String qState = "0";
    String item="choose";
    SQLiteDatabase mDatabase;

    public DropDownFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_dropdown, container, false);
        appDatabase = AppDatabase.getAppDatabase(getActivity());
        nextOrFinishButton = rootView.findViewById(R.id.nextOrFinishButton);
        questionRBTypeTextView = rootView.findViewById(R.id.questionRBTypeTextView);
        radioGroupForChoices = rootView.findViewById(R.id.radioGroupForChoices);
        mDatabase = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        nextOrFinishButton.setOnClickListener(v -> {
            if(item.equalsIgnoreCase("choose")){
                ((TextView)radioGroupForChoices.getChildAt(0)).setError("Please select a value");
            }else {
                if (currentPagePosition == ((FeedbackformActivity) mContext).getTotalQuestionsSize()) {
                    Intent i=new Intent(getContext(), ThankYouScreen.class);
                    startActivity(i);
                    getActivity().finish();
                } else {
                    ((FeedbackformActivity) mContext).nextQuestion();
                }
            }
        });
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            screenVisible = true;
            for (int i = 0; i < radioButtonArrayList.size(); i++)
            {
                Spinner radioButton = radioButtonArrayList.get(i);
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
        atLeastOneChecked = true;
        String cbPosition = String.valueOf(item);
        String[] data = new String[]{"1", questionId, cbPosition};
        insertChoiceInDatabase(data);
        String sql = "UPDATE surveryes \n" +
                "SET answer = ?\n" +
                "WHERE question_name = ?;\n";
        mDatabase.execSQL(sql, new String[]{item, questionRBTypeTextView.getText().toString()});

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        if (getArguments() != null) {
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
                "", finalpathstore, formid});
        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Choose");
        for (AnswerOptions choice : choices) {
            spinnerArray.add(choice.getName());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        radioGroupForChoices.setAdapter(spinnerArrayAdapter);
        radioGroupForChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (screenVisible)
                {
                    item = adapterView.getItemAtPosition(i).toString();
                    saveActionsOfRadioBox();
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                ((TextView)radioGroupForChoices.getChildAt(0)).setError("Please select a value");
            }
        });
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