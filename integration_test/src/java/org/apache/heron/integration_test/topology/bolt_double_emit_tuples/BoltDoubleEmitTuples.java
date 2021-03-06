// Copyright 2016 Twitter. All rights reserved.
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
package org.apache.heron.integration_test.topology.bolt_double_emit_tuples;

import java.net.MalformedURLException;

import org.apache.heron.api.tuple.Fields;
import org.apache.heron.integration_test.common.AbstractTestTopology;
import org.apache.heron.integration_test.common.bolt.DoubleTuplesBolt;
import org.apache.heron.integration_test.common.spout.ABSpout;
import org.apache.heron.integration_test.core.TestTopologyBuilder;

/**
 * Topology to test basic structure, single thread spout and bolt, shuffleGrouping
 */
public final class BoltDoubleEmitTuples extends AbstractTestTopology {

  private BoltDoubleEmitTuples(String[] args) throws MalformedURLException {
    super(args);
  }

  @Override
  protected TestTopologyBuilder buildTopology(TestTopologyBuilder builder) {
    builder.setSpout("ab-spout", new ABSpout(), 1);
    builder.setBolt("double-tuples-bolt", new DoubleTuplesBolt(new Fields("word")), 1)
        .shuffleGrouping("ab-spout");
    return builder;
  }

  public static void main(String[] args) throws Exception {
    BoltDoubleEmitTuples topology = new BoltDoubleEmitTuples(args);
    topology.submit();
  }
}
