package com.kingdew.recipemaster.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kingdew.recipemaster.R;
import com.kingdew.recipemaster.viewmodel.RecipeViewModel;

import java.util.ArrayList;

public class RecipeItemAdapter extends RecyclerView.Adapter<RecipeItemAdapter.ViewHolder> {

    Context context;
    ArrayList<String> ingridientList;

    public RecipeItemAdapter(Context context, ArrayList<String> ingridientList) {
        this.context = context;
        this.ingridientList = ingridientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.ing_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ing=ingridientList.get(position);
        holder.ingTextView.setText(ing);

        holder.cardView.setOnClickListener(v->{
            RecipeViewModel model =new ViewModelProvider((ViewModelStoreOwner) context).get(RecipeViewModel.class);
            model.removeIngredient(position);
        });
    }

    @Override
    public int getItemCount() {
        return ingridientList.size();
    }

    public void updateList(ArrayList<String> ingredientList) {
        this.ingridientList=ingredientList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        MaterialCardView cardView;
        TextView ingTextView;
        public ViewHolder(View view){
            super(view);
            cardView=view.findViewById(R.id.ingCardView);
            ingTextView=view.findViewById(R.id.ingTextField);
        }
    }
}
