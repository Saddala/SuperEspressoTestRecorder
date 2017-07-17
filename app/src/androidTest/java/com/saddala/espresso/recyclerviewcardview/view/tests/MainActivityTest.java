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

package com.saddala.espresso.recyclerviewcardview.view.tests;


import android.support.test.espresso.ViewInteraction;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.saddala.espresso.recyclerviewcardview.MainActivity;
import com.saddala.espresso.recyclerviewcardview.R;
import com.saddala.espresso.view.common.BaseTest;
import com.saddala.espresso.view.common.CustomActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.saddala.espresso.view.actions.CommonViewActions.getMatchedView;
import static com.saddala.espresso.view.matchers.CustomViewMatchers.childAtPosition;
import static org.hamcrest.Matchers.allOf;

@LargeTest
//@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends BaseTest {


    @Rule
    public CustomActivityTestRule<MainActivity> mActivityTestRule = new CustomActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.my_recycler_view), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.my_recycler_view), isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(2, click()));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.add_item), withText("Add"), withContentDescription("Add"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.my_recycler_view), isDisplayed()));
        recyclerView3.perform(actionOnItemAtPosition(4, click()));

        ViewInteraction recyclerView4 = onView(
                allOf(withId(R.id.my_recycler_view), isDisplayed()));
        recyclerView4.perform(actionOnItemAtPosition(4, click()));

    }

    @Test
    public void testNewMethod2() {

        View recyclerView5 = getMatchedView(
                allOf(withId(R.id.my_recycler_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        compareScreenshot(mActivityTestRule.getActivity(), recyclerView5, "0");

        ViewInteraction recyclerView6 = onView(
                allOf(withId(R.id.my_recycler_view), isDisplayed()));
        recyclerView6.perform(actionOnItemAtPosition(2, click()));

    }
}
