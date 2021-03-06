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

package org.apache.heron.integration_test.core;

import java.io.Serializable;

import org.apache.heron.api.bolt.IRichBolt;
import org.apache.heron.api.state.State;
import org.apache.heron.api.topology.IStatefulComponent;

public class StatefulIntegrationTestBolt<K extends Serializable, V extends Serializable>
    extends IntegrationTestBolt implements IStatefulComponent<K, V> {
  private static final long serialVersionUID = 3952704349919374346L;
  private IStatefulComponent<K, V> delegate;

  public StatefulIntegrationTestBolt(IRichBolt delegate, boolean ackAuto) {
    super(delegate, ackAuto);
    this.delegate = (IStatefulComponent<K, V>) delegate;
  }

  @Override
  public void initState(State<K, V> state) {
    this.delegate.initState(state);
  }

  @Override
  public void preSave(String checkpointId) {
    this.delegate.preSave(checkpointId);
  }
}
