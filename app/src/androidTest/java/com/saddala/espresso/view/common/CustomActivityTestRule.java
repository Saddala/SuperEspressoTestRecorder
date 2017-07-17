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
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

/**
 *  ActivityTestRule is overridden to tackle timing issues while launching activities.
 *  We faced some timing issues while lauching some of the apps
 */
public class CustomActivityTestRule<A extends Activity> extends ActivityTestRule<A> {
    public CustomActivityTestRule(Class<A> activityClass) {
        super(activityClass);
    }

    @Override
    protected void beforeActivityLaunched() {
        //sleep(2);
        super.beforeActivityLaunched();
        //sleep(1); // You can avoid and handle sleeps in better ways.
        // Prepare some mock service calls if they are required
        // Override some dependency injection modules with mocks
    }

    @Override
    protected Intent getActivityIntent() {
        Intent customIntent = new Intent();
        // Add some custom extras etc here
        return customIntent;
    }

    @Override
    protected void afterActivityLaunched() {
        super.afterActivityLaunched();
    }

    @Override
    protected void afterActivityFinished() {
        super.afterActivityFinished();
        // Clean up mocks
    }

    public void sleep(int seconds) {
        if (seconds > 0) {
            try {
                Thread.sleep(seconds * 1000);
            } catch (Exception ex) {
            }
        }
    }
}