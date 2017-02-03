package com.whn.whn.whn_qqmusic.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.whn.whn.whn_qqmusic.R;
import com.whn.whn.whn_qqmusic.factory.FragmentFactory;
import com.whn.whn.whn_qqmusic.fragment.BaseFragment;
import com.whn.whn.whn_qqmusic.utils.DisplayUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.iv_menu_main)
    ImageView ivMenuMain;
    @InjectView(R.id.rb_me_main)
    RadioButton rbMeMain;
    @InjectView(R.id.rb_music_main)
    RadioButton rbMusicMain;
    @InjectView(R.id.rb_find_main)
    RadioButton rbFindMain;
    @InjectView(R.id.rg_main)
    RadioGroup rgMain;
    @InjectView(R.id.iv_find_main)
    ImageView ivFindMain;
    @InjectView(R.id.activity_main)
    LinearLayout activityMain;
    @InjectView(R.id.fl_fragment_main)
    FrameLayout ivFragmentMain;
    private int checkedRadioButtonId;
    private ArrayList<RadioButton> buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        SystemBarTint();//设置沉浸式状态栏的颜色
        initView();//初始化view


        //初始化选中"我的"
//        checkedRadioButtonId = R.id.rb_me_main;
//        BaseFragment fragment = FragmentFactory.createFragment(checkedRadioButtonId);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fl_fragment_main, fragment);
//        transaction.commit();
        createFragment(R.id.rb_me_main);

        //相应的改变GroupButton的Button状态,颜色,文字大小,文字style


        //TODO--------------

    }

    /**
     * 初始化view
     */
    private void initView() {
        rbFindMain.setOnClickListener(this);
        rbMeMain.setOnClickListener(this);
        rbMusicMain.setOnClickListener(this);

        buttons = new ArrayList<>();
        buttons.add(rbFindMain);
        buttons.add(rbMeMain);
        buttons.add(rbMusicMain);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_me_main:
                changedShow(rbMeMain);
                createFragment(R.id.rb_me_main);
                break;

            case R.id.rb_music_main:
                changedShow(rbMusicMain);
                createFragment(R.id.rb_music_main);
                break;

            case R.id.rb_find_main:
                changedShow(rbFindMain);
                createFragment(R.id.rb_find_main);
                break;
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
