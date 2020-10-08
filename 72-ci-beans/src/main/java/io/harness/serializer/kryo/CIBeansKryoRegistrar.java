package io.harness.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import io.harness.beans.CIPipelineSetupParameters;
import io.harness.beans.build.CIPipelineDetails;
import io.harness.beans.build.PublishedArtifact;
import io.harness.beans.environment.K8BuildJobEnvInfo;
import io.harness.beans.environment.pod.PodSetupInfo;
import io.harness.beans.environment.pod.container.ContainerDefinitionInfo;
import io.harness.beans.environment.pod.container.ContainerImageDetails;
import io.harness.beans.execution.BranchWebhookEvent;
import io.harness.beans.execution.CommitDetails;
import io.harness.beans.execution.ExecutionSource;
import io.harness.beans.execution.PRWebhookEvent;
import io.harness.beans.execution.WebhookEvent;
import io.harness.beans.execution.WebhookExecutionSource;
import io.harness.beans.execution.WebhookGitUser;
import io.harness.beans.executionargs.CIExecutionArgs;
import io.harness.beans.inputset.WebhookTriggerExecutionInputSet;
import io.harness.beans.stages.IntegrationStage;
import io.harness.beans.stages.IntegrationStageStepParameters;
import io.harness.beans.steps.CiStepOutcome;
import io.harness.beans.steps.stepinfo.BuildEnvSetupStepInfo;
import io.harness.beans.steps.stepinfo.CleanupStepInfo;
import io.harness.beans.steps.stepinfo.GitCloneStepInfo;
import io.harness.beans.steps.stepinfo.LiteEngineTaskStepInfo;
import io.harness.beans.steps.stepinfo.PublishStepInfo;
import io.harness.beans.steps.stepinfo.RestoreCacheStepInfo;
import io.harness.beans.steps.stepinfo.RunStepInfo;
import io.harness.beans.steps.stepinfo.SaveCacheStepInfo;
import io.harness.beans.steps.stepinfo.TestStepInfo;
import io.harness.beans.steps.stepinfo.publish.artifact.DockerFileArtifact;
import io.harness.beans.steps.stepinfo.publish.artifact.connectors.DockerhubConnector;
import io.harness.beans.steps.stepinfo.publish.artifact.connectors.EcrConnector;
import io.harness.beans.steps.stepinfo.publish.artifact.connectors.GcrConnector;
import io.harness.beans.steps.stepinfo.publish.artifact.connectors.NexusConnector;
import io.harness.beans.steps.stepinfo.publish.artifact.connectors.S3Connector;
import io.harness.beans.sweepingoutputs.ContextElement;
import io.harness.beans.sweepingoutputs.K8PodDetails;
import io.harness.beans.sweepingoutputs.StepTaskDetails;
import io.harness.beans.yaml.extended.CustomVariables;
import io.harness.beans.yaml.extended.artifact.DockerHubArtifactStreamYaml;
import io.harness.beans.yaml.extended.connector.GitConnectorYaml;
import io.harness.beans.yaml.extended.container.Container;
import io.harness.beans.yaml.extended.infrastrucutre.K8sDirectInfraYaml;
import io.harness.beans.yaml.extended.infrastrucutre.K8sGCPInfraYaml;
import io.harness.ci.beans.entities.BuildNumber;
import io.harness.ci.stdvars.BuildStandardVariables;
import io.harness.ci.stdvars.GitVariables;
import io.harness.serializer.KryoRegistrar;

import java.util.LinkedHashSet;

/**
 * Class will register all kryo classes
 */

public class CIBeansKryoRegistrar implements KryoRegistrar {
  @Override
  public void register(Kryo kryo) {
    kryo.register(K8PodDetails.class, 100001);
    kryo.register(ContextElement.class, 100002);
    kryo.register(BuildEnvSetupStepInfo.class, 100003);
    kryo.register(CIPipelineSetupParameters.class, 100004);
    kryo.register(CleanupStepInfo.class, 100005);
    kryo.register(GitCloneStepInfo.class, 100006);
    kryo.register(IntegrationStageStepParameters.class, 100007);
    kryo.register(LiteEngineTaskStepInfo.class, 100008);
    kryo.register(PublishStepInfo.class, 100009);
    kryo.register(RestoreCacheStepInfo.class, 100010);
    kryo.register(RunStepInfo.class, 100011);
    kryo.register(SaveCacheStepInfo.class, 100012);
    kryo.register(TestStepInfo.class, 100013);
    kryo.register(StepTaskDetails.class, 100014);
    kryo.register(BuildStandardVariables.class, 100015);
    kryo.register(CIExecutionArgs.class, 100016);
    kryo.register(BuildNumber.class, 100017);
    kryo.register(IntegrationStage.class, 100018);
    kryo.register(Container.class, 100019);
    kryo.register(Container.Resources.class, 100020);
    kryo.register(Container.Limit.class, 100021);
    kryo.register(Container.Reserve.class, 100022);
    kryo.register(CustomVariables.class, 100023);
    kryo.register(K8BuildJobEnvInfo.class, 100024);
    kryo.register(K8BuildJobEnvInfo.PodsSetupInfo.class, 100025);
    kryo.register(PodSetupInfo.class, 100026);
    kryo.register(PodSetupInfo.PodSetupParams.class, 100027);
    kryo.register(ContainerDefinitionInfo.class, 100028);
    kryo.register(ContainerImageDetails.class, 100029);
    kryo.register(LinkedHashSet.class, 100030);
    kryo.register(DockerFileArtifact.class, 100031);
    kryo.register(DockerhubConnector.class, 100032);
    kryo.register(EcrConnector.class, 100033);
    kryo.register(GcrConnector.class, 100034);
    kryo.register(NexusConnector.class, 100035);
    kryo.register(S3Connector.class, 100036);
    kryo.register(GitConnectorYaml.class, 100037);
    kryo.register(GitConnectorYaml.Spec.class, 100038);
    kryo.register(GitConnectorYaml.Spec.AuthScheme.class, 100039);
    kryo.register(K8sDirectInfraYaml.class, 100040);
    kryo.register(K8sDirectInfraYaml.Spec.class, 100041);
    kryo.register(K8sGCPInfraYaml.Spec.class, 100042);
    kryo.register(K8sGCPInfraYaml.class, 100043);
    kryo.register(WebhookExecutionSource.class, 100044);
    kryo.register(WebhookGitUser.class, 100045);
    kryo.register(WebhookEvent.class, 100046);
    kryo.register(PRWebhookEvent.class, 100047);
    kryo.register(CommitDetails.class, 100048);
    kryo.register(BranchWebhookEvent.class, 100049);
    kryo.register(CIPipelineDetails.class, 100050);
    kryo.register(PublishedArtifact.class, 100051);
    kryo.register(DockerHubArtifactStreamYaml.class, 100052);
    kryo.register(DockerHubArtifactStreamYaml.Spec.class, 100053);
    kryo.register(GitVariables.class, 100054);
    kryo.register(WebhookTriggerExecutionInputSet.class, 100055);
    kryo.register(ExecutionSource.Type.class, 100056);
    kryo.register(CiStepOutcome.class, 100057);
  }
}
