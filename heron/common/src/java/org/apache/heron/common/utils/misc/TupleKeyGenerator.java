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

package org.apache.heron.common.utils.misc;

import java.util.Random;

/**
 * Generate tuple key by using Random Class
 */

public class TupleKeyGenerator {
  private final Random rand;

  public TupleKeyGenerator() {
    rand = new Random();
  }

  public long next() {
    return rand.nextLong();
  }
}

