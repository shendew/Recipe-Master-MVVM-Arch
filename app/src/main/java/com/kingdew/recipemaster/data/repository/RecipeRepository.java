package com.kingdew.recipemaster.data.repository;


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RecipeRepository {
    private final Executor executor = Executors.newSingleThreadExecutor();
    GenerativeModelFutures model;
    public void getRecipe(String ingredients,RecipeCallback callback){

        String finalPrompt = String.format(
                "Act as a professional Masterchef. Generate a recipe for: %s. " +
                        "Return the result ONLY as a JSON object with keys: " +
                        "'mdData' (use stylish Markdown and emojis), 'youtubeLink' (search URL), " +
                        "and 'tags' (array). If the input is not related to food or is nonsense, " +
                        "respond ONLY with: 'Please try again later with proper manner'.",
                ingredients
        );

        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI()).generativeModel("gemini-3-flash-preview");

        model = GenerativeModelFutures.from(ai);

        Content propmt = new Content.Builder().addText(finalPrompt).build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(propmt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result.getText());
                    callback.onResponseRecived(jsonObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onErrorOccured();
            }
        },executor);
    }

    public interface RecipeCallback{
        void onResponseRecived(JSONObject recipe);
        void onErrorOccured();
    }
}
