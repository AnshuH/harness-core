package software.wings.sm.states;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static software.wings.api.ContainerServiceElement.ContainerServiceElementBuilder.aContainerServiceElement;
import static software.wings.beans.DelegateTask.SyncTaskContext.Builder.aContext;
import static software.wings.beans.ResizeStrategy.RESIZE_NEW_FIRST;
import static software.wings.beans.SettingAttribute.Builder.aSettingAttribute;
import static software.wings.beans.command.KubernetesSetupParams.KubernetesSetupParamsBuilder.aKubernetesSetupParams;
import static software.wings.sm.StateType.KUBERNETES_REPLICATION_CONTROLLER_SETUP;

import com.google.inject.Inject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.fabric8.kubernetes.api.model.extensions.DaemonSet;
import org.mongodb.morphia.annotations.Transient;
import software.wings.annotation.Encryptable;
import software.wings.api.CommandStateExecutionData;
import software.wings.api.ContainerServiceElement;
import software.wings.api.DeploymentType;
import software.wings.beans.Application;
import software.wings.beans.ContainerInfrastructureMapping;
import software.wings.beans.DelegateTask;
import software.wings.beans.DirectKubernetesInfrastructureMapping;
import software.wings.beans.Environment;
import software.wings.beans.GcpKubernetesInfrastructureMapping;
import software.wings.beans.InfrastructureMapping;
import software.wings.beans.SettingAttribute;
import software.wings.beans.command.CommandExecutionResult;
import software.wings.beans.command.ContainerSetupCommandUnitExecutionData;
import software.wings.beans.command.ContainerSetupParams;
import software.wings.beans.command.KubernetesSetupParams;
import software.wings.beans.container.ContainerTask;
import software.wings.beans.container.ImageDetails;
import software.wings.beans.container.KubernetesContainerTask;
import software.wings.beans.container.KubernetesPortProtocol;
import software.wings.beans.container.KubernetesServiceType;
import software.wings.delegatetasks.DelegateProxyFactory;
import software.wings.security.encryption.EncryptedDataDetail;
import software.wings.service.impl.ContainerServiceParams;
import software.wings.service.intfc.ContainerService;
import software.wings.sm.ExecutionContext;
import software.wings.sm.ExecutionStatus;
import software.wings.utils.KubernetesConvention;

import java.util.List;

/**
 * Created by brett on 3/1/17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KubernetesReplicationControllerSetup extends ContainerServiceSetup {
  // *** Note: UI Schema specified in wingsui/src/containers/WorkflowEditor/custom/KubernetesRepCtrlSetup.js

  private String replicationControllerName;
  private KubernetesServiceType serviceType;
  private Integer port;
  private Integer targetPort;
  private KubernetesPortProtocol protocol;
  private String clusterIP;
  private String externalIPs;
  private String loadBalancerIP;
  private Integer nodePort;
  private String externalName;
  private String commandName = "Setup Replication Controller";

  /**
   * Instantiates a new state.
   */
  public KubernetesReplicationControllerSetup(String name) {
    super(name, KUBERNETES_REPLICATION_CONTROLLER_SETUP.name());
  }

  @Inject @Transient private transient DelegateProxyFactory delegateProxyFactory;

  @Override
  protected ContainerSetupParams buildContainerSetupParams(ExecutionContext context, String serviceName,
      ImageDetails imageDetails, Application app, Environment env, ContainerInfrastructureMapping infrastructureMapping,
      ContainerTask containerTask, String clusterName) {
    String controllerNamePrefix = isNotEmpty(replicationControllerName)
        ? KubernetesConvention.normalize(context.renderExpression(replicationControllerName))
        : KubernetesConvention.getControllerNamePrefix(app.getName(), serviceName, env.getName());

    boolean isDaemonSet = false;
    if (containerTask != null) {
      KubernetesContainerTask kubernetesContainerTask = (KubernetesContainerTask) containerTask;
      kubernetesContainerTask.getContainerDefinitions()
          .stream()
          .filter(containerDefinition -> isNotEmpty(containerDefinition.getCommands()))
          .forEach(containerDefinition
              -> containerDefinition.setCommands(
                  containerDefinition.getCommands().stream().map(context::renderExpression).collect(toList())));
      kubernetesContainerTask.setAdvancedConfig(context.renderExpression(kubernetesContainerTask.getAdvancedConfig()));
      isDaemonSet = kubernetesContainerTask.kubernetesType() == DaemonSet.class;
    }

    String previousDaemonSetYaml = null;
    if (isDaemonSet) {
      DelegateTask.SyncTaskContext syncTaskContext =
          aContext().withAccountId(app.getAccountId()).withAppId(app.getUuid()).build();
      SettingAttribute settingAttribute = infrastructureMapping instanceof DirectKubernetesInfrastructureMapping
          ? aSettingAttribute()
                .withValue(((DirectKubernetesInfrastructureMapping) infrastructureMapping).createKubernetesConfig())
                .build()
          : settingsService.get(infrastructureMapping.getComputeProviderSettingId());
      List<EncryptedDataDetail> encryptionDetails = secretManager.getEncryptionDetails(
          (Encryptable) settingAttribute.getValue(), context.getAppId(), context.getWorkflowExecutionId());
      ContainerServiceParams containerServiceParams = ContainerServiceParams.builder()
                                                          .settingAttribute(settingAttribute)
                                                          .containerServiceName(controllerNamePrefix)
                                                          .encryptionDetails(encryptionDetails)
                                                          .clusterName(clusterName)
                                                          .build();
      previousDaemonSetYaml =
          delegateProxyFactory.get(ContainerService.class, syncTaskContext).getDaemonSetYaml(containerServiceParams);
    }

    return aKubernetesSetupParams()
        .withAppName(app.getName())
        .withEnvName(env.getName())
        .withServiceName(serviceName)
        .withClusterName(clusterName)
        .withImageDetails(imageDetails)
        .withClusterIP(clusterIP)
        .withContainerTask(containerTask)
        .withExternalIPs(externalIPs)
        .withExternalName(externalName)
        .withInfraMappingId(infrastructureMapping.getUuid())
        .withLoadBalancerIP(loadBalancerIP)
        .withNodePort(nodePort)
        .withPort(port)
        .withProtocol(protocol)
        .withServiceType(serviceType)
        .withTargetPort(targetPort)
        .withControllerNamePrefix(controllerNamePrefix)
        .withPreviousDaemonSetYaml(previousDaemonSetYaml)
        .build();
  }

  @Override
  protected ContainerServiceElement buildContainerServiceElement(
      CommandStateExecutionData executionData, CommandExecutionResult executionResult, ExecutionStatus status) {
    ContainerSetupCommandUnitExecutionData setupExecutionData =
        (ContainerSetupCommandUnitExecutionData) executionResult.getCommandExecutionData();
    KubernetesSetupParams setupParams = (KubernetesSetupParams) executionData.getContainerSetupParams();
    return aContainerServiceElement()
        .withUuid(executionData.getServiceId())
        .withName(setupExecutionData.getContainerServiceName())
        .withMaxInstances(getMaxInstances() == 0 ? 10 : getMaxInstances())
        .withResizeStrategy(getResizeStrategy() == null ? RESIZE_NEW_FIRST : getResizeStrategy())
        .withClusterName(executionData.getClusterName())
        .withNamespace(setupParams.getNamespace())
        .withDeploymentType(DeploymentType.KUBERNETES)
        .withInfraMappingId(setupParams.getInfraMappingId())
        .build();
  }

  @Override
  protected boolean isValidInfraMapping(InfrastructureMapping infrastructureMapping) {
    return infrastructureMapping instanceof GcpKubernetesInfrastructureMapping
        || infrastructureMapping instanceof DirectKubernetesInfrastructureMapping;
  }

  @Override
  protected String getDeploymentType() {
    return DeploymentType.KUBERNETES.name();
  }

  @Override
  public String getCommandName() {
    return commandName;
  }

  public void setCommandName(String commandName) {
    this.commandName = commandName;
  }

  public String getReplicationControllerName() {
    return replicationControllerName;
  }

  public void setReplicationControllerName(String replicationControllerName) {
    this.replicationControllerName = replicationControllerName;
  }

  /**
   * Gets service type.
   */
  public String getServiceType() {
    return serviceType.name();
  }

  /**
   * Sets service type.
   */
  public void setServiceType(String serviceType) {
    try {
      this.serviceType = KubernetesServiceType.valueOf(serviceType);
    } catch (IllegalArgumentException e) {
      this.serviceType = KubernetesServiceType.None;
    }
  }

  public String getPort() {
    return port.toString();
  }

  public void setPort(String port) {
    this.port = Integer.parseInt(port);
  }

  public String getTargetPort() {
    return targetPort.toString();
  }

  public void setTargetPort(String targetPort) {
    this.targetPort = Integer.parseInt(targetPort);
  }

  public String getProtocol() {
    return protocol.name();
  }

  public void setProtocol(String protocol) {
    try {
      this.protocol = KubernetesPortProtocol.valueOf(protocol);
    } catch (IllegalArgumentException e) {
      this.protocol = KubernetesPortProtocol.TCP;
    }
  }

  public String getClusterIP() {
    return clusterIP;
  }

  public void setClusterIP(String clusterIP) {
    this.clusterIP = clusterIP;
  }

  public String getExternalIPs() {
    return externalIPs;
  }

  public void setExternalIPs(String externalIPs) {
    this.externalIPs = externalIPs;
  }

  public String getLoadBalancerIP() {
    return loadBalancerIP;
  }

  public void setLoadBalancerIP(String loadBalancerIP) {
    this.loadBalancerIP = loadBalancerIP;
  }

  public String getNodePort() {
    return nodePort.toString();
  }

  public void setNodePort(String nodePort) {
    this.nodePort = Integer.parseInt(nodePort);
  }

  public String getExternalName() {
    return externalName;
  }

  public void setExternalName(String externalName) {
    this.externalName = externalName;
  }
}
