package com.kingdew.recipemaster.viewmodel;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kingdew.recipemaster.data.repository.RecipeRepository;

import org.json.JSONObject;

import java.util.ArrayList;

public class RecipeViewModel extends ViewModel {

    MutableLiveData<JSONObject> mAiResponse = new MutableLiveData<>();
    MutableLiveData<Integer> mProgress = new MutableLiveData<>();
    MutableLiveData<ArrayList<String>> mIngredients = new MutableLiveData<>();

    RecipeRepository rRepository;
    public RecipeViewModel(){
        rRepository=new RecipeRepository();
        mProgress.postValue(View.INVISIBLE);
        mAiResponse.postValue(new JSONObject());
        mIngredients.postValue(new ArrayList<>());
    }

    public void addIngredient(String item){
        if (item==null || item.trim().isEmpty()) return;
        ArrayList<String> currentList = mIngredients.getValue();
        ArrayList<String> newList = (currentList == null) ? new ArrayList<>() : new ArrayList<>(currentList);

        newList.add(item);


        mIngredients.setValue(newList);


    }
    public void removeIngredient(int position){
        ArrayList<String> currentList = mIngredients.getValue();

        if (currentList != null && position >= 0 && position < currentList.size()) {
            ArrayList<String> newList = new ArrayList<>(currentList);
            newList.remove(position);
            mIngredients.setValue(newList);
        }
    }
    public void generateRecipe(String ing){
        mProgress.postValue(View.VISIBLE);
        rRepository.getRecipe(ing, new RecipeRepository.RecipeCallback() {
            @Override
            public void onResponseRecived(JSONObject recipe) {
                mProgress.postValue(View.INVISIBLE);

                mAiResponse.postValue(recipe);
            }
            @Override
            public void onErrorOccured() {
                mProgress.postValue(View.INVISIBLE);
            }
        });
    }
    public String getIngredientsForAI() {
        ArrayList<String> list = mIngredients.getValue();
        return (list != null) ? String.join(", ", list) : "";
    }
    public LiveData<ArrayList<String>> getIngredients(){
        return mIngredients;
    }
    public LiveData<Integer> getProgress(){
        return mProgress;
    }
    public LiveData<JSONObject> getAiResponse(){
        return mAiResponse;
    }
}
