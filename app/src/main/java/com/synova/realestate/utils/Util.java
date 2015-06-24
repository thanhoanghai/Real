
package com.synova.realestate.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.synova.realestate.R;
import com.synova.realestate.base.Constants;

import org.joda.time.DateTime;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ducth on 04/06/2015.
 */
public class Util {
    private static final String TAG = Util.class.getName();

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        int px = Math.round(dp
                * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        int dp = Math.round(px
                / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static void hideKeyboard(Activity activity) {
        View focusedView = activity.getCurrentFocus();
        if (focusedView == null)
            return;
        hideKeyboard(activity, focusedView);
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isNullOrEmpty(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        return false;
    }

    public static String join(Iterator<?> iterator, char separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return first == null ? "" : first.toString();
        }

        // two or more elements
        StringBuilder buf = new StringBuilder(256); // Java default is 16,
        // probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            buf.append(separator);
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }

        return buf.toString();
    }

    public static String getDeviceID(Context context) {
        String deviceID = "";
        if (isHaveSimCard(context)) {
            deviceID = generateDeviceIDWithSIM(context);
        } else {
            deviceID = generateDeviceIDWithoutSIM(context);
        }
        LogUtil.i("device ID", deviceID);
        return deviceID;
    }

    private static boolean isHaveSimCard(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT) {
            return true;
        }
        return false;
    }

    /**
     * Need permission : <br>
     * <br>
     * android:name="android.permission.READ_PHONE_STATE"
     *
     * @param context
     * @return
     */
    private static String generateDeviceIDWithSIM(Context context) {
        String deviceID = "";
        TelephonyManager TelephonyMgr = (TelephonyManager) context
                .getSystemService(Activity.TELEPHONY_SERVICE);

        /**
         * getDeviceId() function Returns the unique device ID. for example,the IMEI for GSM and the
         * MEID or ESN for CDMA phones.
         */
        String IMEI = TelephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE

        /**
         * getSubscriberId() function Returns the unique subscriber ID, for example, the IMSI for a
         * GSM phone.
         */
        String IMSI = TelephonyMgr.getSubscriberId();

        /**
         * Serial Number Since Android 2.3 ("Gingerbread") this is available via
         * android.os.Build.SERIAL. Devices without telephony are required to report a unique device
         * ID here; some phones may do so also. Serial number can be identified for the devices such
         * as MIDs (Mobile Internet Devices) or PMPs (Portable Media Players) which are not having
         * telephony services. Device-Id as serial number is available by reading the System
         * Property Value "ro.serialno" To retrieve the serial number for using Device ID, please
         * refer to example code below.
         */
        String serialnum = null;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
        } catch (Exception ignored) {
        }

        /**
         * More specifically, Settings.Secure.ANDROID_ID. A 64-bit number (as a hex string) that is
         * randomly generated on the device's first boot and should remain constant for the lifetime
         * of the device (The value may change if a factory reset is performed on the device.)
         * ANDROID_ID seems a good choice for a unique device identifier. To retrieve the ANDROID_ID
         * for using Device ID, please refer to example code below Disadvantages: Not 100% reliable
         * of Android prior to 2.2 ("Froyo") devices Also, there has been at least one
         * widely-observed bug in a popular handset from a major manufacturer, where every instance
         * has the same ANDROID_ID.
         */
        String ANDROID_ID = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);

        deviceID = md5(IMEI + IMSI + serialnum + ANDROID_ID).toUpperCase();
        LogUtil.i("device ID", deviceID);
        return deviceID;
    }

    private static String generateDeviceIDWithoutSIM(Context context) {
        String deviceID = "";

        String pseudoIMEI = "35"
                + // we make this look like a valid IMEI
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10
                + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
                + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
                + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
                + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
                + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
                + Build.USER.length() % 10; // 13 digits

        /**
         * Serial Number Since Android 2.3 ("Gingerbread") this is available via
         * android.os.Build.SERIAL. Devices without telephony are required to report a unique device
         * ID here; some phones may do so also. Serial number can be identified for the devices such
         * as MIDs (Mobile Internet Devices) or PMPs (Portable Media Players) which are not having
         * telephony services. Device-Id as serial number is available by reading the System
         * Property Value "ro.serialno" To retrieve the serial number for using Device ID, please
         * refer to example code below.
         */
        String serialnum = null;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
        } catch (Exception ignored) {
        }

        /**
         * More specifically, Settings.Secure.ANDROID_ID. A 64-bit number (as a hex string) that is
         * randomly generated on the device's first boot and should remain constant for the lifetime
         * of the device (The value may change if a factory reset is performed on the device.)
         * ANDROID_ID seems a good choice for a unique device identifier. To retrieve the ANDROID_ID
         * for using Device ID, please refer to example code below Disadvantages: Not 100% reliable
         * of Android prior to 2.2 ("Froyo") devices Also, there has been at least one
         * widely-observed bug in a popular handset from a major manufacturer, where every instance
         * has the same ANDROID_ID.
         */
        String ANDROID_ID = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);

        deviceID = md5(pseudoIMEI + serialnum + ANDROID_ID).toUpperCase();

        return deviceID;
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32
            // chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeUrl(String string) {
        return URLEncoder.encode(string);
    }

    public static String getStringResource(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static String[] matchPattern(String text, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        String result = "";
        while (m.find()) {
            result += m.group(1) + ",";
        }
        return result != null ? result.split(",") : null;
    }

    public static boolean integerToBoolean(String booleanIntegerString) {
        return Integer.parseInt(booleanIntegerString) == 1 ? true : false;
    }

    public static int booleanToInteger(boolean integerBoolean) {
        return integerBoolean ? 1 : 0;
    }

    public static String getDateTimeFromMinute(String minuteString) {
        int time = Integer.parseInt(minuteString);
        int hour = time / 60;
        int min = time % 60;

        return String.format("%02d:%02d", hour, min);
    }

    public static char[] concat(char[]... arrays) {
        StringBuilder sb = new StringBuilder(64);
        for (int i = 0; i < arrays.length; i++) {
            sb.append(arrays[i]);
        }

        return sb.toString().toCharArray();
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static int randomInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Secure.getInt(context.getContentResolver(),
                        Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Secure.getString(context.getContentResolver(),
                    Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void openLocationSetting(final Context context) {
        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(myIntent);
    }

    public static void openSetting(Context context) {
        Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(myIntent);
    }

    // public static void notifyGA(Context context, String deviceID, String appID,
    // String screenName) {
    // try {
    // Tracker t = ((KaraokeApplication) context.getApplicationContext())
    // .getTracker(KaraokeApplication.TrackerName.APP_TRACKER);
    // t.setClientId(deviceID);
    // t.setAppId(appID);
    // t.setScreenName(screenName);
    // t.send(new HitBuilders.AppViewBuilder().build());
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // }
    // }

    public static boolean isEmailSetup(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccounts();
        return accounts.length > 0;
    }

    // public static void sendEmail(Activity activity, String[] recipients, String subject,
    // String content) {
    // Intent intent = new Intent(Intent.ACTION_SEND);
    // if (subject != null && subject.length() > 0) {
    // intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    // }
    // if (content != null && content.length() > 0) {
    // ArrayList<String> contents = new ArrayList<>();
    // contents.add(content);
    // intent.putExtra(Intent.EXTRA_TEXT, contents);
    // }
    // if (recipients != null && recipients.length > 0) {
    // intent.putExtra(Intent.EXTRA_EMAIL, recipients);
    // }
    //
    // intent.setType("message/rfc822");
    // try {
    // activity.startActivity(Intent.createChooser(intent, activity
    // .getResources().getString(R.string.choose_email_client)));
    //
    // } catch (android.content.ActivityNotFoundException ex) {
    // Toast.makeText(
    // activity,
    // activity.getResources().getString(R.string.no_email_client),
    // Toast.LENGTH_SHORT).show();
    // }
    // }

    public static boolean checkAppInstalledByStoreUrl(Context context, String storeUrl) {
        String appPackageName = getAppPackageNameFromStoreUrl(storeUrl);
        return checkAppInstalled(context, appPackageName);
    }

    public static boolean checkAppInstalled(Context context, String appPackageName) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(appPackageName, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static void showDownloadAppByStoreUrl(Context context, String storeUrl) {
        String appPackageName = getAppPackageNameFromStoreUrl(storeUrl);
        showDownloadApp(context, appPackageName);
    }

    public static void showDownloadApp(Context context, String appPackageName) {
        try {
            context.startActivity(new Intent(
                    Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException ex) {
            context.startActivity(new Intent(
                    Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="
                            + appPackageName)));
        }
    }

    public static void openAppByStoreUrl(Context context, String storeUrl) {
        String appPackageName = getAppPackageNameFromStoreUrl(storeUrl);
        openApp(context, appPackageName);
    }

    public static void openApp(Context context, String appPackageName) {
        Intent LaunchIntent = context.getPackageManager()
                .getLaunchIntentForPackage(appPackageName);
        context.startActivity(LaunchIntent);
    }

    public static String getAppPackageNameFromStoreUrl(String storeUrl) {
        Pattern pattern = Pattern.compile("id=([^&]+)");
        Matcher matcher = pattern.matcher(storeUrl);
        return matcher.find() ? matcher.group(1) : "";
    }

    public static void makeLinkInTextViewClickable(TextView textView,
            final OnClickableTextClickListener listener) {
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) text;
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            for (URLSpan url : urls) {
                style.removeSpan(url);
                ClickableText click = new ClickableText(url.getURL(), listener);
                style.setSpan(click, sp.getSpanStart(url), sp.getSpanEnd(url),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(style);
        }
    }

    public static String getAppVersion(Context context) {
        String version = "";
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String lowercaseAndCapitalizeFirstLetter(String s) {
        if (s == null)
            return null;
        if (s.length() == 1) {
            return s.toUpperCase();
        }
        if (s.length() > 1) {
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
        return "";
    }

    public static Comparator<String> makeSmartFilterComparator(final String keyword) {
        return new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                if (lhs.startsWith(keyword)) {
                    return rhs.startsWith(keyword) ? lhs.compareTo(rhs) : -1;
                } else {
                    return rhs.startsWith(keyword) ? 1 : lhs.compareTo(rhs);
                }
            }
        };
    }

    public static String getDateTimeString(Calendar calendar) {
        DateTime dateTime = new DateTime(calendar);
        return getDateTimeString(dateTime);
    }

    public static String getDateTimeString(DateTime dateTime) {
        return dateTime.toString(Constants.DATE_TIME_PATTERN);
    }

    public static String getTimeString(String pattern, DateTime dateTime) {
        return dateTime.toString(pattern);
    }

    public static boolean isYesterday(DateTime dateTime) {
        return dateTime.withTimeAtStartOfDay().plusDays(1)
                .isEqual(DateTime.now().withTimeAtStartOfDay());
    }

    public static boolean isToday(DateTime dateTime) {
        return dateTime.withTimeAtStartOfDay().isEqual(DateTime.now().withTimeAtStartOfDay());
    }

    public static boolean isTomorrow(DateTime dateTime) {
        return dateTime.withTimeAtStartOfDay().minusDays(1)
                .isEqual(DateTime.now().withTimeAtStartOfDay());
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }

    public static String formatPriceNumber(double price) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        dfs.setGroupingSeparator(' ');

        String pattern = "#,##0";
        DecimalFormat df = new DecimalFormat(pattern, dfs);
        df.setGroupingUsed(true);
        return df.format(price).replace(",", " ");
    }

    public static Bitmap createMarkerBitmapWithBadge(Context context, Constants.ElementType type,
            int badgeNumber) {
        Bitmap icon = createMarkerBitmap(context, type);
        Bitmap badge = BitmapFactory.decodeResource(context.getResources(), R.drawable.ico_badge);

        Paint bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);

        Paint badgeText = new Paint();
        badgeText.setAntiAlias(true);
        badgeText.setTextSize(Util.dpToPx(context, 12));
        badgeText.setColor(Color.WHITE);

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap mutableBitmap = Bitmap.createBitmap(icon.getWidth() + badge.getWidth() / 2,
                icon.getHeight(), config);
        Canvas canvas = new Canvas(mutableBitmap);

        canvas.drawBitmap(icon, 0, 0, bitmapPaint);

        canvas.drawBitmap(badge, mutableBitmap.getWidth() - badge.getWidth(), 0, bitmapPaint);

        String badgeNum = "" + badgeNumber;
        Rect textBounds = new Rect();
        badgeText.getTextBounds(badgeNum, 0, badgeNum.length(), textBounds);
        canvas.drawText(badgeNum, mutableBitmap.getWidth() - textBounds.width()
                - (badge.getWidth() - textBounds.width()) / 2, textBounds.height()
                + (badge.getHeight() - textBounds.height()) / 2, badgeText);

        return mutableBitmap;
    }

    public static Bitmap createMarkerBitmap(Context context, Constants.ElementType type) {
        int iconResId = 0;
        switch (type) {
            case BIEN:
                iconResId = R.drawable.ico_marker_bien;
                break;
            case AGENCE:
                iconResId = R.drawable.ico_marker_agence;
                break;
            case PARTICULIER:
                iconResId = R.drawable.ico_marker_particulier;
                break;
            case NOTAIRE:
                iconResId = R.drawable.ico_marker_notaire;
                break;
        }

        return BitmapFactory.decodeResource(context.getResources(), iconResId);
    }

    public static LatLng convertPointGeomToLatLng(String pointGeom) {
        String[] point = pointGeom.substring(6, pointGeom.length() - 1).split(" ");
        LatLng latLng = new LatLng(Double.parseDouble(point[0]), Double.parseDouble(point[1]));
        return latLng;
    }

    public static LatLng[] convertZoneGeomToLatLngs(String zoneGeom) {
        String[] points = zoneGeom.substring(9, zoneGeom.length() - 2).split(",");
        LatLng[] latLngs = new LatLng[points.length];
        for (int i = 0; i < points.length; i++) {
            String[] point = points[i].split(" ");
            LatLng latLng = new LatLng(Double.parseDouble(point[0]), Double.parseDouble(point[1]));
            latLngs[i] = latLng;
        }
        return latLngs;
    }

    private static class ClickableText extends ClickableSpan {

        private String url;
        private OnClickableTextClickListener listener;

        ClickableText(String url, OnClickableTextClickListener listener) {
            this.url = url;
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(url);
            }
        }
    }

    public interface OnClickableTextClickListener {
        public void onClick(String url);
    }
}
