package com.example.reading.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reading.Bean.User;
import com.example.reading.Fragment.MyFragment;
import com.example.reading.MainActivity;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.constant.RequestUrl;
import com.example.reading.databinding.ActivityPersonalBinding;
import com.example.reading.util.ActivityCollector;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.UserUtil;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;
import com.weavey.loading.lib.LoadingLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.reading.MainApplication.getContext;

public class Personal extends BaseActivity implements View.OnClickListener {
    private User userData;
    ActivityPersonalBinding binding;
    public static final int TAKE_PHOTO =1;
    public static final int CHOOSE_PHOTO=2;
    int REQUEST_CODE_ACTIVITY =1;
    int REQUEST_CODE_ACTIVITY1 =1;
    int REQUEST_CODE_ACTIVITY2 =2;
    int REQUEST_CODE_ACTIVITY3 =3;
    private Uri imageUri;
    byte[] userimgurl;
    private static final String TAG = "Personal";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_personal);
        binding.loading.setStatus(LoadingLayout.Loading);
        userData= FileCacheUtil.getUser(getContext());
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initData();
        initView();
    }
    public void  initView(){
        binding.setBack.setOnClickListener(this);
        binding.phoneNumber.setOnClickListener(this);
        binding.email.setOnClickListener(this);
        binding.puserimg.setOnClickListener(this);
    }
    private void initData(){
        binding.puserimg.setImageURL(userData.getUimg());
        binding.pusername.setText(userData.getUsername());
        binding.loading.setStatus(LoadingLayout.Success);
        binding.number.setText("绑定手机号为: "+userData.getPhone_number());
        if (userData.getPhone_number()==null){
            binding.number.setText("暂无绑定手机号");
        }
        binding.emailNumber.setText("绑定邮箱为: "+userData.getEmail());
        if (userData.getEmail() ==null){
            binding.emailNumber.setText("暂无绑定邮箱");
        }
    }
    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.set_back:
                intent  = new Intent(Personal.this, MainActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ACTIVITY);
                finish();
                break;
            case R.id.phone_number:
                intent = new Intent(getContext(), ChangePhoneNumber.class);
                startActivity(intent);
                break;
            case R.id.email:
                intent = new Intent(getContext(), Changing_Mailbox.class);
                startActivity(intent);
                break;
            case R.id.puserimg:
                showTypeDialog();
                break;
        }
    }

    private void showTypeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Personal.this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(Personal.this, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                binding.loading.setStatus(LoadingLayout.Loading);
                if (ContextCompat.checkSelfPermission(Personal.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Personal.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                binding.loading.setStatus(LoadingLayout.Loading);
                File outputImage =new File(getContext().getExternalCacheDir(),"output_image.jpg");
                try {
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri= FileProvider.getUriForFile(getContext(),
                            "com.example.cameraalbumtest.fileprovider",outputImage);
                }else{
                    imageUri= Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }
    private void openAlbum() {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }
                else{
                    Toast.makeText(getContext(),"你否定了打开相册权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri));
                    userimgurl = bitmapToByte(bitmap);
                    String ans = imageUri.toString();
                    userData.setUimg(ans);
                    FileCacheUtil.updateUser(userData, getContext());
                    binding.puserimg.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= 19) {
                    handleImageOnKitKat(data);
                } else {
                    handleImageBeforeKitKat(data);
                }
            }
        } else if (requestCode == REQUEST_CODE_ACTIVITY1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    initData();
                }
            }
        }else if (requestCode == REQUEST_CODE_ACTIVITY2) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    initData();
                }
            }
        }else if (requestCode == REQUEST_CODE_ACTIVITY3) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    initData();
                }
            }//不知道什么原因，不能使用switch ()....
        }
    }
    /**
     * 将图片转换成Uri
     * @param context   传入上下文参数
     * @param path      图片的路径
     * @return      返回的就是一个Uri对象
     */
    public static Uri getImageContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { path }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            // 如果图片不在手机的共享图片数据库，就先把它插入。
            if (new File(path).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    private static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imgBytes = baos.toByteArray();
        return imgBytes;
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(getContext(),uri)){
            //如果是document类型的uri则通过document id处理
            String docId= DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];//解析为数字格式的id
                String selection= MediaStore.Images.Media._ID +"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }else if("content".equalsIgnoreCase(uri.getScheme())){
                //如果是content类型的uri则使用普通方式处理
                imagePath=getImagePath(uri,null);
            }else if("file".equalsIgnoreCase(uri.getScheme())){
                //如果是file类型的uri直接获取图片路径就好
                imagePath=uri.getPath();
            }
            displayImage(imagePath);//根据图片的路径显示图片
        }
    }
    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri,String selection){
        String path=null;
        //通过uri和selection来获取真实的图片路径
        Cursor cursor=getContext().getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath) {
        if(imagePath!=null)
        {
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            Uri ans=getImageContentUri(getContext(),imagePath);
            String bf=ans.toString();
            binding.puserimg.setImageURI(ans);
            adduserimg(imagePath);
        }
        else
        {
            Toast.makeText(getContext(),"获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }
    private void adduserimg(String s){
        Map<String,String> params= UserUtil.createUserMap();
        File file=new File(s);
        StandardRequestMangaer.getInstance().postImage(RequestUrl.ADD_USER_IMG, new BaseCallBack<String>() {
            @Override
            protected void OnRequestBefore(Request request) { }

            @Override
            protected void onFailure(Call call) { }

            @Override
            protected void onSuccess(Call call, Response response, String s) {
                Log.i(TAG, "onSuccess: 图片路径:"+s);
                updateUserInfo();
                userData.setUimg(s);
                FileCacheUtil.updateUser(userData, getContext());
                binding.loading.setStatus(LoadingLayout.Success);
                Toast.makeText(getContext(),"设置头像成功!",Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onResponse(Response response) { }

            @Override
            protected void onEror(Call call, int statusCode) {
                Log.d(TAG, "错误"); }

            @Override
            protected void inProgress(int progress, long total, int id) { }
        },"file",file,params);
    }
    private void updateUserInfo(){
        Map<String,String> params= UserUtil.createUserMap();
        params.put("username",userData.getUsername());
        params.put("uimg",userData.getUimg());
        StandardRequestMangaer.getInstance().post(RequestUrl.CHANGE_USER_INFORMATION,new BaseCallBack<String>(){

            @Override
            protected void OnRequestBefore(Request request) {

            }

            @Override
            protected void onFailure(Call call) {

            }

            @Override
            protected void onSuccess(Call call, Response response, String s) {
                Toast.makeText(getContext(),"头像上传成功!",Toast.LENGTH_SHORT).show();

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
    @Override
    public void onBackPressed() {
        Intent intent  = new Intent(Personal.this, MainActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ACTIVITY);
        finish();
    }
}
