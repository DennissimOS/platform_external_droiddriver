/*
 * Copyright (C) 2013 DroidDriver committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.droiddriver.uiautomation;

import android.app.UiAutomation;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.android.droiddriver.base.AbstractDroidDriver;
import com.google.android.droiddriver.exceptions.TimeoutException;

/**
 * Implementation of a DroidDriver that is driven via the accessibility layer.
 */
public class UiAutomationDriver extends AbstractDroidDriver {
  // TODO: magic const from UiAutomator, but may not be useful
  /**
   * This value has the greatest bearing on the appearance of test execution
   * speeds. This value is used as the minimum time to wait before considering
   * the UI idle after each action.
   */
  private static final long QUIET_TIME_TO_BE_CONSIDERD_IDLE_STATE = 500;// ms

  private final UiAutomationContext context;
  private final UiAutomation uiAutomation;

  public UiAutomationDriver(UiAutomation uiAutomation) {
    this.uiAutomation = uiAutomation;
    this.context = new UiAutomationContext(uiAutomation);
  }

  @Override
  public UiAutomationElement getRootElement() {
    return context.getUiElement(getRootNode());
  }

  private AccessibilityNodeInfo getRootNode() {
    int timeoutMillis = getPoller().getTimeoutMillis();
    try {
      uiAutomation.waitForIdle(QUIET_TIME_TO_BE_CONSIDERD_IDLE_STATE, timeoutMillis);
    } catch (java.util.concurrent.TimeoutException e) {
      throw new TimeoutException(e);
    }
    long end = SystemClock.uptimeMillis() + timeoutMillis;
    while (true) {
      AccessibilityNodeInfo root = uiAutomation.getRootInActiveWindow();
      if (root != null) {
        return root;
      }
      if (SystemClock.uptimeMillis() > end) {
        throw new TimeoutException(
            String.format("Timed out after %d milliseconds waiting for root AccessibilityNodeInfo",
                timeoutMillis));
      }
      SystemClock.sleep(250);
    }
  }
}
