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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reading.Bean.LoadFileVo;
import com.example.reading.Bean.Post;
import com.example.reading.Bean.User;
import com.example.reading.MainActivity;
import com.example.reading.R;
import com.example.reading.ToolClass.BaseActivity;
import com.example.reading.ToolClass.ColorPickerView;
/*import com.example.reading.adapter.LoadPicAdapter;*/
import com.example.reading.adapter.LoadPicAdapter;
import com.example.reading.constant.RequestUrl;
import com.example.reading.util.FileCacheUtil;
import com.example.reading.util.FilePathUtil;
import com.example.reading.util.UserUtil;
import com.example.reading.web.BaseCallBack;
import com.example.reading.web.StandardRequestMangaer;
import com.weavey.loading.lib.LoadingLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.reading.MainApplication.getContext;

public class addPost extends BaseActivity implements View.OnClickListener {
    public static final int TAKE_PHOTO=2;
    private static final String TAG = "addPost";
    public static final int SHOW_TOAST=3;
    public static final int SET_OK = 1;
    private Uri takePhotoUri;
    private List<String> imageList=new ArrayList<>();
    private AlertDialog alertDialog3;
    private LoadPicAdapter adapter;
    private LinearLayout loadLayout;
    private TextView loadTextView;
    RecyclerView rvPic;
    private Button sendButton;
    TextView tvNum;
    private StringBuilder stringBuilder=new StringBuilder();
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
    private  File outputImage;
    private File file;
    private User userData;
    LoadingLayout loadingLayout;
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
        initClickListener();
        checkHistory();
    }
    //initAdapter方法
    private void initAdapter() {
        imageList.add(null);
        adapter=new LoadPicAdapter(this,imageList);
        rvPic.setAdapter(adapter);
        rvPic.setLayoutManager(new GridLayoutManager(this, 3));
        adapter.setListener(new LoadPicAdapter.OnItemClickListener() {
            @Override
            public void click(View view, int positon) {
                if (imageList.size() > 9) {
                    Toast.makeText(addPost.this, "一次最多上传9张图片！", Toast.LENGTH_SHORT).show();
                } else {
                    showTypeDialog();
                }
            }
            @Override
            public void del(View view) {
                tvNum.setText((imageList.size() - 1) + "/9");
            }
        });
    }
    /**
     * 初始化View
     */
    private void initView() {
        //返回
        ImageView back = findViewById(R.id.title_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=mEditor.getHtml();
                String imgurl=stringBuilder.toString();
                if (TextUtils.isEmpty(content)&&TextUtils.isEmpty(imgurl)){
                    finish();
                    return;
                }
                // super.onBackPressed();//注释掉这行,back键不退出activity
                AlertDialog.Builder dialog = new AlertDialog.Builder(addPost.this);
                dialog.setTitle("提醒");
                dialog.setMessage("是否保存");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Post post=new Post();
                        post.setContent(content);
                        post.setImgurl(imgurl);
                        FileCacheUtil.setCache(post,addPost.this,"POST_INFO",0);
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
                alertDialog3 = dialog.create();
                alertDialog3.show();
                //设置AlertDialog长度
                alertDialog3.getWindow().setLayout(950,550);
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
                String content=mEditor.getHtml();
                if (TextUtils.isEmpty(content)){
                    Toast.makeText(addPost.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String imgurl=stringBuilder.substring(0,stringBuilder.length()-1);
                uploadPost(content,imgurl);
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
        alertDialog3 = dialog.create();
        alertDialog3.show();
        //设置AlertDialog长度
        alertDialog3.getWindow().setLayout(950,550);
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


    public void selectPic(int size){
        MultiImageSelector.create()
                .showCamera(false)
                .count(size)
                .multi()
                .start(this,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            String numStr=null;
            switch (requestCode){
                case 1:
                    imageList.remove(null);
                    List<String> nowImageList=data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    imageList.addAll(nowImageList);
                    uploadImage(nowImageList);
                    Log.d(TAG, "onActivityResult: 值"+imageList);
                    adapter.notifyDataSetChanged();
                    numStr=String.valueOf(imageList.size())+"/9";
                    tvNum.setText(numStr);
                    if (imageList.size()<9){
                        Log.i(TAG, "onActivityResult: 添加新的选择");
                        imageList.add(null);
                    }
                    break;
                case 2:
                    String path= outputImage.getPath();
                    Log.i(TAG, "onActivityResult: path="+path);
                    imageList.remove(null);
                    imageList.add(path);
                    uploadImage(Arrays.asList(path));
                    adapter.notifyDataSetChanged();
                    numStr=String.valueOf(imageList.size())+"/9";
                    tvNum.setText(numStr);
                    if (imageList.size()<9){
                        Log.i(TAG, "onActivityResult: 添加新的选择");
                        imageList.add(null);
                    }
                    break;
            }
        }
    }
    private void uploadImage(List<String>list) {
        Map<String, String> params = UserUtil.createUserMap();
        for (String path : list) {
            File file=new File(path);
            StandardRequestMangaer.getInstance().postImage(RequestUrl.UPLOAD_IMG, new BaseCallBack<String>() {

                @Override
                protected void OnRequestBefore(Request request) {
                    /*loadLayout.setVisibility(View.VISIBLE);*/
                }

                @Override
                protected void onFailure(Call call) {

                }

                @Override
                protected void onSuccess(Call call, Response response, String s) {
                    Log.i(TAG, "onSuccess: 图片路径:"+s);
                    Toast.makeText(addPost.this, "上传成功！"+s, Toast.LENGTH_SHORT).show();
                    stringBuilder.append(s+",");
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
            }, "file",file,params);
        }
    }
    private void uploadPost(String content,String imgurl){
        Map<String,String> map=UserUtil.createUserMap();
        map.put("content",content);
        map.put("imgurl",imgurl);
        StandardRequestMangaer.getInstance()
                .post(RequestUrl.ADD_POST, new BaseCallBack<String>(){
                    @Override
                    protected void OnRequestBefore(Request request) {
                        Log.i(TAG, "OnRequestBefore: 上传帖子");
                    }

                    @Override
                    protected void onFailure(Call call) {
                        Toast.makeText(addPost.this, "上传帖子失败了", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onSuccess(Call call, Response response, String s) {
                        Toast.makeText(addPost.this, "上传帖子成功！", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent();
                        intent.putExtra("postId",Integer.valueOf(s));
                        intent.putExtra("content",content);
                        intent.putExtra("imgurl",imgurl);
                        setResult(1,intent);
                        finish();
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
                },map);
    }
    private void checkHistory(){
        Post post=FileCacheUtil.getCache(this,"POST_INFO",0,Post.class);
        if (post==null){
            return;
        }
        File file=new File(Environment.getExternalStorageDirectory().toString() + File.separator + "POST_INFO");
        file.delete();
        String content=post.getContent();
        String imgurl=post.getImgurl();
        if (!TextUtils.isEmpty(content)){
            mEditor.setHtml(content);
        }
        if (!TextUtils.isEmpty(imgurl)){
            stringBuilder.append(imgurl);
            String[] strings=imgurl.substring(0,imgurl.length()-1).split(",");
            imageList.remove(null);
            for (String string:strings){
                imageList.add(string);
            }
            String numStr=String.valueOf(imageList.size())+"/9";
            tvNum.setText(numStr);
            imageList.add(null);
            adapter.notifyDataSetChanged();
        }
    }
    private void showTypeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(addPost.this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(addPost.this, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(addPost.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(addPost.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    selectPic(10-imageList.size());  //调用加载图片方法
                }
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                outputImage =new File(getContext().getExternalCacheDir(),"output_image.jpg");
                try {
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    takePhotoUri= FileProvider.getUriForFile(getContext(),
                            "com.example.cameraalbumtest.fileprovider",outputImage);
                }else{
                    takePhotoUri=Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,takePhotoUri);
                startActivityForResult(intent,TAKE_PHOTO);
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }
}
