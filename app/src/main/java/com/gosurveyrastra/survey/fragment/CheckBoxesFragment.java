package com.gosurveyrastra.survey.fragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;
import static com.gosurveyrastra.survey.MainActivity.DATABASE_NAME;
import static com.gosurveyrastra.survey.feedbackform.FeedbackformActivity.formid;
import static com.gosurveyrastra.survey.service.DataService.finalpathstore;

public class CheckBoxesFragment extends Fragment
{
    private final ArrayList<CheckBox> checkBoxArrayList = new ArrayList<>();
    private int atLeastOneChecked = 0;
    private FragmentActivity mContext;
    private Button nextOrFinishButton;
    private TextView questionCBTypeTextView;
    private LinearLayout checkboxesLinearLayout;
    private AppDatabase appDatabase;
    private String questionId = "";
    private int currentPagePosition = 0;
    private int clickedCheckBoxPosition = 0;
    private String qState = "0";
    SQLiteDatabase mDatabase;

    public CheckBoxesFragment()
    {
        // Required empty public constructor
        // pdf converton based on screenshots and based on codecanyon with fb,
        // equalizer
        // wallpaper based on new,
        // screen recoder,
        // vpn with all links browser
        // flutter webview,
        // cam scanner,
        // speedtest with internet spped
        // unread - whatsapp in battailabs
        // xvideo player with music and video player etc
    }

    String selecteditems="";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_check_boxes, container, false);
        appDatabase = AppDatabase.getAppDatabase(getActivity());
        nextOrFinishButton = rootView.findViewById(R.id.nextOrFinishButton);
        questionCBTypeTextView = rootView.findViewById(R.id.questionCBTypeTextView);
        checkboxesLinearLayout = rootView.findViewById(R.id.checkboxesLinearLayout);
        mDatabase = getActivity().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        nextOrFinishButton.setOnClickListener(v -> {
            if (atLeastOneChecked != 0)
            {
                for (int i = 0; i < checkBoxArrayList.size(); i++)
                {
                    if (i == clickedCheckBoxPosition)
                    {
                        CheckBox checkBox = checkBoxArrayList.get(i);
                        if (checkBox.isChecked())
                        {
                            selecteditems=selecteditems+checkBox.getText().toString()+", ";
                            atLeastOneChecked = atLeastOneChecked + 1;
                            String cbPosition = String.valueOf(checkBoxArrayList.indexOf(checkBox));
                            String[] data = new String[]{"1", questionId, cbPosition};
                            insertAnswerInDatabase(data);
                        } else
                        {
                            atLeastOneChecked = atLeastOneChecked - 1;
                            if (atLeastOneChecked <= 0)
                                atLeastOneChecked = 0;
                            String cbPosition = String.valueOf(checkBoxArrayList.indexOf(checkBox));
                            String[] data = new String[]{"0", questionId, cbPosition};
                            insertAnswerInDatabase(data);
                        }
                    }
                }
                String sql = "UPDATE surveryes \n" +
                        "SET answer = ?\n" +
                        "WHERE question_name = ?;\n";
                mDatabase.execSQL(sql, new String[]{selecteditems, questionCBTypeTextView.getText().toString()});
                if (currentPagePosition == ((FeedbackformActivity) mContext).getTotalQuestionsSize())
                {
                    Intent i=new Intent(getContext(), ThankYouScreen.class);
                    startActivity(i);
                    getActivity().finish();
                } else
                {
                    ((FeedbackformActivity) mContext).nextQuestion();
                }
            } else
            {
                Toast.makeText(mContext, "please select one value", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        atLeastOneChecked = 0;
        if (isVisibleToUser)
        {
            for (int i = 0; i < checkBoxArrayList.size(); i++)
            {
                CheckBox checkBox = checkBoxArrayList.get(i);
                String cbPosition = String.valueOf(i);
                String[] data = new String[]{questionId, cbPosition};
                Observable.just(data)
                        .map(this::getTheStateOfCheckBox)
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
                                    checkBox.setChecked(true);
                                    atLeastOneChecked = atLeastOneChecked + 1;
                                    if (!nextOrFinishButton.isEnabled())
                                    {
                                        nextOrFinishButton.setEnabled(true);
                                    }
                                } else
                                {
                                    checkBox.setChecked(false);
                                }
                            }
                        });
            }
        }
    }

    private String getTheStateOfCheckBox(String[] data)
    {
        return appDatabase.getQuestionChoicesDao().isChecked(data[0], data[1]);
    }

    private void saveActionsOfCheckBox()
    {
        for (int i = 0; i < checkBoxArrayList.size(); i++)
        {
            if (i == clickedCheckBoxPosition)
            {
                CheckBox checkBox = checkBoxArrayList.get(i);
                if (checkBox.isChecked())
                {
                    atLeastOneChecked = atLeastOneChecked + 1;
                    String cbPosition = String.valueOf(checkBoxArrayList.indexOf(checkBox));
                    String[] data = new String[]{"1", questionId, cbPosition};
                    insertAnswerInDatabase(data);
                } else
                {
                    atLeastOneChecked = atLeastOneChecked - 1;
                    if (atLeastOneChecked <= 0)
                        atLeastOneChecked = 0;
                    String cbPosition = String.valueOf(checkBoxArrayList.indexOf(checkBox));
                    String[] data = new String[]{"0", questionId, cbPosition};
                    insertAnswerInDatabase(data);
                }
            }
        }
    }

    private void insertAnswerInDatabase(String[] data)
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
        QuestionsItem checkBoxTypeQuestion = null;
        if (getArguments() != null)
        {
            checkBoxTypeQuestion = getArguments().getParcelable("question");
            questionId = String.valueOf(checkBoxTypeQuestion != null ? checkBoxTypeQuestion.getId() : 0);
            currentPagePosition = getArguments().getInt("page_position") + 1;
        }
        questionCBTypeTextView.setText(checkBoxTypeQuestion != null ? checkBoxTypeQuestion.getQuestionName() : "");
        String insertSQL = "INSERT INTO surveryes \n" +
                "(questionid, question_type_name, question_name, answer,surveyids,salary)\n" +
                "VALUES \n" +
                "(?, ?, ?, ?, ?, ?);";
        mDatabase.execSQL(insertSQL, new String[]{""+checkBoxTypeQuestion.getQuestionTypeId(), checkBoxTypeQuestion.getQuestionTypeName(), checkBoxTypeQuestion.getQuestionName(),
                "", finalpathstore, formid});
        List<AnswerOptions> checkBoxChoices = Objects.requireNonNull(checkBoxTypeQuestion).getAnswerOptions();
        checkBoxArrayList.clear();
        for (AnswerOptions choice : checkBoxChoices)
        {
            CheckBox checkBox = new CheckBox(mContext);
            checkBox.setText(choice.getName());
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            checkBox.setTextColor(ContextCompat.getColor(mContext, R.color.textchoice));
            checkBox.setPadding(10, 40, 10, 40);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 25;
            View view = new View(mContext);
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.divider));
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            checkboxesLinearLayout.addView(checkBox, params);
            checkboxesLinearLayout.addView(view);
            checkBoxArrayList.add(checkBox);
            checkBox.setOnClickListener(view1 -> {
                CheckBox buttonView = (CheckBox) view1;
                clickedCheckBoxPosition = checkBoxArrayList.indexOf(buttonView);
                saveActionsOfCheckBox();
            });
        }
        if (currentPagePosition == ((FeedbackformActivity) mContext).getTotalQuestionsSize())
        {
            nextOrFinishButton.setText(R.string.finish);
        } else
        {
            nextOrFinishButton.setText(R.string.next);
        }
    }
}