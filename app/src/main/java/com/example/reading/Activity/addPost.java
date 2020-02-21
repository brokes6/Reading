package com.example.reading.Activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Bean.LoadFileVo;
import com.example.reading.Bean.User;
import com.example.reading.MainActivity;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.ToolClass.ColorPickerView;
import com.example.reading.adapter.LoadPicAdapter;
import com.example.reading.util.FileCacheUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.reading.MainApplication.getContext;

public class addPost extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "addPost";
    public static final int SHOW_TOAST=3;
    public static final int CHOOSE_PHOTO = 2;
    public static final int SET_OK = 1;
    public static final String PICTURE_FILE="temp.jpg";
    private String imgString = "";
    private StringBuilder builder = new StringBuilder();
    List<LoadFileVo> fileList = new ArrayList<>();
    private int tagId;
    private AlertDialog alertDialog2;
    LoadPicAdapter adapter = null;
    private LinearLayout loadLayout;
    private TextView loadTextView;
    RecyclerView rvPic;
    private Button sendButton;
    TextView tvNum;
    boolean isRequestHttp = false;
    private Uri imageUri=null;
    private String token;
    //文本编辑器
    private RichEditor mEditor;
    //加粗按钮
    private ImageView mBold;
    //颜色编辑器
    private TextView mTextColor;
    //显示显示View
    private LinearLayout llColorView;
    //按序号排列（ol）
    private ImageView mListOL;
    //按序号排列（ul）
    private ImageView mListUL;
    //字体下划线
    private ImageView mLean;
    //字体倾斜
    private ImageView mItalic;
    //字体左对齐
    private ImageView mAlignLeft;
    //字体右对齐
    private ImageView mAlignRight;
    //字体居中对齐
    private ImageView mAlignCenter;
    //字体缩进
    private ImageView mIndent;
    //字体较少缩进
    private ImageView mOutdent;
    //字体索引
    private ImageView mBlockquote;
    //字体中划线
    private ImageView mStrikethrough;
    //字体上标
    private ImageView mSuperscript;
    //字体下标
    private ImageView mSubscript;
    /********************boolean开关**********************/
    //是否加粗
    boolean isClickBold = false;
    //是否正在执行动画
    boolean isAnimating = false;
    //是否按ol排序
    boolean isListOl = false;
    //是否按ul排序
    boolean isListUL = false;
    //是否下划线字体
    boolean isTextLean = false;
    //是否下倾斜字体
    boolean isItalic = false;
    //是否左对齐
    boolean isAlignLeft = false;
    //是否右对齐
    boolean isAlignRight = false;
    //是否中对齐
    boolean isAlignCenter = false;
    //是否缩进
    boolean isIndent = false;
    //是否较少缩进
    boolean isOutdent = false;
    //是否索引
    boolean isBlockquote = false;
    //字体中划线
    boolean isStrikethrough = false;
    //字体上标
    boolean isSuperscript = false;
    //字体下标
    boolean isSubscript = false;
    /********************变量**********************/
    //折叠视图的宽高
    private int mFoldedViewMeasureHeight;

    private File file;
    private User userData;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_OK:
                    TextView view = (TextView) msg.obj;
                    String num = msg.arg1 + "/9";
                    view.setText(num);
                    loadLayout.setVisibility(View.GONE);
                    break;
                case SHOW_TOAST:
                    Toast.makeText(addPost.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post);
        //更改系统通知栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //修改为深色，因为我们把状态栏的背景色修改为主题色白色，默认的文字及图标颜色为白色，导致看不到了。
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        userData= FileCacheUtil.getUser(getContext());
        initView();
        initAdapter();
        initData();
        initClickListener();
    }
    //initAdapter方法
    private void initAdapter() {
        fileList.add(new LoadFileVo());
        adapter = new LoadPicAdapter(this, fileList, 9);
        rvPic.setAdapter(adapter);
        rvPic.setLayoutManager(new GridLayoutManager(this, 3));
        adapter.setListener(new LoadPicAdapter.OnItemClickListener() {
            @Override
            public void click(View view, int positon) {
                if (fileList.size() > 9) {
                    Toast.makeText(addPost.this, "一次最多上传9张图片！", Toast.LENGTH_SHORT).show();
                } else {
                    selectPic();  //调用加载图片方法
                }

            }
            @Override
            public void del(View view) {
                tvNum.setText((fileList.size() - 1) + "/9");
            }
        });
    }
    //自定义方法selectPic
    private void selectPic() {
        //动态请求权限，除此之外还需进行Androidmanifest.xml中进行请求
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        final CharSequence[] items = {"相册", "拍照"};
        AlertDialog.Builder dlg = new AlertDialog.Builder(addPost.this);
        dlg.setTitle("添加图片");
        dlg.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // 这里item是根据选择的方式，
                if (item == 0) {
                    if (ContextCompat.checkSelfPermission(addPost.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(addPost.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent,  0); // 打开相册
                    }
                } else {
                    try {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        file=cratephotofile();
                        if(!file.getParentFile().exists()){
                            file.getParentFile().mkdir();
                        }
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            imageUri= FileProvider.getUriForFile(addPost.this,"com.example.bottomnavigationabar2.fileprovider",file);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                        }else{
                            imageUri = Uri.fromFile(file);
                        }
                        startActivityForResult(intent,1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        alertDialog2 = dlg.create();
        //设置AlertDialog长度
        alertDialog2.getWindow().setLayout(300,200);
        alertDialog2.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 0); // 打开相册
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.
                    getAuthority())) {
                Log.i(TAG, "handleImageOnKitKat: 文档");
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.i(TAG, "handleImageOnKitKat: content");
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.i(TAG, "handleImageOnKitKat: 文件");
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.
                        Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            file=new File(imagePath);
            Log.i(TAG, "displayImage: 相册选择="+file.length());
            netUploadImg();
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }


    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExsit = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExsit) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.toString();
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    //重写onActivityResult方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==0){
            Toast.makeText(this,"返回发帖页面",Toast.LENGTH_SHORT);
            return;
        }
        if (requestCode == 1) {
            Log.i(TAG, "onActivityResult: "+file.length());
            netUploadImg();
        }
        if (requestCode == 0) {
            Log.i(TAG, "onActivityResult: 11");
            if (Build.VERSION.SDK_INT >= 19) {
                // 4.4及以上系统使用这个方法处理图片
                handleImageOnKitKat(data);
            } else {
                // 4.4以下系统使用这个方法处理图片
                handleImageBeforeKitKat(data);
            }
        }

    }
    //一张张图片轮流上传
    public void netUploadImg(){
        loadLayout.setVisibility(View.VISIBLE);
        try {
            MediaType type =MediaType.parse("image/*");
            String name=file.getName();
            Log.i(TAG, "netUploadImg: "+name);
            name=name.substring(name.lastIndexOf(".")+1);
            RequestBody body=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file",name,RequestBody.create(MediaType.parse("application/octet-stream"),file))
                    .build();
            final Request request = new Request.Builder()
                    .url("http://117.48.205.198/xiaoyoudushu/addBanner?")
                    .post(body)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onFailure:失败呃");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String urls = response.body().string();
                    Log.i(TAG, "onResponse:urls"+urls);
                    int index1 = urls.indexOf("http");
                    int index2 = urls.lastIndexOf("\"");
                    urls = (String) urls.subSequence(index1, index2);
                    Log.i(TAG, "onResponse: " + urls);
                    builder.append(urls + ",");
                    adapter.getHolder().ivPic.setImageURL(urls);
                    setNum();
                    if(adapter.fileList.size()<9){
                        fileList.add(new LoadFileVo());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            isRequestHttp = true;
            showShortToast("上传图片请求异常！");

        }
    }

    private void showShortToast(String msg) {
        Message message=new Message();
        message.obj=msg;
        message.what=SHOW_TOAST;
        handler.sendMessage(message);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void setNum() {
        Message message = Message.obtain();
        message.arg1 = fileList.size();
        message.obj = tvNum;
        message.what = 1;
        handler.sendMessage(message);
    }
    //暂时有问题 服务器图片长度设置过小 弄大点
    public void netUploadPost(String token) throws FileNotFoundException {//用jsonOject方式转string传递其他参数
        try {
            String imgUrl=builder.toString();
            if(!imgUrl.trim().equals("")) {
                imgUrl = imgUrl.substring(0, imgUrl.length() - 1);
                System.out.println("imgurl=="+imgUrl);
            }
            FormBody body = new FormBody.Builder()
                    .add("content", mEditor.getHtml())
                    .add("imgUrl",imgUrl)
                    .add("token", token)
                    .build();
            final Request request = new Request.Builder()
                    .url("http://117.48.205.198/xiaoyoudushu/addPost?").post(body)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onFailure:失败呃");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i(TAG, "onResponse: 发帖情况" + response.body().string());
                    showShortToast("发帖成功！");
                    Intent intent = new Intent(addPost.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            isRequestHttp = true;
            showShortToast("上传图片请求异常！");

        }
    }
    /**
     * 初始化View
     */
    private void initView() {
        ImageView back = findViewById(R.id.title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // super.onBackPressed();//注释掉这行,back键不退出activity
                AlertDialog.Builder dialog = new AlertDialog.Builder(addPost.this);
                dialog.setTitle("提醒");
                dialog.setMessage("是否保存");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(addPost.this,"已保存",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                dialog.show();
            }
        });
        rvPic = findViewById(R.id.rvPic);
        tvNum = findViewById(R.id.tvNum);
        sendButton = findViewById(R.id.sendPost);
        loadLayout=findViewById(R.id.loadLayout);
        loadTextView=findViewById(R.id.loadTextView);
        //发送的点击事件
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转完成后，需要调用重新刷新
                try {
                    netUploadPost(token);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                };
            }
        });
        initEditor();
        initMenu();
        initColorPicker();
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();//注释掉这行,back键不退出activity
        AlertDialog.Builder dialog = new AlertDialog.Builder(addPost.this);
        dialog.setTitle("提醒");
        dialog.setMessage("是否保存");
        dialog.setCancelable(false);
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(addPost.this,"已保存",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        dialog.setNegativeButton("否", new DialogInterface.
                OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.show();
    }



    /**
     * 初始化文本编辑器
     */
    private void initEditor() {
        mEditor = findViewById(R.id.re_main_editor);

        mEditor.setEditorHeight(120);
        //输入框显示字体的大小
        mEditor.setEditorFontSize(18);
        //输入框显示字体的颜色
        mEditor.setEditorFontColor(Color.BLACK);
        //输入框背景设置
        mEditor.setEditorBackgroundColor(Color.WHITE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //输入框文本padding
        mEditor.setPadding(10, 10, 10, 10);
        //输入提示文本
        mEditor.setPlaceholder("请输入编辑内容");
        //是否允许输入
        //mEditor.setInputEnabled(false);
        //文本输入框监听事件
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                Log.d("mEditor", "html文本：" + text);
            }
        });
    }

    /**
     * 初始化颜色选择器
     */
    private void initColorPicker() {
        ColorPickerView right = findViewById(R.id.cpv_main_color);
        right.setOnColorPickerChangeListener(new ColorPickerView.OnColorPickerChangeListener() {
            @Override
            public void onColorChanged(ColorPickerView picker, int color) {
                mTextColor.setBackgroundColor(color);
                mEditor.setTextColor(color);
            }

            @Override
            public void onStartTrackingTouch(ColorPickerView picker) {

            }

            @Override
            public void onStopTrackingTouch(ColorPickerView picker) {

            }
        });
    }

    /**
     * 初始化菜单按钮
     */
    private void initMenu() {
        mBold = findViewById(R.id.button_bold);
        mTextColor = findViewById(R.id.button_text_color);
        llColorView = findViewById(R.id.ll_main_color);
        mListOL = findViewById(R.id.button_list_ol);
        mListUL = findViewById(R.id.button_list_ul);
        mLean = findViewById(R.id.button_underline);
        mItalic = findViewById(R.id.button_italic);
        mAlignLeft = findViewById(R.id.button_align_left);
        mAlignRight = findViewById(R.id.button_align_right);
        mAlignCenter = findViewById(R.id.button_align_center);
        mIndent = findViewById(R.id.button_indent);
        mOutdent = findViewById(R.id.button_outdent);
        mBlockquote = findViewById(R.id.action_blockquote);
        mStrikethrough = findViewById(R.id.action_strikethrough);
        mSuperscript = findViewById(R.id.action_superscript);
        mSubscript = findViewById(R.id.action_subscript);
        getViewMeasureHeight();
    }

    /**
     * 获取控件的高度
     */
    private void getViewMeasureHeight() {
        //获取像素密度
        float mDensity = getResources().getDisplayMetrics().density;
        //获取布局的高度
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        llColorView.measure(w, h);
        int height = llColorView.getMeasuredHeight();
        mFoldedViewMeasureHeight = (int) (mDensity * height + 0.5);
    }

    private void initClickListener() {
        mBold.setOnClickListener(this);
        mTextColor.setOnClickListener(this);
        mListOL.setOnClickListener(this);
        mListUL.setOnClickListener(this);
        mLean.setOnClickListener(this);
        mItalic.setOnClickListener(this);
        mAlignLeft.setOnClickListener(this);
        mAlignRight.setOnClickListener(this);
        mAlignCenter.setOnClickListener(this);
        mIndent.setOnClickListener(this);
        mOutdent.setOnClickListener(this);
        mBlockquote.setOnClickListener(this);
        mStrikethrough.setOnClickListener(this);
        mSuperscript.setOnClickListener(this);
        mSubscript.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_bold) {//字体加粗
            if (isClickBold) {
                mBold.setImageResource(R.mipmap.bold);
            } else {  //加粗
                mBold.setImageResource(R.mipmap.bold_);
            }
            isClickBold = !isClickBold;
            mEditor.setBold();
        } else if (id == R.id.button_text_color) {//设置字体颜色
            //如果动画正在执行,直接return,相当于点击无效了,不会出现当快速点击时,
            // 动画的执行和ImageButton的图标不一致的情况
            if (isAnimating) return;
            //如果动画没在执行,走到这一步就将isAnimating制为true , 防止这次动画还没有执行完毕的
            //情况下,又要执行一次动画,当动画执行完毕后会将isAnimating制为false,这样下次动画又能执行
            isAnimating = true;

            if (llColorView.getVisibility() == View.GONE) {
                //打开动画
                animateOpen(llColorView);
            } else {
                //关闭动画
                animateClose(llColorView);
            }
//        } else if (id == R.id.button_image) {//插入图片
//            //这里的功能需要根据需求实现，通过insertImage传入一个URL或者本地图片路径都可以，这里用户可以自己调用本地相
//            //或者拍照获取图片，传图本地图片路径，也可以将本地图片路径上传到服务器（自己的服务器或者免费的七牛服务器），
//            //返回在服务端的URL地址，将地址传如即可（我这里传了一张写死的图片URL，如果你插入的图片不现实，请检查你是否添加
//            // 网络请求权限<uses-permission android:name="android.permission.INTERNET" />）
//            mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
//                    "dachshund");
        }
        else if (id == R.id.button_list_ol) {
            if (isListOl) {
                mListOL.setImageResource(R.mipmap.list_ol);
            } else {
                mListOL.setImageResource(R.mipmap.list_ol_);
            }
            isListOl = !isListOl;
            mEditor.setNumbers();
        } else if (id == R.id.button_list_ul) {
            if (isListUL) {
                mListUL.setImageResource(R.mipmap.list_ul);
            } else {
                mListUL.setImageResource(R.mipmap.list_ul_);
            }
            isListUL = !isListUL;
            mEditor.setBullets();
        } else if (id == R.id.button_underline) {
            if (isTextLean) {
                mLean.setImageResource(R.mipmap.underline);
            } else {
                mLean.setImageResource(R.mipmap.underline_);
            }
            isTextLean = !isTextLean;
            mEditor.setUnderline();
        } else if (id == R.id.button_italic) {
            if (isItalic) {
                mItalic.setImageResource(R.mipmap.lean);
            } else {
                mItalic.setImageResource(R.mipmap.lean_);
            }
            isItalic = !isItalic;
            mEditor.setItalic();
        } else if (id == R.id.button_align_left) {
            if (isAlignLeft) {
                mAlignLeft.setImageResource(R.mipmap.align_left);
            } else {
                mAlignLeft.setImageResource(R.mipmap.align_left_);
            }
            isAlignLeft = !isAlignLeft;
            mEditor.setAlignLeft();
        } else if (id == R.id.button_align_right) {
            if (isAlignRight) {
                mAlignRight.setImageResource(R.mipmap.align_right);
            } else {
                mAlignRight.setImageResource(R.mipmap.align_right_);
            }
            isAlignRight = !isAlignRight;
            mEditor.setAlignRight();
        } else if (id == R.id.button_align_center) {
            if (isAlignCenter) {
                mAlignCenter.setImageResource(R.mipmap.align_center);
            } else {
                mAlignCenter.setImageResource(R.mipmap.align_center_);
            }
            isAlignCenter = !isAlignCenter;
            mEditor.setAlignCenter();
        } else if (id == R.id.button_indent) {
            if (isIndent) {
                mIndent.setImageResource(R.mipmap.indent);
            } else {
                mIndent.setImageResource(R.mipmap.indent_);
            }
            isIndent = !isIndent;
            mEditor.setIndent();
        } else if (id == R.id.button_outdent) {
            if (isOutdent) {
                mOutdent.setImageResource(R.mipmap.outdent);
            } else {
                mOutdent.setImageResource(R.mipmap.outdent_);
            }
            isOutdent = !isOutdent;
            mEditor.setOutdent();
        } else if (id == R.id.action_blockquote) {
            if (isBlockquote) {
                mBlockquote.setImageResource(R.mipmap.blockquote);
            } else {
                mBlockquote.setImageResource(R.mipmap.blockquote_);
            }
            isBlockquote = !isBlockquote;
            mEditor.setBlockquote();
        } else if (id == R.id.action_strikethrough) {
            if (isStrikethrough) {
                mStrikethrough.setImageResource(R.mipmap.strikethrough);
            } else {
                mStrikethrough.setImageResource(R.mipmap.strikethrough_);
            }
            isStrikethrough = !isStrikethrough;
            mEditor.setStrikeThrough();
        } else if (id == R.id.action_superscript) {
            if (isSuperscript) {
                mSuperscript.setImageResource(R.mipmap.superscript);
            } else {
                mSuperscript.setImageResource(R.mipmap.superscript_);
            }
            isSuperscript = !isSuperscript;
            mEditor.setSuperscript();
        } else if (id == R.id.action_subscript) {
            if (isSubscript) {
                mSubscript.setImageResource(R.mipmap.subscript);
            } else {
                mSubscript.setImageResource(R.mipmap.subscript_);
            }
            isSubscript = !isSubscript;
            mEditor.setSubscript();
        }
    }

    /**
     * 开启动画
     *
     * @param view 开启动画的view
     */
    private void animateOpen(LinearLayout view) {
        view.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(view, 0, mFoldedViewMeasureHeight);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }
        });
        animator.start();
    }

    /**
     * 关闭动画
     *
     * @param view 关闭动画的view
     */
    private void animateClose(final LinearLayout view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                isAnimating = false;
            }
        });
        animator.start();
    }


    /**
     * 创建动画
     *
     * @param view  开启和关闭动画的view
     * @param start view的高度
     * @param end   view的高度
     * @return ValueAnimator对象
     */
    private ValueAnimator createDropAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    public File  cratephotofile() throws IOException {//返回一个File类的文件
        String name=new SimpleDateFormat("YYYYMMdd_HHmmss").format(new Date());
//年月日小时分秒
        File stordir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //获得公共目录下的图片文件路径
        File image=File.createTempFile(name,".jpeg",stordir);
        //1：字首2：后缀3：在哪个目录下
        return  image;
    }
    private void initData(){
        token = userData.getToken();
    }
}
