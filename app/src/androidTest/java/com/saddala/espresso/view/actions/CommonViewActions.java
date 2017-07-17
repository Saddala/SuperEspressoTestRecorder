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
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Rect;
import android.os.Build;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.util.TreeIterables;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import org.hamcrest.Matcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * This class provides application-specific ViewActions which can be used for testing.
 */
public class CommonViewActions {

    static View matchedView = null;

    static final View[] matchedViewHolder = {matchedView};
    /**
     * A custom ViewAction which allows the system to wait for a view matching a passed in matcher
     * @param aViewMatcher The matcher to wait for
     * @param timeout How long, in milliseconds, to wait for this match.
     * @return The constructed @{link ViewAction}.
     */
    private static ViewAction waitForMatch(final Matcher<View> aViewMatcher, final long timeout) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Waiting for view matching " + aViewMatcher;
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadUntilIdle();

                //What time is it now, and what time will it be when this has timed out?
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + timeout;

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        if (aViewMatcher.matches(child)) {
                            matchedViewHolder[0] = child;
                            //we found it! Yay!
                            return;
                        }
                    }

                    //Didn't find it, loop around a bit.
                    uiController.loopMainThreadForAtLeast(50);
                } while (System.currentTimeMillis() < endTime);

                //The action has timed out.
                throw new PerformException.Builder()
                        .withActionDescription(getDescription())
                        .withViewDescription("")
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

    public static void waitForView(Matcher viewMatchers, Long maxTimeOut) {
        onView(isRoot())
                .perform(waitForMatch(viewMatchers, maxTimeOut));
    }

    View view;

    public static View getMatchedView(final Matcher<View> aViewMatcher){
        onView(isRoot())
                .perform(waitForMatch(aViewMatcher, 100));
        return matchedViewHolder[0];
    }


    @SuppressWarnings("unchecked") // no way to check
    public static List<ViewRootData> getRootViews(Activity activity) {
        Object globalWindowManager;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            globalWindowManager = getFieldValue("mWindowManager", activity.getWindowManager());
        } else {
            globalWindowManager = getFieldValue("mGlobal", activity.getWindowManager());
        }
        Object rootObjects = getFieldValue("mRoots", globalWindowManager);
        Object paramsObject = getFieldValue("mParams", globalWindowManager);

        Object[] roots;
        WindowManager.LayoutParams[] params;

        //  There was a change to ArrayList implementation in 4.4
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            roots = ((List) rootObjects).toArray();

            List<WindowManager.LayoutParams> paramsList = (List<WindowManager.LayoutParams>) paramsObject;
            params = paramsList.toArray(new WindowManager.LayoutParams[paramsList.size()]);
        } else {
            roots = (Object[]) rootObjects;
            params = (WindowManager.LayoutParams[]) paramsObject;
        }

        List<ViewRootData> rootViews = viewRootData(roots, params);
        if (rootViews.isEmpty()) {
            return Collections.emptyList();
        }

        offsetRootsTopLeft(rootViews);
        rootViews = ensureRootViewsAreDialogs(rootViews);

        return rootViews;
    }

    private static void offsetRootsTopLeft(List<ViewRootData> rootViews) {
        int minTop = Integer.MAX_VALUE;
        int minLeft = Integer.MAX_VALUE;
        for (ViewRootData rootView : rootViews) {
            if (rootView._winFrame.top < minTop) {
                minTop = rootView._winFrame.top;
            }

            if (rootView._winFrame.left < minLeft) {
                minLeft = rootView._winFrame.left;
            }
        }

        for (ViewRootData rootView : rootViews) {
            rootView._winFrame.offset(-minLeft, -minTop);
        }
    }

    private static Activity ownerActivity(Context context) {
        Context currentContext = context;

        while (currentContext != null) {
            if (currentContext instanceof Activity) {
                return (Activity) currentContext;
            }

            if (currentContext instanceof ContextWrapper && !(currentContext instanceof Application)) {
                currentContext = ((ContextWrapper) currentContext).getBaseContext();
            } else {
                break;
            }
        }

        return null;
    }

    private static List<ViewRootData> ensureRootViewsAreDialogs(List<ViewRootData> viewRoots) {
        if (viewRoots.size() <= 1) {
            return null;
        }

        for (int dialogIndex = 0; dialogIndex < viewRoots.size() - 1; dialogIndex++) {
            ViewRootData viewRoot = viewRoots.get(dialogIndex);
            Activity dialogOwnerActivity = ownerActivity(viewRoot.context());
            if (dialogOwnerActivity == null) {
                // make sure we will never compare null == null
                return null;
            }
            if (!viewRoot.isDialogType()) {
                viewRoots.remove(dialogIndex);
                continue;
            }
        }
        return viewRoots;
    }

    private static List<ViewRootData> viewRootData(Object[] roots, WindowManager.LayoutParams[] params) {
        List<ViewRootData> rootViews = new ArrayList<>();
        for (int i = 0; i < roots.length; i++) {
            Object root = roots[i];

            View view = (View) getFieldValue("mView", root);

            // fixes https://github.com/jraska/Falcon/issues/10
            if (view == null) {
                Log.e("Dialog: ", "null View stored as root in Global window manager, skipping");
                continue;
            }

            if(!view.isShown()){
                continue;
            }

            Object attachInfo = getFieldValue("mAttachInfo", root);
            int top = (int) getFieldValue("mWindowTop", attachInfo);
            int left = (int) getFieldValue("mWindowLeft", attachInfo);

            Rect winFrame = (Rect) getFieldValue("mWinFrame", root);
            Rect area = new Rect(left, top, left + winFrame.width(), top + winFrame.height());

            rootViews.add(new ViewRootData(view, area, params[i]));
        }

        return rootViews;
    }

    private static Object getFieldValue(String fieldName, Object target) {
        try {
            return getFieldValueUnchecked(fieldName, target);
        } catch (Exception e) {
            throw new UnableToTakeScreenshotException(e);
        }
    }

    private static Object getFieldValueUnchecked(String fieldName, Object target)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = findField(fieldName, target.getClass());

        field.setAccessible(true);
        return field.get(target);
    }

    private static Field findField(String name, Class clazz) throws NoSuchFieldException {
        Class currentClass = clazz;
        while (currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (name.equals(field.getName())) {
                    return field;
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        throw new NoSuchFieldException("Field " + name + " not found for class " + clazz);
    }

    public static class ViewRootData {
        private final View _view;
        private final Rect _winFrame;
        private final WindowManager.LayoutParams _layoutParams;

        ViewRootData(View view, Rect winFrame, WindowManager.LayoutParams layoutParams) {
            _view = view;
            _winFrame = winFrame;
            _layoutParams = layoutParams;
        }

        boolean isDialogType() {
            return _layoutParams.type == WindowManager.LayoutParams.TYPE_APPLICATION;
        }

        boolean isActivityType() {
            return _layoutParams.type == WindowManager.LayoutParams.TYPE_BASE_APPLICATION;
        }

        Context context() {
            return _view.getContext();
        }
        View getView(){
            return _view;
        }
    }

    /**
     * Custom exception thrown if there is some exception thrown during
     * screenshot capturing to enable better client code exception handling.
     */
    public static class UnableToTakeScreenshotException extends RuntimeException {
        private UnableToTakeScreenshotException(String detailMessage) {
            super(detailMessage);
        }

        private UnableToTakeScreenshotException(String detailMessage, Exception exception) {
            super(detailMessage, extractException(exception));
        }

        private UnableToTakeScreenshotException(Exception ex) {
            super(extractException(ex));
        }

        /**
         * Method to avoid multiple wrapping. If there is already our exception,
         * just wrap the cause again
         */
        private static Throwable extractException(Exception ex) {
            if (ex instanceof UnableToTakeScreenshotException) {
                return ex.getCause();
            }

            return ex;
        }
    }
}