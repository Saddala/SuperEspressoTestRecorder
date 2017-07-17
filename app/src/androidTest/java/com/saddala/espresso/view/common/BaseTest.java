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

package com.saddala.espresso.view.common;

import android.app.Activity;
import android.os.Build;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.saddala.espresso.recyclerviewcardview.MainActivity;
import com.saddala.espresso.view.actions.CommonViewActions;
import com.saddala.espresso.view.actions.ScreenshotActions;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public abstract class BaseTest<T extends Activity> {

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    private static void grantRuntimePermissions(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + activity.getPackageName()
                            +" "+ android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    public void compareScreenshot(Activity activity) {
        ScreenshotActions.compareScreenshot(activity);
    }

    public void compareScreenshot(Activity activity, RecyclerView.ViewHolder holder, int height, int width) {
        ScreenshotActions.compareScreenshot(holder.itemView, height, width);
    }

    public void compareScreenshot(Activity activity, View view, String snapName) {
        ScreenshotActions.compareScreenshot(activity, view, view.getMeasuredHeight(), view.getMeasuredWidth(), snapName);
    }

    public void compareScreenshot(Activity activity, String name) {
        ScreenshotActions.compareScreenshot(activity, name);
    }

    public void compareScreenshot(Activity activity, RecyclerView.ViewHolder holder, int height, int width, String name) {
        ScreenshotActions.compareScreenshot(activity, holder.itemView, height, width, name);
    }

    public void compareScreenshot(Activity activity, final View view, final int height, final int width,String name) {
        ScreenshotActions.compareScreenshot(activity, view, height, width, name);
    }

    public void compareScreenshot(Activity activity, List<CommonViewActions.ViewRootData> rootViews, String name) {
        ScreenshotActions.compareScreenshot(activity, rootViews, name);
    }
}
