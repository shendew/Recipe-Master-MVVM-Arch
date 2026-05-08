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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RecipeRepository {
    private final Executor executor = Executors.newSingleThreadExecutor();
    GenerativeModelFutures model;
    public void getRecipe(String ingredients,RecipeCallback callback){

//        String formattedIngredients = String.join(", ", ingredients);

        String finalPrompt = String.format(
                "Act as a professional chef. Create a recipe using these ingredients: %s. " +
                        "You may assume I have basic pantry staples like oil, salt, and pepper. " +
                        "Provide a concise recipe.",
                ingredients
        );

        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI()).generativeModel("gemini-3-flash-preview");

        model = GenerativeModelFutures.from(ai);

        Content propmt = new Content.Builder().addText(finalPrompt).build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(propmt);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String recipe = result.getText();
                callback.onResponseRecived(recipe);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onErrorOccured();
            }
        },executor);
    }

    public interface RecipeCallback{
        void onResponseRecived(String recipe);
        void onErrorOccured();
    }
}
