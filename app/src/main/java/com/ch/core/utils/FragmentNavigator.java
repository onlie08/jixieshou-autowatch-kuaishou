package com.ch.core.utils;

import android.app.Activity;
import android.os.Bundle;

import com.ch.adapter.FragmentNavigatorAdapter;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentNavigator {
    private static final String EXTRA_CURRENT_POSITION = "extra_current_position";
    private FragmentManager mFragmentManager;
    private FragmentNavigatorAdapter mAdapter;
    @IdRes
    private int mContainerViewId;
    private int mCurrentPosition = -1;
    private int mDefaultPosition;


    public FragmentNavigator(FragmentManager fragmentManager, FragmentNavigatorAdapter adapter, @IdRes int containerViewId) {
        this.mFragmentManager = fragmentManager;
        this.mAdapter = adapter;
        this.mContainerViewId = containerViewId;
    }

    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.mCurrentPosition = savedInstanceState.getInt("extra_current_position", this.mDefaultPosition);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("extra_current_position", this.mCurrentPosition);
    }

    public void showFragment(int position, Activity activity) {
        this.showFragment(position, false, activity);
    }

    public void showFragment(int position, boolean reset, Activity activity) {
        this.showFragment(position, reset, false, activity);
    }

    public void showFragment(int position, boolean reset, boolean allowingStateLoss, Activity activity) {
        this.mCurrentPosition = position;
        FragmentTransaction transaction = this.mFragmentManager.beginTransaction();
        int count = this.mAdapter.getCount();

        for (int i = 0; i < count; i++) {
            if (position == i) {
                if (reset) {
                    remove(position, transaction);
                    add(position, transaction);
                } else {
                    show(i, transaction);
                }
            } else {
                hide(i, transaction);
            }
        }

        if (!activity.isFinishing()) {
            if (allowingStateLoss) {
                transaction.commitAllowingStateLoss();
            } else {
                transaction.commit();
            }

        }

    }

    public void resetFragments() {
        this.resetFragments(this.mCurrentPosition);
    }

    public void resetFragments(int position) {
        this.resetFragments(position, false);
    }

    public void resetFragments(int position, boolean allowingStateLoss) {
        this.mCurrentPosition = position;
        FragmentTransaction transaction = this.mFragmentManager.beginTransaction();
        this.removeAll(transaction);
        this.add(position, transaction);
        if (allowingStateLoss) {
            transaction.commitAllowingStateLoss();
        } else {
            transaction.commit();
        }

    }

    public void removeAllFragment() {
        this.removeAllFragment(false);
    }

    public void removeAllFragment(boolean allowingStateLoss) {
        FragmentTransaction transaction = this.mFragmentManager.beginTransaction();
        this.removeAll(transaction);
        if (allowingStateLoss) {
            transaction.commitAllowingStateLoss();
        } else {
            transaction.commit();
        }

    }

    public int getCurrentPosition() {
        return this.mCurrentPosition;
    }

    public Fragment getCurrentFragment() {
        return this.getFragment(this.mCurrentPosition);
    }

    public Fragment getFragment(int position) {
        String tag = this.mAdapter.getTag(position);
        return this.mFragmentManager.findFragmentByTag(tag);
    }

    private void show(int tagPage, FragmentTransaction ft) {
        String tag = this.mAdapter.getTag(tagPage);
        Fragment f = this.mFragmentManager.findFragmentByTag(tag);
        if (f == null) {
            f = this.mAdapter.onCreateFragment(tagPage);
            ft.add(this.mContainerViewId, f, tag);
        } else {
            if (!f.isAdded() && null != mFragmentManager.findFragmentByTag(tag)) {
                ft.remove(f).commit();
                ft.add(mContainerViewId, f, tag);
            }
            ft.show(f);
        }
    }

    private void hide(int position, FragmentTransaction transaction) {
        String tag = this.mAdapter.getTag(position);
        Fragment fragment = this.mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            transaction.hide(fragment);
        }

    }

    private Fragment add(int position, FragmentTransaction transaction) {
        mFragmentManager.executePendingTransactions();
        Fragment fragment = this.mAdapter.onCreateFragment(position);
        String tag = this.mAdapter.getTag(position);
        transaction.add(this.mContainerViewId, fragment, tag);
        transaction.addToBackStack(null);
        return fragment;
    }

    private void removeAll(FragmentTransaction transaction) {
        int count = this.mAdapter.getCount();
        for (int i = 0; i < count; ++i) {
            this.remove(i, transaction);
        }

    }

    private void remove(int position, FragmentTransaction transaction) {
        String tag = this.mAdapter.getTag(position);
        Fragment fragment = this.mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            transaction.remove(fragment);
        }

    }

    public void setDefaultPosition(int defaultPosition) {
        this.mDefaultPosition = defaultPosition;
        if (this.mCurrentPosition == -1) {
            this.mCurrentPosition = defaultPosition;
        }
    }
}

