package com.ch.adapter;

import java.util.List;

import androidx.fragment.app.Fragment;

public class FragmentAdapter implements FragmentNavigatorAdapter {


    List<Fragment> fragments;

    public FragmentAdapter(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    @Override
    public Fragment onCreateFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public String getTag(int position) {
        // an simple unique tag
        return fragments.get(position).getClass().getName();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}