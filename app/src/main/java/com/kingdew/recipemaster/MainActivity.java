package com.kingdew.recipemaster;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.kingdew.recipemaster.viewmodel.RecipeViewModel;

import io.noties.markwon.Markwon;

public class MainActivity extends AppCompatActivity {
    RecipeViewModel rViewModel;
    Button search;
    EditText editField;
    TextView textView;
    ProgressBar progressBar;
    Markwon markwon;

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
        search=findViewById(R.id.button);
        editField=findViewById(R.id.ingridField);
        textView=findViewById(R.id.responseConrainer);
        progressBar=findViewById(R.id.progressBar);
        markwon=Markwon.create(this);

        rViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        rViewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer visibility) {
                progressBar.setVisibility(visibility);
            }
        });
        rViewModel.getAiResponse().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                markwon.setMarkdown(textView,s);
            }
        });
        search.setOnClickListener(v->{
            String input = editField.getText().toString().trim();
            if (!input.isEmpty()) {
                rViewModel.generateRecipe(input);
            } else {
                editField.setError("Please enter some ingredients");
            }        });
    }
}