package com.kingdew.recipemaster.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kingdew.recipemaster.R;
import com.kingdew.recipemaster.viewmodel.RecipeViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.noties.markwon.Markwon;

public class MainActivity extends AppCompatActivity {
    RecipeViewModel rViewModel;
    Button add,reTryButton;
    FloatingActionButton ytBtn;
    RelativeLayout generateBtn,emptuContainer;
    EditText editField;
    TextView textView,aiLoadingText;
    ProgressBar progressBar;
    Markwon markwon;
    RecyclerView ingRecView;
    ArrayList<String> ingredientList;
    LottieAnimationView aiLoadingView;
    LinearLayout inputInsideLay;
    private String ytLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        add=findViewById(R.id.button);
        generateBtn=findViewById(R.id.generateBtn);
        ytBtn=findViewById(R.id.ytBtn);
        editField=findViewById(R.id.ingridField);
        textView=findViewById(R.id.responseContainer);
        progressBar=findViewById(R.id.progressBar);
        markwon=Markwon.create(this);
        ingRecView=findViewById(R.id.ingRecView);
        aiLoadingView=findViewById(R.id.aiLoadingView);
        aiLoadingText=findViewById(R.id.aiLoadingText);
        emptuContainer=findViewById(R.id.emptuContainer);
        inputInsideLay=findViewById(R.id.inputInsideLay);
        reTryButton=findViewById(R.id.reTryButton);
        ingredientList=new ArrayList<>();
        ingRecView.setHasFixedSize(true);
        ingRecView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        RecipeItemAdapter recipeItemAdapter=new RecipeItemAdapter(MainActivity.this,ingredientList);
        ingRecView.setAdapter(recipeItemAdapter);
        rViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        rViewModel.getIngredients().observe(this, new Observer<ArrayList<String>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(ArrayList<String> strings) {
                editField.setText("");
                ingredientList.clear();
                ingredientList=strings;
                recipeItemAdapter.updateList(ingredientList);
            }
        });
        rViewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer visibility) {
                generateBtn.setBackgroundColor(getColor(visibility==View.VISIBLE? R.color.grey:R.color.base_color));
                aiLoadingText.setVisibility(visibility==View.VISIBLE?View.GONE:View.VISIBLE);
                aiLoadingView.setVisibility(visibility);
                progressBar.setVisibility(visibility);
            }
        });
        rViewModel.getAiResponse().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                try {
                    String markdownContent = jsonObject.getString("mdData");
                    ytLink = jsonObject.getString("youtubeLink");
                    inputInsideLay.setVisibility(View.GONE);
                    reTryButton.setVisibility(View.VISIBLE);
                    if (ytLink == null || ytLink.isEmpty()) {
                        ytBtn.setVisibility(View.GONE);
                    } else {
                        ytBtn.setVisibility(View.VISIBLE);
                    }
                    String tags = jsonObject.getString("tags");
                    markwon.setMarkdown(textView,markdownContent);
                    emptuContainer.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    markwon.setMarkdown(textView,"# Lets see what we got!");
                }

            }
        });
        editField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO){
                    String input = editField.getText().toString().trim();
                    addIng(input);
                    return true;
                }
                return false;
            }
        });
        editField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().contains(",")){
                    String input = s.toString().replace(",","").trim();
                    addIng(input);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        ytBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(ytLink));
            startActivity(intent);
        });

        add.setOnClickListener(v->{
            String input = editField.getText().toString().trim();
            addIng(input);
        });

        generateBtn.setOnClickListener(v -> {
            String ingredients = rViewModel.getIngredientsForAI();
            if (ingredients.isEmpty()) {
                Toast.makeText(this, "Please enter ingredients", Toast.LENGTH_SHORT).show();
            }else {
                rViewModel.generateRecipe(ingredients);
            }
        });
        reTryButton.setOnClickListener(v -> {
            inputInsideLay.setVisibility(View.VISIBLE);
            reTryButton.setVisibility(View.GONE);
            ytBtn.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            emptuContainer.setVisibility(View.VISIBLE);
        });
    }
    private void addIng(String ing){
        if (!ing.isEmpty()) {
            rViewModel.addIngredient(ing);
        } else {
            editField.setError("Please enter some ingredients");
        }
    }
}