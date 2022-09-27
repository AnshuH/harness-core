/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ci.integrationstage;

import io.harness.beans.steps.stepinfo.InitializeStepInfo;
import io.harness.beans.yaml.extended.infrastrucutre.DockerInfraYaml;
import io.harness.beans.yaml.extended.infrastrucutre.Infrastructure;
import io.harness.delegate.beans.ci.DockerInfraInfo;
import io.harness.delegate.beans.ci.InfraInfo;
import io.harness.delegate.beans.ci.vm.CIVmInitializeTaskParams;
import io.harness.exception.ngexception.CIStageExecutionException;
import io.harness.pms.contracts.ambiance.Ambiance;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DockerInitializeTaskParamsBuilder {
  @Inject private VmInitializeTaskParamsBuilder vmInitializeTaskParamsBuilder;

  public CIVmInitializeTaskParams getDockerInitializeTaskParams(
      InitializeStepInfo initializeStepInfo, Ambiance ambiance) {
    return vmInitializeTaskParamsBuilder.getVmInitializeParams(initializeStepInfo, ambiance, "");
  }

  public static InfraInfo validateInfrastructureAndGetInfraInfo(Infrastructure infrastructure) {
    validateInfrastructure(infrastructure);
    return DockerInfraInfo.builder().build();
  }

  public static void validateInfrastructure(Infrastructure infrastructure) {
    if (((DockerInfraYaml) infrastructure).getSpec() == null) {
      throw new CIStageExecutionException("Docker input infrastructure can not be empty");
    }
  }
}