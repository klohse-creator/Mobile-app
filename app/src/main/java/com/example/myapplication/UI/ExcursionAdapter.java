package com.example.myapplication.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.entities.Excursion;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private final TextView excursionItemView;
        private final TextView excursionItemView2;

        private ExcursionViewHolder(View itemView) {
            super(itemView);
            excursionItemView = itemView.findViewById(R.id.textView8);
            excursionItemView2 = itemView.findViewById(R.id.textView9);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final Excursion current = mExcursions.get(position);
                    Intent intent = new Intent(context, ExcursionDetails.class);
                    intent.putExtra("id", current.getExcursionID());
                    intent.putExtra("name", current.getExcursionName());
                    intent.putExtra("exDate", current.getExDate());
                    intent.putExtra("vacaID", current.getVacationID());
                    context.startActivity(intent);
                }
            });
        }
    }

    private List<Excursion> mExcursions;
    private final Context context;
    private final LayoutInflater mInflater;

    public ExcursionAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView=mInflater.inflate(R.layout.excursion_list_item, parent, false);
            return new ExcursionViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if(mExcursions!=null){
            Excursion current=mExcursions.get(position);
            String name=current.getExcursionName();
            int vacaID= current.getExcursionID();
            holder.excursionItemView.setText(name);
            holder.excursionItemView2.setText(Integer.toString(vacaID));
        }
        else{
            holder.excursionItemView.setText( "No Excursion name");
            holder.excursionItemView.setText("No Vacation ID ");
        }
    }

    public void setExcursions(List<Excursion> excursions){
        mExcursions=excursions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mExcursions != null) {
            return mExcursions.size();
        } else return 0;
    }

}









