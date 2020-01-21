package software.wings.service.impl.instana;

import static software.wings.beans.TaskType.INSTANA_COLLECT_METRIC_DATA;

import com.google.common.base.Preconditions;

import io.harness.delegate.beans.executioncapability.ExecutionCapability;
import io.harness.security.encryption.EncryptedDataDetail;
import io.harness.security.encryption.EncryptionConfig;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import software.wings.annotation.EncryptableSetting;
import software.wings.beans.InstanaConfig;
import software.wings.beans.TaskType;
import software.wings.delegatetasks.cv.DataCollector;
import software.wings.delegatetasks.cv.InstanaDataCollector;
import software.wings.delegatetasks.delegatecapability.CapabilityHelper;
import software.wings.service.impl.analysis.DataCollectionInfoV2;
import software.wings.service.impl.analysis.MetricsDataCollectionInfo;
import software.wings.settings.SettingValue;
import software.wings.sm.StateType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@FieldNameConstants(innerTypeName = "InstanaDataCollectionInfoKeys")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InstanaDataCollectionInfo extends MetricsDataCollectionInfo {
  private InstanaConfig instanaConfig;
  private String query;
  private List<String> metrics;

  @Builder
  public InstanaDataCollectionInfo(String accountId, String applicationId, String envId, Instant startTime,
      Instant endTime, Set<String> hosts, String cvConfigId, String stateExecutionId, String workflowId,
      String workflowExecutionId, String serviceId, String cvTaskId, String connectorId,
      List<EncryptedDataDetail> encryptedDataDetails, Map<String, String> hostsToGroupNameMap,
      InstanaConfig instanaConfig, String query, List<String> metrics) {
    super(accountId, applicationId, envId, startTime, endTime, hosts, cvConfigId, stateExecutionId, workflowId,
        workflowExecutionId, serviceId, cvTaskId, connectorId, encryptedDataDetails, hostsToGroupNameMap);
    this.query = query;
    this.metrics = metrics;
    this.instanaConfig = instanaConfig;
  }
  @Override
  public TaskType getTaskType() {
    return INSTANA_COLLECT_METRIC_DATA;
  }

  @Override
  public StateType getStateType() {
    return StateType.INSTANA;
  }

  @Override
  public Class<? extends DataCollector<? extends DataCollectionInfoV2>> getDataCollectorImplClass() {
    return InstanaDataCollector.class;
  }

  @Override
  public Optional<String> getUrlForValidation() {
    return Optional.of(instanaConfig.getInstanaUrl());
  }

  @Override
  public Optional<EncryptionConfig> getEncryptionConfig() {
    return Optional.of(getEncryptedDataDetails().get(0).getEncryptionConfig());
  }

  @Override
  public Optional<EncryptableSetting> getEncryptableSetting() {
    return Optional.of(instanaConfig);
  }

  @Override
  public DataCollectionInfoV2 deepCopy() {
    InstanaDataCollectionInfo instanaDataCollectionInfo = InstanaDataCollectionInfo.builder()
                                                              .metrics(new ArrayList<>(this.metrics))
                                                              .query(this.query)
                                                              .instanaConfig(instanaConfig)
                                                              .build();
    super.copy(instanaDataCollectionInfo);
    return instanaDataCollectionInfo;
  }

  @Override
  public void setSettingValue(SettingValue settingValue) {
    instanaConfig = (InstanaConfig) settingValue;
  }

  @Override
  protected void validateParams() {
    Preconditions.checkNotNull(instanaConfig, InstanaDataCollectionInfoKeys.instanaConfig);
    Preconditions.checkNotNull(query, InstanaDataCollectionInfoKeys.query);
    Preconditions.checkNotNull(metrics, InstanaDataCollectionInfoKeys.metrics);
    Preconditions.checkArgument(
        !metrics.isEmpty(), InstanaDataCollectionInfoKeys.metrics + " metrics list can not be empty");
  }

  @Override
  public List<ExecutionCapability> fetchRequiredExecutionCapabilities() {
    return CapabilityHelper.generateDelegateCapabilities(instanaConfig, getEncryptedDataDetails());
  }
}
