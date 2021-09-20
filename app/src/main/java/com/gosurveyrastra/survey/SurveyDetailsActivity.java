package com.gosurveyrastra.survey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.gosurveyrastra.survey.feedbackform.AppDatabase;
import com.gosurveyrastra.survey.feedbackform.FeedbackformActivity;
import com.gosurveyrastra.survey.feedbackform.QuestionsItem;
import com.gosurveyrastra.survey.feedbackform.ThankYouScreen;
import com.gosurveyrastra.survey.service.Surverys;
import com.gosurveyrastra.survey.ui.SurveyListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.gosurveyrastra.survey.MainActivity.DATABASE_NAME;
import static com.gosurveyrastra.survey.ui.HomeFragment.centerLatitude;
import static com.gosurveyrastra.survey.ui.HomeFragment.centerLongitude;
import static com.gosurveyrastra.survey.ui.HomeFragment.lat;
import static com.gosurveyrastra.survey.ui.HomeFragment.lat1;
import static com.gosurveyrastra.survey.ui.HomeFragment.lat2;
import static com.gosurveyrastra.survey.ui.HomeFragment.lon1;
import static com.gosurveyrastra.survey.ui.HomeFragment.lon2;
import static com.gosurveyrastra.survey.ui.HomeFragment.resultlat;
import static com.gosurveyrastra.survey.ui.HomeFragment.resultlong;
import static com.gosurveyrastra.survey.ui.HomeFragment.testLatitude;
import static com.gosurveyrastra.survey.ui.HomeFragment.testLongitude;

public class SurveyDetailsActivity extends AppCompatActivity {

    SQLiteDatabase mDatabase;
    String name;
    public static String formanews;
    public static String formid;
    List<View> allViewInstance = new ArrayList<View>();
    JSONObject jsonObject = new JSONObject();
    private JSONObject optionsObj;
    boolean validation=false;
    ArrayList<String> dataModelArrayList;
    ArrayList<String> dataModelArrayList1;
    SurveyListModel1 playerModel;
    int ia1=0;
    int ia2=0;
    SparseBooleanArray sparseBooleanArray ;
    String radioselected;
    String textiewsselected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_details);
        formanews=getIntent().getStringExtra("formanews");
        formid=getIntent().getStringExtra("formid");
        TextView surveyname=findViewById(R.id.surveyname);
        surveyname.setText(""+formanews);
        SharedPreferences prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
        name = prefs.getString(""+formanews, "No name defined");
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        createEmployeeTable();
        ImageView navagitionvie=findViewById(R.id.navagitionvie);
        navagitionvie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SurveyDetailsActivity.this, MainActivity.class));
                finish();
            }
        });
        dataModelArrayList = new ArrayList<>();
        dataModelArrayList1 = new ArrayList<>();
        dataModelArrayList.clear();
        playerModel = new SurveyListModel1();

        TextView surveynamelocation=findViewById(R.id.surveynamelocation);
        surveynamelocation.setText("Current Location: "+lat1);

        LinearLayout viewProductLayout = (LinearLayout) findViewById(R.id.customOptionLL);
        try {
            jsonObject = new JSONObject(name);
            JSONArray customOptnList = jsonObject.getJSONArray("lstRegisterFields");
            ia2=customOptnList.length();
            Log.e("strrrrrrr","init "+customOptnList.length());
            Log.e("strrrrrrr","init "+ia2);
            for (int noOfCustomOpt = 0; noOfCustomOpt < customOptnList.length(); noOfCustomOpt++) {
                JSONObject eachData = customOptnList.getJSONObject(noOfCustomOpt);
                TextView customOptionsName = new TextView(SurveyDetailsActivity.this);
                customOptionsName.setTextSize(18);
                customOptionsName.setPadding(0, 30, 0, 10);
                customOptionsName.setText(eachData.getString("Name"));
                if(noOfCustomOpt%2==0) {
                    customOptionsName.setTextColor(Color.parseColor("#475175"));
                }else {
                    customOptionsName.setTextColor(Color.parseColor("#000000"));
                }
                Typeface face = Typeface.createFromAsset(getAssets(),
                        "fonts/montserrat_bold.otf");
                customOptionsName.setTypeface(face);
                viewProductLayout.addView(customOptionsName);
                if (eachData.getString("QuestionType").equalsIgnoreCase("dropdown")) {
                    final JSONArray dropDownJSONOpt = eachData.getJSONArray("lstRegisterOptions");
                    ArrayList<String> SpinnerOptions = new ArrayList<String>();
                    SpinnerOptions.add("");
                    for (int j = 0; j < dropDownJSONOpt.length(); j++) {
                        String optionString = dropDownJSONOpt.getJSONObject(j).getString("QOption");
                        SpinnerOptions.add(optionString);
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = null;
                    spinnerArrayAdapter = new ArrayAdapter<String>(SurveyDetailsActivity.this, R.layout.spiner_row, SpinnerOptions);
                    Spinner spinner = new Spinner(SurveyDetailsActivity.this);
                    allViewInstance.add(spinner);
                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            try {
                                String variant_name = dropDownJSONOpt.getJSONObject(position).getString("QOption");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }
                    });
                    viewProductLayout.addView(spinner);
                    dataModelArrayList.add("false");
                }

                if (eachData.getString("QuestionType").equalsIgnoreCase("radio")) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    final JSONArray radioButtonJSONOpt = eachData.getJSONArray("lstRegisterOptions");
                    RadioGroup rg = new RadioGroup(SurveyDetailsActivity.this); //create the RadioGroup
                    allViewInstance.add(rg);
                    for (int j = 0; j < radioButtonJSONOpt.length(); j++) {
                        RadioButton rb = new RadioButton(SurveyDetailsActivity.this);
                        rg.addView(rb, params);
                        rb.setLayoutParams(params);
                        rb.setTag(radioButtonJSONOpt.getJSONObject(j).getString("QOption"));
                        rb.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        String optionString = radioButtonJSONOpt.getJSONObject(j).getString("QOption");
                        rb.setText(optionString);
                        rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        rb.setTextColor(ContextCompat.getColor(SurveyDetailsActivity.this, R.color.textchoice));
                        rb.setPadding(0, 0, 0, 0);
                        rb.setLayoutParams(params);
                        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                ((RadioButton)rg.getChildAt(0)).setError(null);
                                View radioButton = group.findViewById(checkedId);
                                String variant_name = radioButton.getTag().toString();
                                radioselected=variant_name;
                            }
                        });
                    }
                    viewProductLayout.addView(rg, params);
                    dataModelArrayList.add("false");

                }
                if (eachData.getString("QuestionType").equalsIgnoreCase("checkbox")) {
                    JSONArray checkBoxJSONOpt = eachData.getJSONArray("lstRegisterOptions");
                    String[] values;
                    values = new String[checkBoxJSONOpt.length()];
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, checkBoxJSONOpt.length()*130);
                    final ListView listview = new ListView(this);
                    listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    for (int j = 0; j < checkBoxJSONOpt.length(); j++) {
                        values[j] = ""+checkBoxJSONOpt.getJSONObject(j).getString("QOption");
//                        CheckBox chk = new CheckBox(SurveyDetailsActivity.this);
//                        chk.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        allViewInstance.add(chk);
//                        chk.setTag(checkBoxJSONOpt.getJSONObject(j).getString("QOption"));
//                        chk.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//                        chk.setTextColor(ContextCompat.getColor(SurveyDetailsActivity.this, R.color.textchoice));
//                        chk.setPadding(0, 0, 0, 0);
//                        chk.setId(checkBoxJSONOpt.length());
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        params.topMargin = 3;
//                        params.bottomMargin = 3;
//                        String optionString = checkBoxJSONOpt.getJSONObject(j).getString("QOption");
//                        chk.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                            }
//                        });
//                        chk.setText(optionString);
//                        viewProductLayout.addView(chk, params);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (SurveyDetailsActivity.this,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    android.R.id.text1, values);
                    listview.setAdapter(adapter);
                    viewProductLayout.addView(listview, params);
                    int finalNoOfCustomOpt = noOfCustomOpt;
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // TODO Auto-generated method stub
                            sparseBooleanArray = listview.getCheckedItemPositions();
                            String ValueHolder = "" ;
                            int i = 0 ;
                            while (i < sparseBooleanArray.size()) {
                                if (sparseBooleanArray.valueAt(i)) {
                                    ValueHolder += values[ sparseBooleanArray.keyAt(i) ] + ",";
                                }
                                i++ ;
                            }
                            ValueHolder = ValueHolder.replaceAll("(,)*$", "");
                            Log.e("strrrrr","ValueHolder  "+ValueHolder);
                            if(ValueHolder==""){
                                validation=false;
                            }else {
                                try {
                                    if(dataModelArrayList1.size()>0){
                                        for(int i1=0;i1<dataModelArrayList1.size();i1++){
                                            if(dataModelArrayList1.get(i1).contains(eachData.getString("RegisterFieldId"))){
                                                dataModelArrayList1.remove(i1);
                                            }else{
                                            }
                                        }
                                        dataModelArrayList1.add(eachData.getString("RegisterFieldId") + " " + ValueHolder);
                                    }else {
                                        dataModelArrayList1.add(eachData.getString("RegisterFieldId") + " " + ValueHolder);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.e("strrrrr",""+dataModelArrayList1);
                                validation=true;
                                dataModelArrayList.set(finalNoOfCustomOpt, "true");
                            }
                        }
                    });
                    dataModelArrayList.add("false");
                }
                if (eachData.getString("QuestionType").equalsIgnoreCase("text")|| eachData.getString("QuestionType").equalsIgnoreCase("textarea")) {
                    TextInputLayout til = new TextInputLayout(SurveyDetailsActivity.this);
                    EditText et = new EditText(SurveyDetailsActivity.this);
                    et.setMinLines(2);
                    et.setMinHeight(160);
                    et.setGravity(Gravity.START);
                    et.setPadding(20, 10, 0, 0);
                    final int sdk = android.os.Build.VERSION.SDK_INT;
                    if(eachData.getString("ValidationType").equalsIgnoreCase("phone")){
                        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL); //for decimal numbers
                        et.setMinLines(2);
                        et.setMinHeight(160);
                        et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
                        et.setGravity(Gravity.START);
                        et.setPadding(20, 10, 0, 0);
                    }
                    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        et.setBackgroundDrawable(ContextCompat.getDrawable(SurveyDetailsActivity.this, R.drawable.drawable_yellow) );
                    } else {
                        et.setBackground(ContextCompat.getDrawable(SurveyDetailsActivity.this, R.drawable.drawable_yellow));
                    }
                    til.addView(et);
                    allViewInstance.add(et);
                    et.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void afterTextChanged(Editable s) {}

                        @Override
                        public void beforeTextChanged(CharSequence s, int start,
                                                      int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                            if(s.length() != 0){
                                try {
                                    textiewsselected=s.toString()+""+eachData.getString("Name");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
                    viewProductLayout.addView(til);
                }
                View ruler = new View(SurveyDetailsActivity.this);
                ruler.setBackgroundColor(0xFFFFFFFF);
                viewProductLayout.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 20));
                dataModelArrayList.add("false");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getDataFromDynamicViews(View v) {
        ia1=0;
        try {
            JSONArray customOptnList = jsonObject.getJSONArray("lstRegisterFields");
            Log.e("strrrrrrr","sav "+customOptnList.length());
            for (int noOfViews = 0; noOfViews < customOptnList.length(); noOfViews++) {
                JSONObject eachData = customOptnList.getJSONObject(noOfViews);
                if (eachData.getString("QuestionType").equalsIgnoreCase("dropdown")) {
                    Spinner spinner = (Spinner) allViewInstance.get(noOfViews);
                    JSONArray dropDownJSONOpt = eachData.getJSONArray("lstRegisterOptions");
                    String variant_name = dropDownJSONOpt.getJSONObject(spinner.getSelectedItemPosition()).getString("QOption");
                    String selectedItemTitle = spinner.getSelectedItem().toString();
                    if(selectedItemTitle==""){
                        validation=false;
                        TextView errorText = (TextView)spinner.getSelectedView();
                        errorText.setError("");
                        errorText.setText("Please select a value");
                    }else {
                        validation=true;
                        Log.e("strrrrrrrr", "variant_name " + variant_name);
                        Log.e("strrrrrrrr", "selectedItemTitle " + selectedItemTitle);
                        dataModelArrayList.set(noOfViews, "true");
                    }

                }
                if (eachData.getString("QuestionType").equalsIgnoreCase("radio")) {
                    try{
                        RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
                        ((RadioButton)radioGroup.getChildAt(0)).setError(null);
                        if (radioGroup.getCheckedRadioButtonId() == -1)
                        {
                            validation=false;
                            radioGroup.requestFocus();
                            radioGroup.requestFocusFromTouch();
                            ((RadioButton)radioGroup.getChildAt(0)).setError("Please select a value");
                        }else {
                            validation=true;
                            dataModelArrayList.set(noOfViews,"true");
                        }
                    }catch (Exception e){
                        final JSONArray radioButtonJSONOpt = eachData.getJSONArray("lstRegisterOptions");
                        for (int j = 0; j < radioButtonJSONOpt.length(); j++) {
                            if(radioselected.equalsIgnoreCase(radioButtonJSONOpt.getJSONObject(j).getString("QOption"))){
                                validation=true;
                                dataModelArrayList.set(noOfViews,"true");
                            }
                        }

                    }

                }
//                if (eachData.getString("QuestionType").equalsIgnoreCase("checkbox")) {
//                    try {
//                        if(dataModelArrayList1.size()>0){
//                            for(int i1=0;i1<dataModelArrayList1.size();i1++){
//                                if(dataModelArrayList1.get(i1).contains(eachData.getString("RegisterFieldId"))){
//                                }else{
//                                }
//                            }
//                        }else{
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
////                    CheckBox tempChkBox = (CheckBox) allViewInstance.get(noOfViews);
////                    tempChkBox.setError(null);
////                    if (tempChkBox.isChecked()) {
////                        validation=true;
////                        tempChkBox.clearFocus();
////                        dataModelArrayList.set(noOfViews,"true");
////                    }else {
////                        validation=false;
////                        tempChkBox.requestFocus();
////                        tempChkBox.setError("Required");
////                    }
//
//
//                }
                if (eachData.getString("QuestionType").equalsIgnoreCase("text")|| eachData.getString("QuestionType").equalsIgnoreCase("textarea")) {
                    try{
                        TextView textView = (TextView) allViewInstance.get(noOfViews);
                        textView.setError(null);

                        if(TextUtils.isEmpty(textView.getText().toString())) {
                            validation = false;
                            textView.setError("Required");
                            textView.requestFocus();
                        }else if(eachData.getString("ValidationType").equalsIgnoreCase("phone")) {
                            Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");

                            if(textView.getText().toString().trim().length()<10){
                                validation = false;
                                textView.setError("Check number");
                                textView.requestFocus();
                            }else if(textView.getText().toString().trim().length()>10){
                                validation = false;
                                textView.setError("Check number");
                                textView.requestFocus();
                            }else if (regex.matcher(textView.getText().toString()).find()) {
                                validation = false;
                                textView.setError("invalid number format");
                                textView.requestFocus();
                            }
                            else{
                                textView.clearFocus();
                                validation=true;
                                dataModelArrayList.set(noOfViews,"true");
                            }
                        }
                        else {
                            textView.clearFocus();
                            validation=true;
                            dataModelArrayList.set(noOfViews,"true");
                        }
                    }catch (Exception e){
                        try {
                            if(textiewsselected.contains(eachData.getString("Name"))){
                            validation=true;
                            dataModelArrayList.set(noOfViews,"true");}
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i=0;i<dataModelArrayList.size();i++){
            Log.e("strrrrrrrrss","   "+dataModelArrayList.get(i));
            if(dataModelArrayList.get(i).equalsIgnoreCase("true")){
                ia1=ia1+1;
            }else{
            }
        }
        Log.e("strrrr",""+ia1);
        Log.e("strrrr",""+ia2);
        if(ia1==ia2){
            try {
                JSONArray customOptnList = jsonObject.getJSONArray("lstRegisterFields");
                Log.e("strrrrrrr","sav "+customOptnList.length());
                Log.e("strrrrrrr","sav "+validation);
                optionsObj = new JSONObject();
                JSONArray reqs = new JSONArray();
                for (int noOfViews = 0; noOfViews < customOptnList.length(); noOfViews++) {
                    JSONObject eachData = customOptnList.getJSONObject(noOfViews);
                    if (eachData.getString("QuestionType").equalsIgnoreCase("dropdown")) {
                        Spinner spinner = (Spinner) allViewInstance.get(noOfViews);
                        JSONArray dropDownJSONOpt = eachData.getJSONArray("lstRegisterOptions");
                        String variant_name = dropDownJSONOpt.getJSONObject(spinner.getSelectedItemPosition()).getString("QOption");
                        String selectedItemTitle = spinner.getSelectedItem().toString();
                        optionsObj.put(eachData.getString("Name"),
                                "" + variant_name);
                        JSONObject reqObj = new JSONObject();
                        reqObj.put("RegisterFieldId",""+eachData.get("RegisterFieldId"));
                        reqObj.put("Answer",""+selectedItemTitle);
                        reqs.put(reqObj);
                    }
                    if (eachData.getString("QuestionType").equalsIgnoreCase("radio")) {
                        try {
                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                            optionsObj.put(eachData.getString("Name"),
                                    "" + selectedRadioBtn.getTag().toString());
                            JSONObject reqObj = new JSONObject();
                            reqObj.put("RegisterFieldId",""+eachData.get("RegisterFieldId"));
                            reqObj.put("Answer",""+selectedRadioBtn.getTag().toString());
                            reqs.put(reqObj);
                        }catch (Exception e){
                            optionsObj.put(eachData.getString("Name"),
                                    ""+radioselected);
                            JSONObject reqObj = new JSONObject();
                            reqObj.put("RegisterFieldId",""+eachData.get("RegisterFieldId"));
                            reqObj.put("Answer",""+radioselected);
                            reqs.put(reqObj);
                        }

                    }
                    if (eachData.getString("QuestionType").equalsIgnoreCase("checkbox")) {
//                        ListView listview = (ListView) allViewInstance.get(noOfViews);
//                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
//                        {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                // TODO Auto-generated method stub
//                                sparseBooleanArray = listview.getCheckedItemPositions();
//                                String ValueHolder = "" ;
//                                int i = 0 ;
//                                while (i < sparseBooleanArray.size()) {
//                                    if (sparseBooleanArray.valueAt(i)) {
//                                        ValueHolder += values[ sparseBooleanArray.keyAt(i) ] + ",";
//                                    }
//                                    i++ ;
//                                }
//                                ValueHolder = ValueHolder.replaceAll("(,)*$", "");
//                                try {
//                                    optionsObj.put(eachData.getString("Name"), ValueHolder);
//                                    JSONObject reqObj = new JSONObject();
//                                    reqObj.put("RegisterFieldId",""+eachData.get("RegisterFieldId"));
//                                    reqObj.put("Answer",""+ValueHolder);
//                                    reqs.put(reqObj);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });

//                        String ValueHolder = "" ;
//                        int i = 0 ;
//                        while (i < sparseBooleanArray.size()) {
//                            if (sparseBooleanArray.valueAt(i)) {
//                                ValueHolder += values[ sparseBooleanArray.keyAt(i) ] + ",";
//                            }
//                            i++ ;
//                        }
//                        ValueHolder = ValueHolder.replaceAll("(,)*$", "");
                        try {
                            if(dataModelArrayList1.size()>0){
                                for(int i1=0;i1<dataModelArrayList1.size();i1++){
                                    if(dataModelArrayList1.get(i1).contains(eachData.getString("RegisterFieldId"))){
                                        optionsObj.put(eachData.getString("Name"), dataModelArrayList1.get(i1).replace(eachData.getString("RegisterFieldId")+" ",""));
                                        JSONObject reqObj = new JSONObject();
                                        reqObj.put("RegisterFieldId",""+eachData.get("RegisterFieldId"));
                                        reqObj.put("Answer",""+dataModelArrayList1.get(i1).replace(eachData.getString("RegisterFieldId")+" ",""));
                                        reqs.put(reqObj);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    if (eachData.getString("QuestionType").equalsIgnoreCase("text")|| eachData.getString("QuestionType").equalsIgnoreCase("textarea")) {

                        try {
                            TextView textView = (TextView) allViewInstance.get(noOfViews);
                            if (!textView.getText().toString().equalsIgnoreCase(""))
                                optionsObj.put(eachData.getString("Name"), textView.getText().toString());
                            else
                                optionsObj.put(eachData.getString("Name"), textView.getText().toString());
                            JSONObject reqObj = new JSONObject();
                            reqObj.put("RegisterFieldId", "" + eachData.get("RegisterFieldId"));
                            reqObj.put("Answer", "" + textView.getText().toString());
                            reqs.put(reqObj);
                        }catch (Exception e){

                            optionsObj.put(eachData.getString("Name"),
                                    ""+radioselected);
                            JSONObject reqObj = new JSONObject();
                            reqObj.put("RegisterFieldId",""+eachData.get("RegisterFieldId"));
                            reqObj.put("Answer",""+textiewsselected.replace(eachData.getString("Name"),""));
                            reqs.put(reqObj);
                        }
                    }
                }
                Log.d("optionsObj  reqs ", reqs + "");
                hideSoftKeyboard(findViewById(R.id.layout));
                try {
                    SharedPreferences prefs = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE);
                    int surverytaken = prefs.getInt("surverytaken", 0);
                    Log.e("strrrrrr tak,",""+surverytaken);
                    surverytaken=surverytaken+1;
                    Log.e("strrrrrr tak,",""+surverytaken);
                    SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS_NAME", MODE_PRIVATE).edit();
                    editor.putInt("surverytaken",surverytaken);
                    editor.commit();
//                    if(resultlat==0.0f){
//                        resultlat= testLatitude;
//                        resultlong= testLongitude;
//                        String a=String.format("%.4f",resultlat);
//                        String b=String.format("%.4f",testLatitude);
//
//                        Log.e("strrrrracenterLatitudea", "" + a);
//                        Log.e("strrrrracenterLatitudeb", "" + b);
//                        Intent i = new Intent(SurveyDetailsActivity.this, ThankYouScreen.class);
//                        i.putExtra("reqs", "" + reqs);
//                        i.putExtra("formanews", "" + formanews);
//                        i.putExtra("formid", "" + formid);
//                        startActivity(i);
//                    }else {
//
//                        String a=String.format("%.4f",resultlat);
//                        String b=String.format("%.4f",testLatitude);
//                        String c=String.format("%.4f",resultlong);
//                        String d=String.format("%.4f",testLongitude);
//
//                        Log.e("strrrrracenterLatitudea", "" + a);
//                        Log.e("strrrrracenterLatitudeb", "" + b);
//
//                        if ((a).equalsIgnoreCase(b))
//                        {
//                            Toast.makeText(this, "You have already submited the survey from same location", Toast.LENGTH_SHORT).show();
//                        } else {
//                            resultlat= testLatitude;
//                            resultlong= testLongitude;
//                            Log.e("strrrrracenterLatitudee", "" + String.format("%.4f",resultlat));
//                            Log.e("strrrrracenterLatitudee", "" + String.format("%.4f",resultlong));
//                            Intent i = new Intent(SurveyDetailsActivity.this, ThankYouScreen.class);
//                            i.putExtra("reqs", "" + reqs);
//                            i.putExtra("formanews", "" + formanews);
//                            i.putExtra("formid", "" + formid);
//                            startActivity(i);
//                        }
//                    }
                    Intent i = new Intent(SurveyDetailsActivity.this, ThankYouScreen.class);
                    i.putExtra("reqs", "" + reqs);
                    i.putExtra("formanews", "" + formanews);
                    i.putExtra("formid", "" + formid);
                    startActivity(i);
                }catch (Exception e){

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            Toast.makeText(SurveyDetailsActivity.this, "Please complete all the question", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(SurveyDetailsActivity.this, MainActivity.class));
        finish();
    }


    public void hideSoftKeyboard(View v) {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
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
}