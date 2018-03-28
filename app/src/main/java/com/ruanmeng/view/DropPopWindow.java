package com.ruanmeng.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.ruanmeng.north_town.R;
import com.ruanmeng.utils.AnimationHelper;
import com.ruanmeng.utils.CommonUtil;

public abstract class DropPopWindow extends PopupWindow {

    private View window, mContainer, indicator;
    private Animation animationIn, animationOut;
    private boolean isDismiss = false;

    public DropPopWindow(Context context, @LayoutRes int resource) {
        this(context, resource, null);
    }

    public DropPopWindow(Context context, @LayoutRes int resource, View indicator) {
        this.indicator = indicator;

        window = LayoutInflater.from(context).inflate(resource, null);
        setContentView(window);
        setWidth(CommonUtil.getScreenWidth(context));
        setHeight(CommonUtil.getScreenHeight(context));
        setAnimationStyle(R.style.WindowStyle);
        setFocusable(true);
        setOutsideTouchable(true);
        update();
        setBackgroundDrawable(new ColorDrawable(Color.argb(123, 0, 0, 0)));

        animationIn = AnimationUtils.loadAnimation(context, R.anim.pop_anim_show);
        animationOut = AnimationUtils.loadAnimation(context, R.anim.pop_anim_dismiss);
        initView();
        afterInitView(window);
    }

    private void initView() {
        mContainer = window.findViewById(R.id.pop_container);

        mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        window.findViewById(R.id.pop_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public abstract void afterInitView(View view);

    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }

        super.showAsDropDown(anchor);
        isDismiss = false;
        mContainer.startAnimation(animationIn);
        if (indicator != null)
            AnimationHelper.startRotateAnimator(indicator, 0f, 180f);
    }

    public void dismiss() {
        if (!isDismiss) {
            isDismiss = true;
            mContainer.startAnimation(animationOut);
            dismiss();
            animationOut.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation animation) {
                    if (indicator != null) AnimationHelper.startRotateAnimator(indicator, 180f, 0f);
                }

                @SuppressLint("ObsoleteSdkInt")
                public void onAnimationEnd(Animation animation) {
                    DropPopWindow.this.isDismiss = false;

                    if (Build.VERSION.SDK_INT <= 16) DropPopWindow.this.dismiss4Pop();
                    else DropPopWindow.super.dismiss();
                }

                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    private void dismiss4Pop() {
        (new Handler()).post(new Runnable() {
            public void run() {
                DropPopWindow.super.dismiss();
            }
        });
    }
}
