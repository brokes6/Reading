package com.example.reading.Fragment;

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

import com.example.reading.Activity.History;
import com.example.reading.Activity.LoginActivity;
import com.example.reading.Activity.Set_up;
import com.example.reading.Bean.User;
import com.example.reading.R;
import com.example.reading.databinding.MyfragmentBinding;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.RequestStatus;
import com.weavey.loading.lib.LoadingLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.jessyan.autosize.internal.CustomAdapt;

import static android.app.Activity.RESULT_OK;

public class MyFragment extends Fragment implements CustomAdapt,View.OnClickListener {
    private View view;
    private User userData;
    private Bitmap bitmap ;//存放裁剪后的头像
    private String picturePath;//头像路径
    MyfragmentBinding binding;
    public static final int TAKE_PHOTO =1;
    public static final int CHOOSE_PHOTO=2;
    private Uri imageUri;
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
        binding.setting.setOnClickListener(this);

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
        switch (v.getId()){
            case R.id.setting:
                Intent intent = new Intent(getContext(), Set_up.class);
                startActivity(intent);
                break;
            case R.id.myhistory:
                Intent intent1 = new Intent(getContext(), History.class);
                startActivity(intent1);
                break;
            case R.id.user_img:
                showTypeDialog();
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
                        String ans=imageUri.toString();
                        userData.setUimg(ans);
                        Log.d(TAG, "displayImage: 更换的图片为-------"+imageUri);
                        FileCacheUtil.updateUser(userData, getContext());
//                        muserOperator.updateImage(bean);
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
            //Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            //imageView.setImageBitmap(bitmap);
            Uri ans=getImageContentUri(getContext(),imagePath);
            String bf=ans.toString();
            userData.setUimg(imagePath);
            Log.d(TAG, "displayImage: 更换的图片为-------"+imagePath);
            FileCacheUtil.updateUser(userData, getContext());

            binding.userImg.setImageURI(ans);
        }
        else
        {
            Toast.makeText(getContext(),"获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    //需要改变适配尺寸的时候，在重写这两个方法
    @Override
    public boolean isBaseOnWidth() {
        return false;
    }
    @Override
    public float getSizeInDp() {
        return 640;
    }
}
