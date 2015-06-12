
package com.synova.realestate.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.synova.realestate.utils.Util;

import java.util.Stack;

/**
 * Created by ducth on 3/24/15.
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getName();

    private Stack<BaseFragment> childFragments = null;

    protected BaseActivity activity;
    public View rootView;
    public Constants.TransitionType transitionInType;
    private boolean blockBackButtonPressed = false;

    public TextView tvTitle;
    public Button btnLeft;
    public Button btnRight;

    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        if (rootView == null) {
            rootView = onFirstTimeCreateView(inflater, container, savedInstanceState);
        }
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                        viewDidLoad();
                    }
                });
        return rootView;
    }

    /**
     * Should use this method to init fragments views instead of onCreateView
     */
    protected abstract View onFirstTimeCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState);

    /**
     * Views have been layout. Use this method to setup views.
     */
    protected void viewDidLoad() {
    }

    public void pushChildFragment(BaseFragment fragment,
            Constants.TransitionType transitionType,
            boolean addToStack) {
        if (childFragments == null) {
            childFragments = new Stack<>();
        }

        Util.hideKeyboard(activity);

        fragment.transitionInType = transitionType;

        if (addToStack) {
            childFragments.push(fragment);
        }

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(transitionType.transitionInResId,
                transitionType.transitionOutResId);
//        ft.replace(R.id.child_container, fragment);
        ft.commit();
    }

    public void popChildFragment() {
        Util.hideKeyboard(activity);

        /* Select the second last fragment in stack */
        BaseFragment fragment = childFragments.elementAt(
                childFragments.size() - 2);

        /* Remove current fragment from manually managed stack */
        BaseFragment currentFragment = childFragments.pop();

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        Constants.TransitionType transitionType = reverseTransitionType(currentFragment.transitionInType);
        ft.setCustomAnimations(transitionType.transitionInResId,
                transitionType.transitionOutResId);
//        ft.replace(R.id.child_container, fragment);
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

    @Override
    public final Animation onCreateAnimation(int transit, final boolean enter,
            int nextAnim) {
        if (nextAnim != 0) {
            Animation anim = AnimationUtils.loadAnimation(activity,
                    nextAnim);

            anim.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation animation) {
                    blockBackButtonPressed = true;
                }

                public void onAnimationEnd(Animation animation) {
                    if (enter) {
                        blockBackButtonPressed = false;
                        onFragmentTransitionEnd();
                    }
                }

                public void onAnimationRepeat(Animation animation) {
                }

            });
            return anim;
        }
        if (enter) {
            blockBackButtonPressed = false;
        }
        return null;
    }

    protected void onFragmentTransitionEnd() {
    }

    private BaseFragment getCurrentFragment() {
        return childFragments != null && childFragments.size() > 0 ? childFragments.peek() : null;
    }

    public boolean onBackPressed() {
        BaseFragment currentChildFragment = getCurrentFragment();
        if (currentChildFragment == null) {
            return blockBackButtonPressed;
        }
        return currentChildFragment.onBackPressed();
    }

}
