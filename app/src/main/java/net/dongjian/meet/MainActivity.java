package net.dongjian.meet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dongjian.framwork.base.BaseUIActivity;
import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.entity.Constants;
import com.dongjian.framwork.gson.TokenBean;
import com.dongjian.framwork.manager.DialogManager;
import com.dongjian.framwork.manager.HttpManager;
import com.dongjian.framwork.utils.LogUtils;
import com.dongjian.framwork.utils.SpUtils;
import com.dongjian.framwork.view.DialogView;
import com.google.gson.Gson;

import net.dongjian.meet.fragment.ChatFragment;
import net.dongjian.meet.fragment.MeFragment;
import net.dongjian.meet.fragment.SquareFragment;
import net.dongjian.meet.fragment.StarFragment;
import net.dongjian.meet.service.CloudService;
import net.dongjian.meet.ui.FirstUploadActivity;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Fragment的优化

 */
public class MainActivity extends BaseUIActivity implements View.OnClickListener {

    //星球
    private ImageView iv_star;
    private TextView tv_star;
    private LinearLayout ll_star;
    private StarFragment mStarFragment = null;
    private FragmentTransaction mStarTransaction = null;

    //广场
    private ImageView iv_square;
    private TextView tv_square;
    private LinearLayout ll_square;
    private SquareFragment mSquareFragment = null;
    private FragmentTransaction mSquareTransaction = null;

    //聊天
    private ImageView iv_chat;
    private TextView tv_chat;
    private LinearLayout ll_chat;
    private ChatFragment mChatFragment = null;
    private FragmentTransaction mChatTransaction = null;

    //我的
    private ImageView iv_me;
    private TextView tv_me;
    private LinearLayout ll_me;
    private MeFragment mMeFragment = null;
    private FragmentTransaction mMeTransaction = null;

    //在检查token的时候发现需要跳转到上传头像和昵称的地方时的回调
    public static final int UPLOAD_REQUEST_CODE = 1002;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        requestPermiss();
        initFragment();

        iv_star = (ImageView) findViewById(R.id.iv_star);
        tv_star = (TextView) findViewById(R.id.tv_star);
        ll_star = (LinearLayout) findViewById(R.id.ll_star);

        iv_square = (ImageView) findViewById(R.id.iv_square);
        tv_square = (TextView) findViewById(R.id.tv_square);
        ll_square = (LinearLayout) findViewById(R.id.ll_square);

        iv_chat = (ImageView) findViewById(R.id.iv_chat);
        tv_chat = (TextView) findViewById(R.id.tv_chat);
        ll_chat = (LinearLayout) findViewById(R.id.ll_chat);

        iv_me = (ImageView) findViewById(R.id.iv_me);
        tv_me = (TextView) findViewById(R.id.tv_me);
        ll_me = (LinearLayout) findViewById(R.id.ll_me);

        ll_star.setOnClickListener(this);
        ll_square.setOnClickListener(this);
        ll_chat.setOnClickListener(this);
        ll_me.setOnClickListener(this);

        //设置文本
        tv_star.setText(getString(R.string.text_main_star));
        tv_square.setText(getString(R.string.text_main_square));
        tv_chat.setText(getString(R.string.text_main_chat));
        tv_me.setText(getString(R.string.text_main_me));

        //切换到默认的选项卡--星球
        checkMainTab(0);

        checkToken();   //域名申请下来以后可以打开

    }

    /**
     * 检查token
     */
    private void checkToken() {
        //获取TOKEN 需要三个参数：1、用户ID  2、头像地址 3、昵称
        String token = SpUtils.getInstance().getString(Constants.SP_TOKEN,"");
        if(!TextUtils.isEmpty(token)){
            startCloudService();
        }else{
            //token为空，需要获取这三个参数:用户ID永远不变，不需要再次获取了
            String tokenPhoto = BmobManager.getmInstance().getUser().getTokenPhoto();
            String tokenName = BmobManager.getmInstance().getUser().getTokenNickName();
            if(!TextUtils.isEmpty(tokenPhoto) && !TextUtils.isEmpty(tokenName)){
                //创建token
                //LogUtils.e("checkToken 来这里的" );
                creatToken();
            }else{
                //tokenPhoto或tokenName为空，则证明用户尚未注册,则创建上传头像和昵称的提示框
                createUploadDialog();
            }
        }
    }

    /**
     * 创建上传提示框
     */
    private void createUploadDialog() {
        DialogView mUploadView = DialogManager.getInstance().initView(this,R.layout.dialog_first_upload);
        //注意：此时外部不能进行点击了
        mUploadView.setCancelable(false);
        //dialog里的那个跳转箭头--点击后进行跳转
        ImageView iv_go_upload = mUploadView.findViewById(R.id.iv_go_upload);
        iv_go_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转前，这个Dialog需要隐藏起来
                DialogManager.getInstance().hide(mUploadView);
                FirstUploadActivity.startActivity(MainActivity.this,UPLOAD_REQUEST_CODE);
            }
        });
        DialogManager.getInstance().show(mUploadView);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == UPLOAD_REQUEST_CODE){
                //说明头像和昵称都上传成功了，则重新去创建token
                checkToken();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 创建token
     * 1、再融云后台获取Token
     * 2、连接融云
     */
    private void creatToken() {
        HashMap<String,String> map = new HashMap<>();
        map.put("userId" , BmobManager.getmInstance().getUser().getObjectId());
        map.put("name" , BmobManager.getmInstance().getUser().getTokenNickName());
        map.put("portraitUri" , BmobManager.getmInstance().getUser().getTokenPhoto());

        //通过OkHTTP请求Token  -- 用到了RxJava

        disposable = Observable.create(new ObservableOnSubscribe<String>(){
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                //执行请求过程
                String json = HttpManager.getInstance().postCloudToken(map);
                emitter.onNext(json);
                emitter.onComplete();
            }
            //线程调度
        }).subscribeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        parsingCloudToken(s);
                    }
                });

    }

    /**
     * 启动云服务 -- 连接融云
     */
    private void startCloudService(){
        startService(new Intent(this, CloudService.class));
    }

    /**
     * 解析token
     * @param s
     */
    private void parsingCloudToken(String s) {
        //得到token实体类
        TokenBean tokenBean = new Gson().fromJson(s,TokenBean.class);
        if(tokenBean.getCode() == 200){
            if(!TextUtils.isEmpty(tokenBean.getToken())){
                //保存token
                SpUtils.getInstance().putString(Constants.SP_TOKEN,tokenBean.getToken());
                //连接融云
                startCloudService();
            }
        }
    }

    /**
     * 初始化四个Fragment:星球、广场、聊天、我的
     */
    private void initFragment() {
        if (mStarFragment == null) {
            mStarFragment = new StarFragment();
            mStarTransaction = getSupportFragmentManager().beginTransaction();
            mStarTransaction.add(R.id.mMainLayout, mStarFragment);
            mStarTransaction.commit();
        }
        if (mSquareFragment == null) {
            mSquareFragment = new SquareFragment();
            mSquareTransaction = getSupportFragmentManager().beginTransaction();
            mSquareTransaction.add(R.id.mMainLayout, mSquareFragment);
            mSquareTransaction.commit();
        }
        if (mChatFragment == null) {
            mChatFragment = new ChatFragment();
            mChatTransaction = getSupportFragmentManager().beginTransaction();
            mChatTransaction.add(R.id.mMainLayout, mChatFragment);
            mChatTransaction.commit();
        }
        if (mMeFragment == null) {
            mMeFragment = new MeFragment();
            mMeTransaction = getSupportFragmentManager().beginTransaction();
            mMeTransaction.add(R.id.mMainLayout, mMeFragment);
            mMeTransaction.commit();
        }
    }

    /**
     * 显示Fragment----要隐藏所有fragment
     * @param fragment
     */
    private void showFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //在显示之前，要先把所有fragment隐藏起来
            hideAllFragment(transaction);
            transaction.show(fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 隐藏所有的Fragment
     * @param transaction
     */
    private void hideAllFragment(FragmentTransaction transaction) {
        if(mStarFragment != null){
            transaction.hide(mStarFragment);
        }
        if(mChatFragment != null){
            transaction.hide(mChatFragment);
        }
        if(mSquareFragment != null){
            transaction.hide(mSquareFragment);
        }
        if(mMeFragment != null){
            transaction.hide(mMeFragment);
        }
    }

    /**
     * 【当应用的内存紧张的时候，系统会回收掉Fragment对象
     * 再一次进入的时候会重新创建Fragment，此Fragment非原来对象，我们无法控制，导致重叠】
     *
     * 防止重叠：让原来的fragmnet对象指向现在的同类fragment即可
     * @param fragment
     */
    @Override
    public void onAttachFragment(Fragment fragment) {
        if (mStarFragment != null && fragment instanceof StarFragment) {
            mStarFragment = (StarFragment) fragment;
        }
        if (mSquareFragment != null && fragment instanceof SquareFragment) {
            mSquareFragment = (SquareFragment) fragment;
        }
        if (mChatFragment != null && fragment instanceof ChatFragment) {
            mChatFragment = (ChatFragment) fragment;
        }
        if (mMeFragment != null && fragment instanceof MeFragment) {
            mMeFragment = (MeFragment) fragment;
        }
    }


    /**
     * 切换主页选项卡
     * @param index 从0开始，按顺序分别为星球-广场-聊天-我的
     */
    private void checkMainTab(int index) {
        switch (index) {
            case 0:
                showFragment(mStarFragment);

                iv_star.setImageResource(R.drawable.img_star_p);
                iv_square.setImageResource(R.drawable.img_square);
                iv_chat.setImageResource(R.drawable.img_chat);
                iv_me.setImageResource(R.drawable.img_me);

                tv_star.setTextColor(getResources().getColor(R.color.colorAccent));
                tv_square.setTextColor(Color.BLACK);
                tv_chat.setTextColor(Color.BLACK);
                tv_me.setTextColor(Color.BLACK);

                break;
            case 1:
                showFragment(mSquareFragment);

                iv_star.setImageResource(R.drawable.img_star);
                iv_square.setImageResource(R.drawable.img_square_p);
                iv_chat.setImageResource(R.drawable.img_chat);
                iv_me.setImageResource(R.drawable.img_me);

                tv_star.setTextColor(Color.BLACK);
                tv_square.setTextColor(getResources().getColor(R.color.colorAccent));
                tv_chat.setTextColor(Color.BLACK);
                tv_me.setTextColor(Color.BLACK);

                break;
            case 2:
                showFragment(mChatFragment);

                iv_star.setImageResource(R.drawable.img_star);
                iv_square.setImageResource(R.drawable.img_square);
                iv_chat.setImageResource(R.drawable.img_chat_p);
                iv_me.setImageResource(R.drawable.img_me);

                tv_star.setTextColor(Color.BLACK);
                tv_square.setTextColor(Color.BLACK);
                tv_chat.setTextColor(getResources().getColor(R.color.colorAccent));
                tv_me.setTextColor(Color.BLACK);

                break;
            case 3:
                showFragment(mMeFragment);

                iv_star.setImageResource(R.drawable.img_star);
                iv_square.setImageResource(R.drawable.img_square);
                iv_chat.setImageResource(R.drawable.img_chat);
                iv_me.setImageResource(R.drawable.img_me_p);

                tv_star.setTextColor(Color.BLACK);
                tv_square.setTextColor(Color.BLACK);
                tv_chat.setTextColor(Color.BLACK);
                tv_me.setTextColor(getResources().getColor(R.color.colorAccent));

                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_star:
                checkMainTab(0);
                break;

            case R.id.ll_square:
                checkMainTab(1);
                break;
            case R.id.ll_chat:
                checkMainTab(2);
                break;
            case R.id.ll_me:
                checkMainTab(3);
                break;
        }
    }

    /**
     * 请求权限
     */
    private void requestPermiss() {
        //危险权限
        request(new OnPermissionsResult() {
            @Override
            public void OnSuccess() {

            }

            @Override
            public void OnFail(List<String> noPermissions) {
                LogUtils.i("noPermissions:" + noPermissions.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable.isDisposed()){
            disposable.dispose();
        }
    }
}