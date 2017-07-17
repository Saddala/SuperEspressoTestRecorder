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
import android.support.test.espresso.intent.rule.IntentsTestRule;

/**
 * Created by venkatreddy on 24/06/17.
 */

public class CustomIntentsTestRule<A extends Activity> extends IntentsTestRule<A> {
    public CustomIntentsTestRule(Class<A> activityClass) {
        super(activityClass);
    }

    @Override
    protected void beforeActivityLaunched() {
        //sleep(2);
        super.beforeActivityLaunched();
        sleep(1);
        // Maybe prepare some mock service calls
        // Maybe override some depency injection modules with mocks
    }

    @Override
    protected Intent getActivityIntent() {
        Intent customIntent = new Intent();
        // add some custom extras and stuff
        return customIntent;
    }

    @Override
    protected void afterActivityLaunched() {
        super.afterActivityLaunched();
        // maybe you want to do something here
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