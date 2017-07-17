/*
 * Copyright (C) 2017. Saddala.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.saddala.espresso.view.actions;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.saddala.espresso.recyclerviewcardview.MainActivity;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

public class ScreenshotActions {

    public static void compareScreenshot(Activity activity) {
        Screenshot.snapActivity(activity).record();
    }

    public static void compareScreenshot(RecyclerView.ViewHolder holder, int height, int width) {
        compareScreenshot(holder.itemView, height, width);
    }

    public static void compareScreenshot(final View view, final int height, final int width) {
        Context context = getInstrumentation().getTargetContext();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        MainActivity.getActivityContext().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ViewHelpers.setupView(view)
                            .setExactHeightPx(height)
                            .setExactWidthPx(width)
                            .layout();
                } finally {
                    //Implement if anything required.
                }
            }
        });
        Screenshot.snap(view).record();
    }

    public static void compareScreenshot(Activity activity, View view, String snapName) {
        compareScreenshot(activity, view, view.getMeasuredHeight(), view.getMeasuredWidth(), snapName);
    }

    public static void compareScreenshot(Activity activity, String name) {
        Screenshot.snapActivity(activity).setName(name).record();
    }

    public static void compareScreenshot(Activity activity, RecyclerView.ViewHolder holder, int height, int width, String name) {
        compareScreenshot(activity, holder.itemView, height, width, name);
    }

    public static void compareScreenshot(Activity activity, final View view, final int height, final int width,String name) {
        Context context = getInstrumentation().getTargetContext();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ViewHelpers.setupView(view)
                            .setExactHeightPx(height)
                            .setExactWidthPx(width)
                            .layout();
                } finally {
                    //Implement if anything required.
                }
            }
        });
        Screenshot.snap(view).setName(name).record();
    }

    public static void compareScreenshot(Activity activity, List<CommonViewActions.ViewRootData> rootViews, String name) {
        int i = 0;
        final Activity localActivity = activity;
        for(final CommonViewActions.ViewRootData rootView: rootViews) {
            final String snapName = name+"_"+i;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        compareScreenshot(localActivity, rootView.getView(), rootView.getView().getMeasuredHeight(), rootView.getView().getMeasuredWidth(), snapName);
                    } finally {
                        //Implement if anything required.
                    }
                }
            });
            i++;
        }
    }
}
