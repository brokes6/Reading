package com.example.reading.Fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.reading.Activity.AboutMe;
import com.example.reading.Activity.History;
import com.example.reading.Activity.LoginActivity;
import com.example.reading.Activity.Personal;
import com.example.reading.Activity.PostDetails;
import com.example.reading.Activity.Record_praise;
import com.example.reading.Activity.Set_up;
import com.example.reading.Activity.UserCommentActivity;
import com.example.reading.Activity.UserFeedBack;
import com.example.reading.Bean.User;
import com.example.reading.R;
import com.example.reading.constant.RequestUrl;
import com.example.reading.databinding.MyfragmentBinding;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.RequestStatus;
import com.example.reading.util.UserUtil;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;
import com.weavey.loading.lib.LoadingLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import me.jessyan.autosize.AutoSizeConfig;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.jessyan.autosize.internal.CustomAdapt;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class MyFragment extends Fragment implements View.OnClickListener {
    private View view;
    private User userData;
    private Bitmap bitmap ;//存放裁剪后的头像
    private String picturePath;//头像路径
    MyfragmentBinding binding;
    public static final int TAKE_PHOTO =1;
    public static final int CHOOSE_PHOTO=2;
    int REQUEST_CODE_ACTIVITY =1;
    private Uri imageUri;
    byte[] userimgurl;
    private static final String TAG = "MyFragment";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case RequestStatus.SUCCESS:
                    binding.userName.setText(userData.getUsername());
                    if (userData.getUimg()!=null){
                        binding.userImg.setImageURL(userData.getUimg());
                        binding.myload.setStatus(LoadingLayout.Success);
                    }else{
                        binding.userImg.setImageResource(R.mipmap.userimg);
                        binding.myload.setStatus(LoadingLayout.Success);
                        Log.d(TAG, "user"+"无头像");
                    }
                    break;
                case RequestStatus.FAILURE:
                    break;
            }
        }
    };
    public static MyFragment newInstance(String param1) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            AutoSizeConfig.getInstance().setCustomFragment(true);
            binding = DataBindingUtil.inflate(inflater,R.layout.myfragment,container,false);
            binding.myload.setStatus(LoadingLayout.Loading);
            userData= FileCacheUtil.getUser(getContext());
            initView();
            initData();
        return binding.getRoot();
    }
    private void initView(){
        binding.userImg.setOnClickListener(this);
        binding.myhistory.setOnClickListener(this);
        binding.msetting.setOnClickListener(this);
        binding.commentLayout.setOnClickListener(this);
        binding.mfeedback.setOnClickListener(this);
        binding.personal.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.about.setOnClickListener(this);
        binding.RecordOfPraise.setOnClickListener(this);
    }
    private void initData(){
        if (userData.getUsername()==null){
            Log.d(TAG, "user无数据!");
            Message mes=new Message();
            mes.what=RequestStatus.FAILURE;
            handler.sendMessage(mes);
        }else{
            Message mes=new Message();
            mes.what=RequestStatus.SUCCESS;
            handler.sendMessage(mes);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.msetting:
                intent = new Intent(getContext(), Set_up.class);
                startActivity(intent);
                break;
            case R.id.myhistory:
                intent = new Intent(getContext(), History.class);
                startActivity( intent);
                break;
            case R.id.user_img:
                break;
            case R.id.commentLayout:
                intent=new Intent(getContext(), UserCommentActivity.class);
                startActivity(intent);
                break;
            case R.id.mfeedback:
                intent=new Intent(getContext(), UserFeedBack.class);
                startActivity(intent);
                break;
            case R.id.personal:
                intent=new Intent(getContext(), Personal.class);
                startActivity(intent);
                break;
            case R.id.back:
                Toast.makeText(getContext(),"已退出",Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences= getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("key");
                editor.commit();
                Log.d(TAG, "---SharedPreferences的key已清除---");
                intent = new Intent("com.gesoft.admin.loginout");
                getContext().sendBroadcast(intent);
                break;
            case R.id.about:
                intent = new Intent(getContext(), AboutMe.class);
                startActivity(intent);
                break;
            case R.id.Record_of_praise:
                intent = new Intent(getContext(), Record_praise.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                if (intent != null) {
                    initData();
                }
            }
        }
    }

}
