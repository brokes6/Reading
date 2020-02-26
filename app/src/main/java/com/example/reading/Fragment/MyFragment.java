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
                showTypeDialog();
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
                Intent intent3 = new Intent("com.gesoft.admin.loginout");
                getContext().sendBroadcast(intent3);
                break;
            case R.id.about:
                Intent intent4 = new Intent(getContext(), AboutMe.class);
                startActivity(intent4);
                break;
        }
    }
    private void showTypeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(getActivity(), R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                binding.myload.setStatus(LoadingLayout.Loading);
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                binding.myload.setStatus(LoadingLayout.Loading);
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
                    imageUri=Uri.fromFile(outputImage);
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
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){
                    try {
                        Bitmap bitmap= BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri));
                        userimgurl = bitmapToByte(bitmap);
                        String ans=imageUri.toString();
                        userData.setUimg(ans);
                        FileCacheUtil.updateUser(userData, getContext());
                        binding.userImg.setImageBitmap(bitmap);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
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
            binding.userImg.setImageURI(ans);
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
        Log.d(TAG, "adduserimg: ---------"+file);
        StandardRequestMangaer.getInstance().postImage(RequestUrl.ADD_USER_IMG, new BaseCallBack<String>() {
            @Override
            protected void OnRequestBefore(Request request) { }

            @Override
            protected void onFailure(Call call) { }

            @Override
            protected void onSuccess(Call call, Response response, String s) {
                Log.i(TAG, "onSuccess: 图片路径:"+s);
                userData.setUimg(s);
                FileCacheUtil.updateUser(userData, getContext());
                binding.myload.setStatus(LoadingLayout.Success);
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
    //需要改变适配尺寸的时候，在重写这两个方法

}
