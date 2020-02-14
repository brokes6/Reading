package com.example.reading.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.reading.Activity.ReadActivity;
import com.example.reading.R;
import com.example.reading.ToolClass.BookDetails;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MAdapter_seller extends RecyclerView.Adapter<MyViewHolder_seller>{
    private LayoutInflater inflater;
    private Context mContext;
    private List<BookDetails> bookDetails=new ArrayList<>();
    //创建构造参数
    public MAdapter_seller(Context context){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }
    public void setBookDetails(List<BookDetails> bookDetailsList) {
        this.bookDetails.addAll(bookDetailsList);
    }

    public void setMAdapter_seller(List<BookDetails> bookDetails) {
        this.bookDetails.addAll(bookDetails);
        Log.i(TAG, "setOrganizations: 我被设置了啊"+this.bookDetails.size());
    }
    //创建ViewHolder
    @Override
    public MyViewHolder_seller onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.book_item , parent , false);
        MyViewHolder_seller myViewHolder_seller = new MyViewHolder_seller(view);
        return myViewHolder_seller;
    }
    //绑定ViewHolder
    @Override
    public void onBindViewHolder(MyViewHolder_seller holder, int position) {
        //为textview 赋值
        Log.i(TAG, "MAdapter_seller: 开始设置");
        final BookDetails bookDetails1 = bookDetails.get(position);
        Log.i(TAG, "MAdapter_seller: " + bookDetails1.getBimg());
        Uri uri = Uri.parse(bookDetails1.getBimg());
        holder.bookname.setText(bookDetails1.getBname());
//        holder.bookimg.setImageURI(uri);
        Glide.with(mContext).load(uri).into(holder.bookimg);
       holder.whole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ReadActivity.class);
                intent.putExtra("id",bookDetails1.getBid());
                Log.d(TAG, "onClick: MAdapter适配器"+"传值完成,书籍id为："+bookDetails1.getBid());
                mContext.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return bookDetails.size();

    }
}
class MyViewHolder_seller extends RecyclerView.ViewHolder{

    ImageView bookimg;
    TextView bookname;
    LinearLayout whole;

    public MyViewHolder_seller(View itemView) {
        super(itemView);
        whole = itemView.findViewById(R.id.Whole);
        bookname = itemView.findViewById(R.id.book1_name);
        bookimg = itemView.findViewById(R.id.book1_img);

    }
}
