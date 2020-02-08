package com.example.reading.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reading.R;
import com.example.reading.ToolClass.BookDetails;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MAdapter extends RecyclerView.Adapter<MyViewHolder>{
    private LayoutInflater inflater;
    private Context mContext;

    private List<BookDetails> bookDetails=new ArrayList<>();
    //创建构造参数
    public MAdapter(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }
    public void setBookDetails(List<BookDetails> bookDetailsList) {
        this.bookDetails.addAll(bookDetailsList);
    }

    public void setMyAdapter(List<BookDetails> bookDetails) {
        this.bookDetails.addAll(bookDetails);
        Log.i(TAG, "setOrganizations: 我被设置了啊"+this.bookDetails.size());
    }

    //创建ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.book_item , parent , false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }
    //绑定ViewHolder
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //为textview 赋值
        Log.i(TAG, "onBindViewHolder: 开始设置");
        final BookDetails bookDetails1 = bookDetails.get(position);
        Log.i(TAG, "onBindViewHolder: " + bookDetails1.getBimg());
        Uri uri = Uri.parse(bookDetails1.getBimg());
        holder.bookname.setText(bookDetails1.getBname());
//        holder.bookimg.setImageURI(uri);
        Glide.with(mContext).load(uri).into(holder.bookimg);
    /*    holder.whole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, BookTest.class);
                intent.putExtra("type",bookDetails1.getAuthor());
                intent.putExtra("type",bookDetails1.getBookimg());
                intent.putExtra("type",bookDetails1.getBookintroduce());
                intent.putExtra("type",bookDetails1.getType());
                intent.putExtra("vurl",bookDetails1.getVideo_path());
                intent.putExtra("murl",bookDetails1.getMusic_path());
                mContext.startActivity(intent);
            }
        });
    }*/
    }

    @Override
    public int getItemCount() {
        return bookDetails.size();

    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    TextView number_of_visitors;
    ImageView bookimg;
    TextView bookname;
    LinearLayout whole;

    public MyViewHolder(View itemView) {
        super(itemView);
        whole = itemView.findViewById(R.id.Whole);
        bookname = itemView.findViewById(R.id.book1_name);
        bookimg = itemView.findViewById(R.id.book1_img);
        number_of_visitors = itemView.findViewById(R.id.number_of_visitors);

    }
}