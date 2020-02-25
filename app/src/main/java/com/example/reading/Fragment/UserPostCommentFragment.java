package com.example.reading.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Bean.UserPostComment;
import com.example.reading.R;
import com.example.reading.adapter.UserPostCommentAdapter;
import com.example.reading.util.UserUtil;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.reading.constant.RequestUrl.*;

public class UserPostCommentFragment extends Fragment {
    private View mView;
    private RecyclerView recyclerView;
    private UserPostCommentAdapter userPostCommentAdapter;
    private int currentPage=1;
    private List<UserPostComment> userPostComments=new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView=LayoutInflater.from(getContext()).inflate(R.layout.comment_layout,container,false);
        recyclerView=mView.findViewById(R.id.recyclerView);
        initData();
        return mView;
    }
    private void initData(){
        userPostCommentAdapter=new UserPostCommentAdapter(userPostComments,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(userPostCommentAdapter);
        findUserPostComment();
    }

    public void findUserPostComment(){
        Map<String,String> params= UserUtil.createUserMap();
        params.put("currentPage", String.valueOf(currentPage));
        StandardRequestMangaer.getInstance()
                .get(FIND_USER_POST_COMMENT, new BaseCallBack<List<UserPostComment>>() {
                    @Override
                    protected void OnRequestBefore(Request request) {

                    }

                    @Override
                    protected void onFailure(Call call) {

                    }

                    @Override
                    protected void onSuccess(Call call, Response response, List<UserPostComment>list) {
                        Toast.makeText(getContext(), "获得帖子评论成功！", Toast.LENGTH_SHORT).show();
                        userPostComments.addAll(list);
                        userPostCommentAdapter.notifyDataSetChanged();
                    }


                    @Override
                    protected void onResponse(Response response) {

                    }

                    @Override
                    protected void onEror(Call call, int statusCode) {

                    }

                    @Override
                    protected void inProgress(int progress, long total, int id) {

                    }
                },params);
    }
}
