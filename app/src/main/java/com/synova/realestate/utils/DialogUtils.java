
package com.synova.realestate.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.nineoldandroids.view.ViewHelper;
import com.synova.realestate.R;
import com.synova.realestate.adapters.RadioButtonAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducth on 3/25/15.
 */
public class DialogUtils {

    private static ArrayList<Dialog> dialogs = new ArrayList<>();
    private static Spring spring = SpringSystem.create().createSpring();

    // public static void showOpenLocationSettingDialog(final Context context) {
    // showOKCancelDialog(context, context.getString(R.string.notice),
    // context.getString(R.string.location_service_unenabled), new View.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // Util.openLocationSetting(context);
    // }
    // }, null);
    // }
    //
    // public static Dialog showOKCancelDialog(Context context, String title,
    // String message, final View.OnClickListener onBtnOKClicked,
    // final View.OnClickListener onBtnCancelClicked) {
    // final Dialog dialog = createTitleMessageDialogAndSave(context,
    // R.layout.dialog_ok_cancel, false, title, message);
    // Button btnOK = (Button) dialog.findViewById(R.id.ok_cancel_btnOK);
    // btnOK.setOnClickListener(new View.OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // hideDialog(dialog);
    // if (onBtnOKClicked != null) {
    // onBtnOKClicked.onClick(v);
    // }
    // }
    // });
    // Button btnCancel = (Button) dialog
    // .findViewById(R.id.ok_cancel_btnCancel);
    // btnCancel.setOnClickListener(new View.OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // hideDialog(dialog);
    // if (onBtnCancelClicked != null) {
    // onBtnCancelClicked.onClick(v);
    // }
    // }
    // });
    //
    // return dialog;
    // }
    //
    // public static Dialog showOkDialog(Context context, String title,
    // String message, final View.OnClickListener onBtnOKClicked) {
    // final Dialog dialog = createTitleMessageDialogAndSave(context,
    // R.layout.dialog_ok, false, title, message);
    // Button btnOK = (Button) dialog.findViewById(R.id.ok_btnOK);
    // btnOK.setOnClickListener(new View.OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // hideDialog(dialog);
    // if (onBtnOKClicked != null) {
    // onBtnOKClicked.onClick(v);
    // }
    // }
    // });
    // return dialog;
    // }

    public static void hideAllDialogs() {
        for (Dialog dialog : dialogs) {
            dialog.dismiss();
        }
    }

    private static void hideDialog(final Dialog dialog) {
        dialog.dismiss();
        dialogs.remove(dialog);
    }

    // private static Dialog createTitleMessageDialogAndSave(Context context,
    // int layoutId, boolean isCancelable, String title, String message) {
    // Dialog dialog = createDialogAndSave(context, layoutId, false);
    // TextView tvTitle = (TextView) dialog.findViewById(R.id.tvtitle);
    // TextView tvMessage = (TextView) dialog
    // .findViewById(R.id.tvMessage);
    //
    // if (Util.isNullOrEmpty(title)) {
    // tvTitle.setVisibility(View.GONE);
    // } else {
    // tvTitle.setText(title);
    // }
    //
    // if (Util.isNullOrEmpty(message)) {
    // tvMessage.setVisibility(View.GONE);
    // } else {
    // tvMessage.setText(Html.fromHtml("<html>" + message + "<html>"));
    // }
    //
    // return dialog;
    // }

    public static Dialog showDialogFilter(Context context) {
        Dialog dialog = createDialogAndSave(context, R.layout.dialog_filter, true);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.dialog_filter_rvType);
        GridLayoutManager manager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL,
                false);
        recyclerView.setLayoutManager(manager);
        RadioButtonAdapter adapter = new RadioButtonAdapter(recyclerView);
        recyclerView.setAdapter(adapter);

        List<String> items = new ArrayList<>();
        items.add("Appartement");
        items.add("Maison");
        items.add("Parking");
        items.add("Bureau");
        items.add("Terrain");
        items.add("Commerce");
        adapter.setData(items);

        recyclerView.setMinimumHeight(Util.dpToPx(context, 90));

        return dialog;
    }

    private static Dialog createDialogAndSave(Context context, int layoutId,
            boolean isCancelable) {
        Dialog dialog = createDialog(context, layoutId, isCancelable);
        dialogs.add(dialog);
        return dialog;
    }

    private static Dialog createDialog(Context context, int layoutId,
            boolean isCancelable) {
        return doCreateDialog(context, layoutId, isCancelable, R.style.AlertDialog_AppCompat);
    }

    private static Dialog doCreateDialog(Context context, int layoutId,
            boolean isCancelable, int theme) {
        final SpringDialog dialog = new SpringDialog(context, theme);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.7f;
        dialog.getWindow().setAttributes(lp);
        dialog.setContentView(layoutId);
        dialog.setCancelable(isCancelable);
        dialog.setCanceledOnTouchOutside(isCancelable);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.show();

        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float scaleValue = (float) SpringUtil.mapValueFromRangeToRange(
                        spring.getCurrentValue(), 0, 1, 1.5, 1);
                float alphaValue = (float) SpringUtil.mapValueFromRangeToRange(
                        spring.getCurrentValue(), 0, 1, 0, 1);

                if (dialog.contentView != null) {
                    ViewHelper.setScaleX(dialog.contentView, scaleValue);
                    ViewHelper.setScaleY(dialog.contentView, scaleValue);
                    ViewHelper.setAlpha(dialog.contentView, alphaValue);
                }
            }
        });
        spring.setCurrentValue(0);
        spring.setEndValue(1);

        return dialog;
    }

    private static class SpringDialog extends Dialog {

        private View contentView;

        public SpringDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        public void setContentView(int layoutResID) {
            super.setContentView(layoutResID);
            contentView = findViewById(R.id.dialog_rootView);
        }
    }
}
