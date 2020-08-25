package io.harness.cvng.beans;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
@Builder
public class MetricPackDTO {
  String accountId;
  String projectIdentifier;
  DataSourceType dataSourceType;
  String identifier;
  CVMonitoringCategory category;
  Set<MetricDefinitionDTO> metrics;
  List<TimeSeriesThresholdDTO> thresholds;

  @Value
  @Builder
  public static class MetricDefinitionDTO {
    String name;
    TimeSeriesMetricType type;
    String path;
    String validationPath;
    List<TimeSeriesThresholdDTO> thresholds;
    boolean included;
  }
}
