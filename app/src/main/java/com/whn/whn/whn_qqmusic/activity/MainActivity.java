package com.whn.whn.whn_qqmusic.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.whn.whn.whn_qqmusic.R;
import com.whn.whn.whn_qqmusic.factory.FragmentFactory;
import com.whn.whn.whn_qqmusic.fragment.BaseFragment;
import com.whn.whn.whn_qqmusic.fragment.LocalFragment;
import com.whn.whn.whn_qqmusic.utils.DisplayUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.iv_menu_main)
    ImageView ivMenuMain;
    @InjectView(R.id.rb_me_main)
    RadioButton rbMeMain;
    //    @InjectView(R.id.rb_music_main)
//    RadioButton rbMusicMain;
//    @InjectView(R.id.rb_find_main)
//    RadioButton rbFindMain;
    @InjectView(R.id.rg_main)
    RadioGroup rgMain;
    @InjectView(R.id.activity_main)
    LinearLayout activityMain;
    @InjectView(R.id.fl_fragment_main)
    FrameLayout ivFragmentMain;
    @InjectView(R.id.bt_find_main)
    Button btFindMain;
    private int checkedRadioButtonId;
    private ArrayList<RadioButton> buttons;
    private static boolean isExit = false;
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        SystemBarTint();//设置沉浸式状态栏的颜色
        initView();//初始化view

        createFragment(R.id.rb_me_main);
    }

    /**
     * 初始化view
     */
    private void initView() {
        rbMeMain.setOnClickListener(this);
//        rbFindMain.setOnClickListener(this);
//        rbMusicMain.setOnClickListener(this);
        btFindMain.setOnClickListener(this);
        ivMenuMain.setOnClickListener(this);

        buttons = new ArrayList<>();
        buttons.add(rbMeMain);
//        buttons.add(rbFindMain);
//        buttons.add(rbMusicMain);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_me_main:
                changedShow(rbMeMain);
                createFragment(R.id.rb_me_main);
                break;

//            case R.id.rb_music_main:
//                changedShow(rbMusicMain);
//                createFragment(R.id.rb_music_main);
//                break;
//
//            case R.id.rb_find_main:
//                changedShow(rbFindMain);
//                createFragment(R.id.rb_find_main);
//                break;

            case R.id.bt_find_main:
                Toast.makeText(this, "刷新列表", Toast.LENGTH_SHORT).show();
                LocalFragment fragment = (LocalFragment) getSupportFragmentManager().findFragmentById(R.id.fl_fragment_main);
                fragment.initData();
                fragment.refreshData();
                break;

            case R.id.iv_menu_main:
//                Toast.makeText(this, "退出", Toast.LENGTH_SHORT).show();
                exit();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            this.finish();
        }
    }


    /**
     * 创建fragment
     */
    private void createFragment(int id) {
        BaseFragment fragment = FragmentFactory.createFragment(id);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_fragment_main, fragment);
        transaction.commit();
    }


    /**
     * 改变文字的状态
     */
    private void changedShow(RadioButton rb) {
        for (RadioButton button : buttons) {
            button.setTextColor(getResources().getColor(R.color.textColorNormal));
            button.setTextSize(DisplayUtils.px2sp(this, getResources().getDimension(R.dimen.textSizeNormal)));
        }
        rb.setTextColor(getResources().getColor(R.color.textColorSelected));
        rb.setTextSize(DisplayUtils.px2sp(this, getResources().getDimension(R.dimen.textSizeSelected)));
    }

    /**
     * 设置沉浸式状态栏的颜色
     */
    private void SystemBarTint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // 激活状态栏
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint 激活导航栏
            tintManager.setNavigationBarTintEnabled(true);
            //设置系统栏设置颜色
            //tintManager.setTintColor(R.color.red);
            //给状态栏设置颜色
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
            //Apply the specified drawable or color resource to the system navigation bar.
            //给导航栏设置资源
            tintManager.setNavigationBarTintResource(R.color.colorPrimary);
        }
    }

}
