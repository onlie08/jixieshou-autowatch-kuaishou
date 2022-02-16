package com.ch.adapter;


import androidx.fragment.app.Fragment;

public interface FragmentNavigatorAdapter {
    Fragment onCreateFragment(int var1);

    String getTag(int var1);

    int getCount();
}
