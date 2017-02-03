package com.whn.whn.whn_qqmusic.factory;

import com.whn.whn.whn_qqmusic.R;
import com.whn.whn.whn_qqmusic.fragment.BaseFragment;
import com.whn.whn.whn_qqmusic.fragment.FindFragment;
import com.whn.whn.whn_qqmusic.fragment.LocalFragment;
import com.whn.whn.whn_qqmusic.fragment.MusicFragment;

import java.util.HashMap;

public class FragmentFactory {
    //创建一个hashmap,用于存储创建过得对象
    public static HashMap<Integer,BaseFragment> hashMap = new HashMap<>();
    public static BaseFragment createFragment(int id) {
        BaseFragment fragment = null;
        fragment = hashMap.get(id);
        if(fragment == null){
            switch (id){
                case R.id.rb_me_main:
                    fragment = new LocalFragment();
                    break;
                case R.id.rb_music_main:
                    fragment = new MusicFragment();
                    break;
                case R.id.rb_find_main:
                    fragment = new FindFragment();
                    break;
            }
            hashMap.put(id,fragment);
        }
        return fragment;
    }
}
