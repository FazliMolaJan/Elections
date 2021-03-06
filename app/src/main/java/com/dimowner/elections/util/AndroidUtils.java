/*
 * Copyright 2019 Dmitriy Ponomarenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dimowner.elections.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.dimowner.elections.R;

/**
 * Android related utilities methods.
 */
public class AndroidUtils {

	//Prevent object instantiation
	private AndroidUtils() {
	}

	/**
	 * Convert density independent pixels value (dip) into pixels value (px).
	 *
	 * @param dp Value needed to convert
	 * @return Converted value in pixels.
	 */
	public static float dpToPx(int dp) {
		return dpToPx((float) dp);
	}

	/**
	 * Convert density independent pixels value (dip) into pixels value (px).
	 *
	 * @param dp Value needed to convert
	 * @return Converted value in pixels.
	 */
	public static float dpToPx(float dp) {
		return (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	/**
	 * Convert pixels value (px) into density independent pixels (dip).
	 *
	 * @param px Value needed to convert
	 * @return Converted value in pixels.
	 */
	public static float pxToDp(int px) {
		return pxToDp((float) px);
	}

	/**
	 * Convert pixels value (px) into density independent pixels (dip).
	 *
	 * @param px Value needed to convert
	 * @return Converted value in pixels.
	 */
	public static float pxToDp(float px) {
		return (px / Resources.getSystem().getDisplayMetrics().density);
	}

	/**
	 * Check connectivity to network
	 * @param context app context
	 * @return true if connected, otherwise - false
	 */
	public static boolean isConnectedToNetwork(Context context) {
		NetworkInfo networkInfo = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	// A method to find height of the status bar
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
			if (resourceId > 0) {
				result = context.getResources().getDimensionPixelSize(resourceId);
			}
		}
		return result;
	}

	public static void setTranslucent(Activity activity, boolean translucent) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window w = activity.getWindow();
			if (translucent) {
				w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			} else {
				w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			}
		}
	}

	// A method to find height of the navigation bar
	public static int getNavigationBarHeight(Context context) {
		int result = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (hasNavBar(context)) {
				int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
				if (resourceId > 0) {
					result = context.getResources().getDimensionPixelSize(resourceId);
				}
			}
		}
		return result;
	}

	public static boolean hasNavBar (Context context) {
//		int id = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
//		return id > 0 && context.getResources().getBoolean(id);
		boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
		boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
		return !hasMenuKey && !hasBackKey;
	}

	public static String getBrandModel() {
		return Build.BRAND + " " + android.os.Build.MODEL;
	}

	public static int getAndroidVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	public static String getDisplayLanguage(Context context) {
		return context.getResources().getConfiguration().locale.getDisplayLanguage();
	}

	public static boolean isEmulator() {
		return Build.FINGERPRINT.startsWith("generic")
				|| Build.FINGERPRINT.startsWith("unknown")
				|| Build.MODEL.contains("google_sdk")
				|| Build.MODEL.contains("Emulator")
				|| Build.MODEL.contains("Android SDK built for x86")
				|| Build.MANUFACTURER.contains("Genymotion")
				|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
				|| "google_sdk".equals(Build.PRODUCT);
	}

	public static String getDeviceIdentifier(Context context) {
		String serial = android.os.Build.SERIAL;
		if (serial != null && !serial.isEmpty() && !serial.equalsIgnoreCase("unknown")) {
			return serial.toLowerCase();
		} else {
			String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
			if (androidId != null && !androidId.isEmpty()) {
				return androidId.toLowerCase();
			} else {
				String macWlan = NetworkUtils.getMACAddress("wlan0");
				if (macWlan != null && !macWlan.isEmpty()) {
					return macWlan.toLowerCase();
				} else {
					String macEth = NetworkUtils.getMACAddress("eth0");
					if (macEth != null && !macEth.isEmpty()) {
						return macEth.toLowerCase();
					} else {
						return "";
					}
				}
			}
		}
	}

	public static void showDialog(Activity activity, String title, String content,
											View.OnClickListener positiveBtnListener, View.OnClickListener negativeBtnListener){
		showDialog(activity, -1, -1, title, content, positiveBtnListener, negativeBtnListener);
	}

	public static void showDialog(Activity activity, int resTitle, int resContent,
											View.OnClickListener positiveBtnListener, View.OnClickListener negativeBtnListener){
		showDialog(activity, -1, -1, resTitle, resContent, positiveBtnListener, negativeBtnListener);
	}

	public static void showDialog(Activity activity, int positiveBtnTextRes, int negativeBtnTextRes, int resTitle, int resContent,
											View.OnClickListener positiveBtnListener, View.OnClickListener negativeBtnListener){
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		View view = activity.getLayoutInflater().inflate(R.layout.dialog_layout, null, false);
		((TextView)view.findViewById(R.id.dialog_title)).setText(resTitle);
		((TextView)view.findViewById(R.id.dialog_content)).setText(resContent);
		if (negativeBtnListener != null) {
			Button negativeBtn = view.findViewById(R.id.dialog_negative_btn);
			if (negativeBtnTextRes >=0) {
				negativeBtn.setText(negativeBtnTextRes);
			}
			negativeBtn.setOnClickListener(v -> {
				negativeBtnListener.onClick(v);
				dialog.dismiss();
			});
		} else {
			view.findViewById(R.id.dialog_negative_btn).setVisibility(View.GONE);
		}
		if (positiveBtnListener != null) {
			Button positiveBtn = view.findViewById(R.id.dialog_positive_btn);
			if (positiveBtnTextRes >=0) {
				positiveBtn.setText(positiveBtnTextRes);
			}
			positiveBtn.setOnClickListener(v -> {
				positiveBtnListener.onClick(v);
				dialog.dismiss();
			});
		} else {
			view.findViewById(R.id.dialog_positive_btn).setVisibility(View.GONE);
		}
		dialog.setContentView(view);
		dialog.show();
	}

	public static void showDialog(Activity activity, int positiveBtnTextRes, int negativeBtnTextRes, String title, String content,
											View.OnClickListener positiveBtnListener, View.OnClickListener negativeBtnListener){
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		View view = activity.getLayoutInflater().inflate(R.layout.dialog_layout, null, false);
		((TextView)view.findViewById(R.id.dialog_title)).setText(title);
		((TextView)view.findViewById(R.id.dialog_content)).setText(content);
		if (negativeBtnListener != null) {
			Button negativeBtn = view.findViewById(R.id.dialog_negative_btn);
			if (negativeBtnTextRes >=0) {
				negativeBtn.setText(negativeBtnTextRes);
			}
			negativeBtn.setOnClickListener(v -> {
				negativeBtnListener.onClick(v);
				dialog.dismiss();
			});
		} else {
			view.findViewById(R.id.dialog_negative_btn).setVisibility(View.GONE);
		}
		if (positiveBtnListener != null) {
			Button positiveBtn = view.findViewById(R.id.dialog_positive_btn);
			if (positiveBtnTextRes >=0) {
				positiveBtn.setText(positiveBtnTextRes);
			}
			positiveBtn.setOnClickListener(v -> {
				positiveBtnListener.onClick(v);
				dialog.dismiss();
			});
		} else {
			view.findViewById(R.id.dialog_positive_btn).setVisibility(View.GONE);
		}
		dialog.setContentView(view);
		dialog.show();
	}

	public static boolean isAndroid5() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	}

	public static void transparentNavigationBar(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}

	public static int candidateCodeToResource(String code) {
		switch (code) {
			case "porohovenko192":
				return R.drawable.porohovenko192;
			case "tishomenko192":
				return R.drawable.tishomenko192;
			case "zelenenko192":
				return R.drawable.zelenenko192;
			case "lyashkenko192":
				return R.drawable.lyashkenko192;
			case "gricenenko192":
				return R.drawable.gricenenko192;
			case "boikenko192":
				return R.drawable.boikenko192;
			case "kivenko192":
				return R.drawable.kivenko192;
			case "vilkulenko192":
				return R.drawable.vilkulenko192;
			case "other192":
				return R.drawable.other192;

			default:
				return R.drawable.other192;
		}
	}

	public static int candidateCodeToResourceBig(String code) {
		switch (code) {
			case "porohovenko192":
				return R.drawable.porohovenko512;
			case "tishomenko192":
				return R.drawable.tishomenko512;
			case "zelenenko192":
				return R.drawable.zelenenko512;
			case "lyashkenko192":
				return R.drawable.lyashkenko512;
			case "gricenenko192":
				return R.drawable.gricenenko512;
			case "boikenko192":
				return R.drawable.boikenko512;
			case "kivenko192":
				return R.drawable.kivenko512;
			case "vilkulenko192":
				return R.drawable.vilkulenko512;
			case "other192":
				return R.drawable.other512;

			default:
				return R.drawable.other512;
		}
	}
}
