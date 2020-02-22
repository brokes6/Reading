package com.example.reading.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Activity.ReadActivity;
import com.example.reading.Bean.BookDetails;
import com.example.reading.R;
import com.example.reading.util.RoundImageView;

import java.util.ArrayList;
import java.util.List;

public class ALLBookAdapter extends RecyclerView.Adapter<ALLBookHoder> {
    private static final String TAG = "ALLBookAdapter";
    private LayoutInflater inflater;
    private Context mContext;
    List<BookDetails>bookDetails = new ArrayList<>();
    public ALLBookAdapter(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }
    public void setALLBook(List<BookDetails> bookDetailsList){
        this.bookDetails.addAll(bookDetailsList);

    }
    @NonNull
    @Override
    public ALLBookHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.allbook_item,parent,false);
        ALLBookHoder allBookHoder = new ALLBookHoder(view);
        return allBookHoder;
    }

    @Override
    public void onBindViewHolder(@NonNull ALLBookHoder holder, int position) {
        final BookDetails bookDetails1 = bookDetails.get(position);
        Log.d(TAG, "onBindViewHolder: -----------"+"开始设置");
        holder.bookname.setText(bookDetails1.getBname());
        holder.bookdescription.setText(bookDetails1.getDescription());
        holder.bookimg.setImageURL(bookDetails1.getBimg());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ReadActivity.class);
                intent.putExtra("id",bookDetails1.getBid());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookDetails.size();
    }
}
class ALLBookHoder extends RecyclerView.ViewHolder{
    RoundImageView bookimg;
    TextView bookname,bookdescription;
    LinearLayout layout;

    public ALLBookHoder(@NonNull View itemView) {
        super(itemView);
        bookimg = itemView.findViewById(R.id.book_img);
        bookname = itemView.findViewById(R.id.book_name);
        bookdescription = itemView.findViewById(R.id.book_description);
        layout = itemView.findViewById(R.id.Allbook_book);
    }

}
