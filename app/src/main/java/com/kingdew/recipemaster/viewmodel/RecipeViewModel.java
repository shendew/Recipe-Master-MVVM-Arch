package com.kingdew.recipemaster.viewmodel;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kingdew.recipemaster.data.repository.RecipeRepository;

public class RecipeViewModel extends ViewModel {

    MutableLiveData<String> mAiResponse = new MutableLiveData<>();
    MutableLiveData<Integer> mProgress = new MutableLiveData<>();

    RecipeRepository rRepository;
    public RecipeViewModel(){
        rRepository=new RecipeRepository();
        mProgress.postValue(View.INVISIBLE);
        mAiResponse.postValue("");
    }

    public void generateRecipe(String ing){
        mProgress.postValue(View.VISIBLE);
        rRepository.getRecipe(ing, new RecipeRepository.RecipeCallback() {
            @Override
            public void onResponseRecived(String recipe) {
                mProgress.postValue(View.INVISIBLE);
                mAiResponse.postValue(recipe);
            }

            @Override
            public void onErrorOccured() {
                mProgress.postValue(View.INVISIBLE);
            }
        });
    }

    public LiveData<Integer> getProgress(){
        return mProgress;
    }
    public LiveData<String> getAiResponse(){
        return mAiResponse;
    }
}
