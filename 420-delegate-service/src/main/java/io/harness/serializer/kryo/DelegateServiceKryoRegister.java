/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.serializer.kryo;

import io.harness.delegate.beans.Delegate;
import io.harness.delegate.beans.DelegateCapacity;
import io.harness.delegate.beans.DelegateGroup;
import io.harness.delegate.beans.DelegateGroupStatus;
import io.harness.delegate.beans.K8sConfigDetails;
import io.harness.delegate.beans.K8sPermissionType;
import io.harness.perpetualtask.PerpetualTaskClientContext;
import io.harness.serializer.KryoRegistrar;

import com.esotericsoftware.kryo.Kryo;

public class DelegateServiceKryoRegister implements KryoRegistrar {
  @Override
  public void register(Kryo kryo) {
    kryo.register(PerpetualTaskClientContext.class, 40030);
    kryo.register(Delegate.class, 40031);
    kryo.register(DelegateGroup.class, 40041);
    kryo.register(K8sConfigDetails.class, 40042);
    kryo.register(K8sPermissionType.class, 40043);
    kryo.register(DelegateGroupStatus.class, 40044);
    kryo.register(DelegateCapacity.class, 5808);
  }
}
