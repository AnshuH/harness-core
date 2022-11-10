package io.harness.ci.serializer.vm;

import static io.harness.beans.serializer.RunTimeInputHandler.resolveMapParameter;

import io.harness.beans.serializer.RunTimeInputHandler;
import io.harness.beans.steps.stepinfo.ActionStepInfo;
import io.harness.beans.sweepingoutputs.StageInfraDetails;
import io.harness.ci.config.CIExecutionServiceConfig;
import io.harness.ci.serializer.SerializerUtils;
import io.harness.delegate.beans.ci.vm.steps.VmRunStep;
import io.harness.exception.ngexception.CIStageExecutionException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class VmActionStepSerializer {
  @Inject CIExecutionServiceConfig ciExecutionServiceConfig;

  public VmRunStep serialize(ActionStepInfo actionStepInfo, String identifier, StageInfraDetails stageInfraDetails) {
    if (stageInfraDetails.getType() != StageInfraDetails.Type.DLITE_VM) {
      throw new CIStageExecutionException("Action step is only applicable for builds on cloud infrastructure");
    }

    String uses =
        RunTimeInputHandler.resolveStringParameter("Uses", "Action", identifier, actionStepInfo.getUses(), true);
    Map<String, String> with = resolveMapParameter("with", "Action", identifier, actionStepInfo.getWith(), false);

    Map<String, String> env = resolveMapParameter("env", "Action", identifier, actionStepInfo.getEnv(), false);
    if (env == null) {
      env = new HashMap<>();
    }
    env.put("PLUGIN_WITH", SerializerUtils.convertMapToJsonString(with));

    return VmRunStep.builder()
        .entrypoint(Arrays.asList("plugin", "-kind", "action", "-name"))
        .command(uses)
        .envVariables(env)
        .build();
  }
}