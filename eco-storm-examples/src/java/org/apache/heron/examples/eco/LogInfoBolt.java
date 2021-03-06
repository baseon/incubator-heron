//  Copyright 2018 Twitter. All rights reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
package org.apache.heron.examples.eco;


import java.util.logging.Logger;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

/**
 * Simple bolt that does nothing other than LOG.info() every tuple received.
 *
 */
@SuppressWarnings("serial")
public class LogInfoBolt extends BaseBasicBolt {
  private static final Logger LOG = Logger.getLogger(LogInfoBolt.class.getName());

  @Override
  public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
    LOG.info("{ }" + tuple);
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

  }
}
