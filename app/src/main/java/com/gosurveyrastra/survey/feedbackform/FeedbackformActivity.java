package com.gosurveyrastra.survey.feedbackform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gosurveyrastra.survey.MainActivity;
import com.gosurveyrastra.survey.QuestionEntity;
import com.gosurveyrastra.survey.QuestionWithChoicesEntity;
import com.gosurveyrastra.survey.R;
import com.gosurveyrastra.survey.fragment.CheckBoxesFragment;
import com.gosurveyrastra.survey.fragment.DropDownFragment;
import com.gosurveyrastra.survey.fragment.EditTextFragment;
import com.gosurveyrastra.survey.fragment.RadioBoxesFragment;
import com.gosurveyrastra.survey.fragment.ViewPagerAdapter;
import com.google.gson.Gson;
import com.gosurveyrastra.survey.service.Surverys;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.gosurveyrastra.survey.MainActivity.DATABASE_NAME;

public class FeedbackformActivity extends AppCompatActivity {

    final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    List<QuestionsItem> questionsItems = new ArrayList<>();
    private AppDatabase appDatabase;
    private TextView questionPositionTV;
    private String totalQuestions = "1";
    private Gson gson;
    private ViewPager questionsViewPager;
    SQLiteDatabase mDatabase;
    String name;
    public static String formanews;
    public static String formid;
    List<Surverys> employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbackform);
        formanews=getIntent().getStringExtra("formanews");
        formid=getIntent().getStringExtra("formid");
        SharedPreferences prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        name = prefs.getString(""+formanews, "No name defined");
        employeeList = new ArrayList<>();
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        createEmployeeTable();
        toolBarInit();
        appDatabase = AppDatabase.getAppDatabase(FeedbackformActivity.this);
        gson = new Gson();
        parsingData(name);
        ImageView navagitionvie=findViewById(R.id.navagitionvie);
        navagitionvie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FeedbackformActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void createEmployeeTable() {
        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS surveryes (\n" +
                        "    id INTEGER NOT NULL CONSTRAINT employees_pk PRIMARY KEY AUTOINCREMENT,\n" +
                        "    questionid varchar(200) NOT NULL,\n" +
                        "    question_type_name varchar(200) NOT NULL,\n" +
                        "    question_name varchar(200) NOT NULL,\n" +
                        "    answer varchar(200) NOT NULL,\n" +
                        "    surveyids varchar(200) NOT NULL,\n" +
                        "    salary varchar(200) NOT NULL\n," +
                        "    formname varchar(200) NOT NULL\n," +
                        "    instime varchar(200) NOT NULL\n" +
                        ");"
        );
     }

    private void toolBarInit()
    {
        questionPositionTV = findViewById(R.id.questionPositionTV);
    }

    private void parsingData(String bundle)
    {
        QuestionDataModel questionDataModel = new QuestionDataModel();
        questionDataModel = gson.fromJson(name, QuestionDataModel.class);
        questionsItems = questionDataModel.getQuestions();
        totalQuestions = String.valueOf(questionsItems.size());
        String questionPosition = "1/" + totalQuestions;
        setTextWithSpan(questionPosition);
        preparingQuestionInsertionInDb(questionsItems);
        preparingInsertionInDb(questionsItems);
        for (int i = 0; i < questionsItems.size(); i++)
        {
            QuestionsItem question = questionsItems.get(i);
            if (question.getQuestionTypeName().equalsIgnoreCase("CheckBox"))
            {
                CheckBoxesFragment checkBoxesFragment = new CheckBoxesFragment();
                Bundle checkBoxBundle = new Bundle();
                checkBoxBundle.putParcelable("question", question);
                checkBoxBundle.putInt("page_position", i);
                checkBoxesFragment.setArguments(checkBoxBundle);
                fragmentArrayList.add(checkBoxesFragment);
            }
            if (question.getQuestionTypeName().equalsIgnoreCase("radio"))
            {
                RadioBoxesFragment radioBoxesFragment = new RadioBoxesFragment();
                Bundle radioButtonBundle = new Bundle();
                radioButtonBundle.putParcelable("question", question);
                radioButtonBundle.putInt("page_position", i);
                radioBoxesFragment.setArguments(radioButtonBundle);
                fragmentArrayList.add(radioBoxesFragment);
            }
            if (question.getQuestionTypeName().equalsIgnoreCase("dropdown"))
            {
                DropDownFragment radioBoxesFragment = new DropDownFragment();
                Bundle radioButtonBundle = new Bundle();
                radioButtonBundle.putParcelable("question", question);
                radioButtonBundle.putInt("page_position", i);
                radioBoxesFragment.setArguments(radioButtonBundle);
                fragmentArrayList.add(radioBoxesFragment);
            }
            if (question.getQuestionTypeName().equalsIgnoreCase("text"))
            {
                EditTextFragment radioBoxesFragment = new EditTextFragment();
                Bundle radioButtonBundle = new Bundle();
                radioButtonBundle.putParcelable("question", question);
                radioButtonBundle.putInt("page_position", i);
                radioBoxesFragment.setArguments(radioButtonBundle);
                fragmentArrayList.add(radioBoxesFragment);
            }

        }
        questionsViewPager = findViewById(R.id.pager);
        questionsViewPager.setOffscreenPageLimit(3);
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentArrayList);
        questionsViewPager.setAdapter(mPagerAdapter);
    }

    public void nextQuestion()
    {
        int item = questionsViewPager.getCurrentItem() + 1;
        questionsViewPager.setCurrentItem(item);
        String currentQuestionPosition = String.valueOf(item + 1);
        String questionPosition = currentQuestionPosition + "/" + totalQuestions;
        setTextWithSpan(questionPosition);
    }

    public int getTotalQuestionsSize()
    {
        return questionsItems.size();
    }

    private void preparingQuestionInsertionInDb(List<QuestionsItem> questionsItems)
    {
        List<QuestionEntity> questionEntities = new ArrayList<>();
        for (int i = 0; i < questionsItems.size(); i++)
        {
            QuestionEntity questionEntity = new QuestionEntity();
            questionEntity.setQuestionId(questionsItems.get(i).getId());
            questionEntity.setQuestion(questionsItems.get(i).getQuestionName());
            questionEntities.add(questionEntity);
        }
        insertQuestionInDatabase(questionEntities);
    }

    private void insertQuestionInDatabase(List<QuestionEntity> questionEntities)
    {
        Observable.just(questionEntities)
                .map(this::insertingQuestionInDb)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    /*First, clear the table, if any previous data saved in it. Otherwise, we get repeated data.*/
    private String insertingQuestionInDb(List<QuestionEntity> questionEntities)
    {
        appDatabase.getQuestionDao().deleteAllQuestions();
        appDatabase.getQuestionDao().insertAllQuestions(questionEntities);
        return "";
    }

    private void preparingInsertionInDb(List<QuestionsItem> questionsItems)
    {
        ArrayList<QuestionWithChoicesEntity> questionWithChoicesEntities = new ArrayList<>();
        for (int i = 0; i < questionsItems.size(); i++)
        {
            List<AnswerOptions> answerOptions = questionsItems.get(i).getAnswerOptions();
            for (int j = 0; j < answerOptions.size(); j++)
            {
                QuestionWithChoicesEntity questionWithChoicesEntity = new QuestionWithChoicesEntity();
                questionWithChoicesEntity.setQuestionId(String.valueOf(questionsItems.get(i).getId()));
                questionWithChoicesEntity.setAnswerChoice(answerOptions.get(j).getName());
                questionWithChoicesEntity.setAnswerChoicePosition(String.valueOf(j));
                questionWithChoicesEntity.setAnswerChoiceId(answerOptions.get(j).getAnswerId());
                questionWithChoicesEntity.setAnswerChoiceState("0");
                questionWithChoicesEntities.add(questionWithChoicesEntity);
            }
        }
        insertQuestionWithChoicesInDatabase(questionWithChoicesEntities);
    }

    private void insertQuestionWithChoicesInDatabase(List<QuestionWithChoicesEntity> questionWithChoicesEntities)
    {
        Observable.just(questionWithChoicesEntities)
                .map(this::insertingQuestionWithChoicesInDb)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private String insertingQuestionWithChoicesInDb(List<QuestionWithChoicesEntity> questionWithChoicesEntities)
    {
        appDatabase.getQuestionChoicesDao().deleteAllChoicesOfQuestion();
        appDatabase.getQuestionChoicesDao().insertAllChoicesOfQuestion(questionWithChoicesEntities);
        return "";
    }

    @Override
    public void onBackPressed()
    {
        if (questionsViewPager.getCurrentItem() == 0)
        {

            startActivity(new Intent(FeedbackformActivity.this, MainActivity.class));
            finish();
        } else
        {
            int item = questionsViewPager.getCurrentItem() - 1;
            questionsViewPager.setCurrentItem(item);
            String currentQuestionPosition = String.valueOf(item + 1);
            String questionPosition = currentQuestionPosition + "/" + totalQuestions;
            setTextWithSpan(questionPosition);
        }
    }

    private void setTextWithSpan(String questionPosition)
    {
        int slashPosition = questionPosition.indexOf("/");
        Spannable spanText = new SpannableString(questionPosition);
        spanText.setSpan(new RelativeSizeSpan(0.7f), slashPosition, questionPosition.length(), 0);
        questionPositionTV.setText(spanText);
    }
}