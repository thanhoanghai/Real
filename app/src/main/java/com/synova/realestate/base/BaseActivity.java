
package com.synova.realestate.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.synova.realestate.R;
import com.synova.realestate.models.AdsInfoResponseEnt;
import com.synova.realestate.models.House;
import com.synova.realestate.utils.Util;

import java.util.Stack;

/**
 * Created by ducth on 04/06/2015.
 */
public class BaseActivity extends AppCompatActivity {

    protected Stack<BaseFragment> fragmentStacks = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void pushFragment(BaseFragment fragment,
            Constants.TransitionType transitionType,
            boolean addToStack) {
        Util.hideKeyboard(this);

        fragment.transitionInType = transitionType;

        if (addToStack) {
            fragmentStacks.push(fragment);
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(transitionType.transitionInResId,
                transitionType.transitionOutResId);
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    public void popFragment() {
        Util.hideKeyboard(this);

        /* Select the second last fragment in stack */
        BaseFragment fragment = fragmentStacks.elementAt(
                fragmentStacks.size() - 2);

        /* Remove current fragment from manually managed stack */
        BaseFragment currentFragment = fragmentStacks.pop();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        Constants.TransitionType transitionType = reverseTransitionType(currentFragment.transitionInType);
        ft.setCustomAnimations(transitionType.transitionInResId,
                transitionType.transitionOutResId);
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    protected Constants.TransitionType reverseTransitionType(Constants.TransitionType transitionType) {
        switch (transitionType) {
            case NONE:
                break;
            case SLIDE_IN_RIGHT_TO_LEFT:
                transitionType = Constants.TransitionType.SLIDE_IN_LEFT_TO_RIGHT;
                break;
            case SLIDE_IN_LEFT_TO_RIGHT:
                transitionType = Constants.TransitionType.SLIDE_IN_RIGHT_TO_LEFT;
                break;
            case SLIDE_IN_BOTTOM:
                transitionType = Constants.TransitionType.SLIDE_OUT_BOTTOM;
                break;
            case SLIDE_OUT_BOTTOM:
                transitionType = Constants.TransitionType.SLIDE_IN_BOTTOM;
                break;
        }
        return transitionType;
    }

    private BaseFragment getCurrentFragment() {
        return fragmentStacks.size() > 0 ? fragmentStacks.peek() : null;
    }

    @Override
    public void onBackPressed() {
        BaseFragment currentFragment = getCurrentFragment();
        if (!currentFragment.onBackPressed()) {
            if (fragmentStacks.size() > 1) {
                popFragment();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void showDetailActivity(AdsInfoResponseEnt house) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("house", new Gson().toJson(house));
        startActivity(intent);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
