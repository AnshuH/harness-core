/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.cvng.servicelevelobjective.services.api;

import io.harness.cvng.core.beans.params.TimeRangeParams;
import io.harness.cvng.servicelevelobjective.beans.SLIMissingDataType;
import io.harness.cvng.servicelevelobjective.beans.SLODashboardWidget.SLOGraphData;
import io.harness.cvng.servicelevelobjective.entities.CompositeServiceLevelObjective;
import io.harness.cvng.servicelevelobjective.entities.CompositeServiceLevelObjective.ServiceLevelObjectivesDetail;
import io.harness.cvng.servicelevelobjective.entities.SLIRecord;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface CompositeSLORecordService {
  void create(Map<ServiceLevelObjectivesDetail, List<SLIRecord>> serviceLevelObjectivesDetailSLIRecordMap,
      Map<ServiceLevelObjectivesDetail, SLIMissingDataType> objectivesDetailSLIMissingDataTypeMap, int sloVersion,
      String verificationTaskId, Instant startTime, Instant endTime);
  SLOGraphData getGraphData(CompositeServiceLevelObjective compositeServiceLevelObjective, Instant startTime,
      Instant endTime, int totalErrorBudgetMinutes, int sloVersion);
  SLOGraphData getGraphData(CompositeServiceLevelObjective compositeServiceLevelObjective, Instant startTime,
      Instant endTime, int totalErrorBudgetMinutes, int sloVersion, TimeRangeParams timeRangeParams);
}