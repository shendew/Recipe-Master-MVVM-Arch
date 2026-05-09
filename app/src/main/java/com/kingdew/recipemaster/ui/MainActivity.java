package com.kingdew.recipemaster.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kingdew.recipemaster.R;
import com.kingdew.recipemaster.viewmodel.RecipeViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.noties.markwon.Markwon;

public class MainActivity extends AppCompatActivity {
    RecipeViewModel rViewModel;
    Button add;
    FloatingActionButton ytBtn;
    AppCompatButton generateBtn;
    EditText editField;
    TextView textView;
    ProgressBar progressBar;
    Markwon markwon;
    RecyclerView ingRecView;
    ArrayList<String> ingredientList;
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
                progressBar.setVisibility(visibility);
            }
        });
        rViewModel.getAiResponse().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                try {
                    String markdownContent = jsonObject.getString("mdData");
                    ytLink = jsonObject.getString("youtubeLink");
                    if (ytLink == null || ytLink.isEmpty()) {
                        ytBtn.setVisibility(View.GONE);
                    } else {
                        ytBtn.setVisibility(View.VISIBLE);
                    }
                    String tags = jsonObject.getString("tags");
                    markwon.setMarkdown(textView,markdownContent);
                } catch (JSONException e) {
                    markwon.setMarkdown(textView,"# Lets see what we got!");
                }

            }
        });
        ytBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(ytLink));
            startActivity(intent);
        });

        add.setOnClickListener(v->{
            String input = editField.getText().toString().trim();
            if (!input.isEmpty()) {
                rViewModel.addIngredient(input);
            } else {
                editField.setError("Please enter some ingredients");
            }
        });

        generateBtn.setOnClickListener(v -> {
            String ingredients = rViewModel.getIngredientsForAI();
            rViewModel.generateRecipe(ingredients);
        });
    }
}