// Copyright 2017 Twitter. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.heron.api.windowing.triggers;

import java.io.Serializable;
import java.util.logging.Logger;

import org.apache.heron.api.windowing.DefaultEvictionContext;
import org.apache.heron.api.windowing.Event;
import org.apache.heron.api.windowing.TriggerHandler;

/**
 * Handles watermark events and triggers {@link TriggerHandler#onTrigger()} for each window
 * interval that has events to be processed up to the watermark ts.
 */
public class WatermarkTimeTriggerPolicy<T extends Serializable> extends
        AbstractBaseTriggerPolicy<T, Long> {
  private static final Logger LOG = Logger.getLogger(WatermarkTimeTriggerPolicy.class.getName());
  private final long slidingIntervalMs;
  private volatile long nextWindowEndTs;

  public WatermarkTimeTriggerPolicy(long slidingIntervalMs) {
    super();
    this.slidingIntervalMs = slidingIntervalMs;
  }

  @Override
  public void track(Event<T> event) {
    if (started && event.isWatermark()) {
      handleWaterMarkEvent(event);
    }
  }

  @Override
  public void reset() {
    // NOOP
  }

  @Override
  public void shutdown() {
    // NOOP
  }

  /**
   * Invokes the trigger all pending windows up to the
   * watermark timestamp. The end ts of the window is set
   * in the eviction policy context so that the events falling
   * within that window can be processed.
   */
  private void handleWaterMarkEvent(Event<T> event) {
    long watermarkTs = event.getTimestamp();
    long windowEndTs = nextWindowEndTs;
    LOG.fine(String.format("Window end ts %d Watermark ts %d", windowEndTs, watermarkTs));
    while (windowEndTs <= watermarkTs) {
      long currentCount = windowManager.getEventCount(windowEndTs);
      evictionPolicy.setContext(new DefaultEvictionContext(windowEndTs, currentCount));
      if (handler.onTrigger()) {
        windowEndTs += slidingIntervalMs;
      } else {
                /*
                 * No events were found in the previous window interval.
                 * Scan through the events in the queue to find the next
                 * window intervals based on event ts.
                 */
        long ts = getNextAlignedWindowTs(windowEndTs, watermarkTs);
        LOG.fine(String.format("Next aligned window end ts %d", ts));
        if (ts == Long.MAX_VALUE) {
          LOG.fine(String.format("No events to process between %d and watermark ts %d",
              windowEndTs, watermarkTs));
          break;
        }
        windowEndTs = ts;
      }
    }
    nextWindowEndTs = windowEndTs;
  }

  /**
   * Computes the next window by scanning the events in the window and
   * finds the next aligned window between the startTs and endTs. Return the end ts
   * of the next aligned window, i.e. the ts when the window should fire.
   *
   * @param startTs the start timestamp (excluding)
   * @param endTs the end timestamp (including)
   * @return the aligned window end ts for the next window or Long.MAX_VALUE if there
   * are no more events to be processed.
   */
  private long getNextAlignedWindowTs(long startTs, long endTs) {
    long nextTs = windowManager.getEarliestEventTs(startTs, endTs);
    if (nextTs == Long.MAX_VALUE || (nextTs % slidingIntervalMs == 0)) {
      return nextTs;
    }
    return nextTs + (slidingIntervalMs - (nextTs % slidingIntervalMs));
  }

  @Override
  public Long getState() {
    return nextWindowEndTs;
  }

  @Override
  public void restoreState(Long state) {
    nextWindowEndTs = state;
  }

  @Override
  public String toString() {
    return "WatermarkTimeTriggerPolicy{" + "slidingIntervalMs=" + slidingIntervalMs
        + ", nextWindowEndTs=" + nextWindowEndTs + ", started=" + started + '}';
  }
}
