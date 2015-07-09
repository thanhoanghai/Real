
package com.synova.realestate.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.nineoldandroids.view.ViewHelper;
import com.synova.realestate.R;
import com.synova.realestate.adapters.RadioButtonAdapter;
import com.synova.realestate.base.Constants;
import com.synova.realestate.customviews.rangebar.RangeBar;
import com.synova.realestate.models.DialogFilterPrixDataHolder;

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

    public static ProgressDialog showWaitDialog(final Context context, boolean isCancelable) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(isCancelable);
        dialog.setMessage("Loading...");
        dialog.show();
        return dialog;
    }

    public static Dialog showDialogFilter(final Context context) {
        final Dialog dialog = createDialogAndSave(context, R.layout.dialog_filter, true);

        Button btnClose = (Button) dialog.findViewById(R.id.dialog_filter_btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final DialogFilterPrixDataHolder prixData = new DialogFilterPrixDataHolder();
        final RangeBar priceBar = (RangeBar) dialog.findViewById(R.id.dialog_filter_priceBar);

        final TextView tvMinPrix = (TextView) dialog.findViewById(R.id.dialog_filter_tvMinPrix);
        final TextView tvMaxPrix = (TextView) dialog.findViewById(R.id.dialog_filter_tvMaxPrix);

        final RadioGroup groupAchatLocation = (RadioGroup) dialog
                .findViewById(R.id.dialog_filter_groupAchatLocation);
        groupAchatLocation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == -1) {
                    return;
                }

                switch (checkedId) {
                    case R.id.dialog_filter_btnAchat:
                        prixData.minPrixValue = 50;
                        prixData.maxPrixValue = 2000;
                        prixData.prixStep = 50;
                        prixData.prixCurrency = "k";

                        tvMinPrix.setText("< 50k");
                        tvMaxPrix.setText("> 2M");
                        break;
                    case R.id.dialog_filter_btnLocation:
                        prixData.minPrixValue = 100;
                        prixData.maxPrixValue = 4000;
                        prixData.prixStep = 100;
                        prixData.prixCurrency = "";

                        tvMinPrix.setText("< 100");
                        tvMaxPrix.setText("> 4000");
                        break;
                }
                priceBar.setTickCount(prixData.maxPrixValue / prixData.prixStep);

                if (prixData.checkedId != checkedId) {
                    prixData.checkedId = checkedId;

                    PrefUtil.setPrixMinMax("200-600");
                    String[] minMaxPrix = PrefUtil.getPrixMinMax().split("-");
                    int minPrixIndex = (Integer.parseInt(minMaxPrix[0]) - prixData.minPrixValue)
                            / prixData.prixStep;
                    final int maxPrixIndex = (Integer.parseInt(minMaxPrix[1]) - prixData.minPrixValue)
                            / prixData.prixStep;

                    priceBar.setThumbIndices(minPrixIndex, maxPrixIndex);
                }
            }
        });
        prixData.checkedId = groupAchatLocation.getChildAt(PrefUtil.getAchatLocation().ordinal())
                .getId();
        groupAchatLocation.check(prixData.checkedId);

        final EditText etMotsCles = (EditText) dialog.findViewById(R.id.dialog_filter_etMotsCles);
        etMotsCles.setText(PrefUtil.getMotsCles());

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.dialog_filter_rvType);
        GridLayoutManager manager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL,
                false);
        recyclerView.setLayoutManager(manager);
        final RadioButtonAdapter adapter = new RadioButtonAdapter(recyclerView);
        recyclerView.setAdapter(adapter);

        adapter.selectItem(PrefUtil.getTypeDeBiens().ordinal());

        List<String> items = new ArrayList<>();
        for (Constants.PropertyType type : Constants.PropertyType.values()) {
            items.add(type.getName());
        }
        adapter.setData(items);

        recyclerView.setMinimumHeight(Util.dpToPx(context, 120));

        final int distanceStep = 100;
        final TextView tvDistance = (TextView) dialog.findViewById(R.id.dialog_filter_tvDistance);
        final SeekBar distanceBar = (SeekBar) dialog.findViewById(R.id.dialog_filter_distanceBar);
        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvDistance.setText((progress * distanceStep) + "m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        distanceBar.setProgress(Integer.parseInt(PrefUtil.getDistance()) / distanceStep);

        String[] minMaxPrix = PrefUtil.getPrixMinMax().split("-");
        int minPrixIndex = (Integer.parseInt(minMaxPrix[0]) - prixData.minPrixValue)
                / prixData.prixStep;
        final int maxPrixIndex = (Integer.parseInt(minMaxPrix[1]) - prixData.minPrixValue)
                / prixData.prixStep;

        final TextView tvPrice = (TextView) dialog.findViewById(R.id.dialog_filter_tvPrice);
        priceBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex,
                    int rightThumbIndex) {
                String text;
                if (leftThumbIndex == rightThumbIndex) {
                    text = (prixData.minPrixValue + leftThumbIndex * prixData.prixStep)
                            + prixData.prixCurrency + " €";
                } else {
                    text = (prixData.minPrixValue + leftThumbIndex * prixData.prixStep)
                            + prixData.prixCurrency + " à "
                            + (prixData.minPrixValue + rightThumbIndex * prixData.prixStep)
                            + prixData.prixCurrency + " €";
                }
                tvPrice.setText(text);
            }
        });
        priceBar.setThumbIndices(minPrixIndex, maxPrixIndex);

        final int minSurfaceValue = 10;
        final int stepSurfaceValue = 10;

        String[] minMaxSurface = PrefUtil.getSurfaceMinMax().split("-");
        final int minSurfaceIndex = (Integer.parseInt(minMaxSurface[0]) - minSurfaceValue)
                / stepSurfaceValue;
        int maxSurfaceIndex = (Integer.parseInt(minMaxSurface[1]) - minSurfaceValue)
                / stepSurfaceValue;

        final TextView tvSurface = (TextView) dialog.findViewById(R.id.dialog_filter_tvSurface);
        final RangeBar surfaceBar = (RangeBar) dialog.findViewById(R.id.dialog_filter_surfaceBar);
        surfaceBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex,
                    int rightThumbIndex) {
                String text;
                if (leftThumbIndex == rightThumbIndex) {
                    text = (minSurfaceValue + leftThumbIndex * stepSurfaceValue) + "";
                } else {
                    text = (minSurfaceValue + leftThumbIndex * stepSurfaceValue) + " à "
                            + (minSurfaceValue + rightThumbIndex * stepSurfaceValue);
                }
                tvSurface.setText(Html.fromHtml(String.format(
                        context.getString(R.string.surface_unit), text)));
            }
        });
        surfaceBar.setThumbIndices(minSurfaceIndex, maxSurfaceIndex);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                PrefUtil.setAchatLocatioin(
                        groupAchatLocation.getCheckedRadioButtonId() == R.id.dialog_filter_btnAchat
                                ? Constants.AchatLocation.ACHAT : Constants.AchatLocation.LOCATION);

                PrefUtil.setMotsCles(etMotsCles.getText().toString());

                PrefUtil.setTypeDeBiens(Constants.PropertyType.values()[adapter
                        .getSelectedPosition()]);

                PrefUtil.setDistance("" + (distanceBar.getProgress() * distanceStep));

                PrefUtil.setPrixMinMax((prixData.minPrixValue + priceBar.getLeftIndex()
                        * prixData.prixStep)
                        + "-"
                        + (prixData.minPrixValue + priceBar.getRightIndex() * prixData.prixStep));

                PrefUtil.setSurfaceMinMax(
                        (minSurfaceValue + surfaceBar.getLeftIndex() * stepSurfaceValue)
                                + "-"
                                + (minSurfaceValue + surfaceBar.getRightIndex() * stepSurfaceValue));
            }
        });

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
        return doCreateDialog(context, layoutId, isCancelable,
                android.R.style.Theme_Holo_NoActionBar);
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
