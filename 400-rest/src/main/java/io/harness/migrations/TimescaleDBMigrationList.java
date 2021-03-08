package io.harness.migrations;

import io.harness.annotations.dev.Module;
import io.harness.annotations.dev.TargetModule;
import io.harness.migrations.timescaledb.AddAccountIdStatusIndexToDeployment;
import io.harness.migrations.timescaledb.AddAlertTypeColumnToBudgetAlerts;
import io.harness.migrations.timescaledb.AddCostEvents;
import io.harness.migrations.timescaledb.AddDeploymentTagsToDeployment;
import io.harness.migrations.timescaledb.AddExplorerV2Indices;
import io.harness.migrations.timescaledb.AddFeedbackToAnomalies;
import io.harness.migrations.timescaledb.AddFieldsToServiceGuardStats;
import io.harness.migrations.timescaledb.AddFieldsToWorkflowCVMetrics;
import io.harness.migrations.timescaledb.AddIdleUnallocatedColumns;
import io.harness.migrations.timescaledb.AddIndexToInstanceV2Migration;
import io.harness.migrations.timescaledb.AddIndicesForCostEvents;
import io.harness.migrations.timescaledb.AddInstancesDeployedToDeployment;
import io.harness.migrations.timescaledb.AddMaxUtilColumns;
import io.harness.migrations.timescaledb.AddNonComputeCostColumnToBillingData;
import io.harness.migrations.timescaledb.AddPercentagesToCostEvents;
import io.harness.migrations.timescaledb.AddRequestColumnToBillingData;
import io.harness.migrations.timescaledb.AddRollbackToDeployment;
import io.harness.migrations.timescaledb.AddSchemaForServiceGuardStats;
import io.harness.migrations.timescaledb.AddSlackNotificationSupportAnomalies;
import io.harness.migrations.timescaledb.AddStorageSupportK8sUtilTable;
import io.harness.migrations.timescaledb.AddSystemCostBillingData;
import io.harness.migrations.timescaledb.AddingToCVDeploymentMetrics;
import io.harness.migrations.timescaledb.AlterCEUtilizationDataTables;
import io.harness.migrations.timescaledb.ChangeToTimeStampTZ;
import io.harness.migrations.timescaledb.CreateAggregatedBillingTable;
import io.harness.migrations.timescaledb.CreateAnomaliesData;
import io.harness.migrations.timescaledb.CreateBillingData;
import io.harness.migrations.timescaledb.CreateBillingDataHourly;
import io.harness.migrations.timescaledb.CreateBudgetAlerts;
import io.harness.migrations.timescaledb.CreateDeploymentParentTable;
import io.harness.migrations.timescaledb.CreateDeploymentStageTable;
import io.harness.migrations.timescaledb.CreateInstanceStatsDayTable;
import io.harness.migrations.timescaledb.CreateInstanceStatsHourTable;
import io.harness.migrations.timescaledb.CreateKubernetesUtilizationData;
import io.harness.migrations.timescaledb.CreateNewInstanceV2Migration;
import io.harness.migrations.timescaledb.CreatePreAggHourlyTable;
import io.harness.migrations.timescaledb.CreateUtilizationData;
import io.harness.migrations.timescaledb.DeploymentAdditionalColumns;
import io.harness.migrations.timescaledb.InitSchemaMigration;
import io.harness.migrations.timescaledb.InitTriggerFunctions;
import io.harness.migrations.timescaledb.InitVerificationSchemaMigration;
import io.harness.migrations.timescaledb.RenameInstanceMigration;
import io.harness.migrations.timescaledb.UniqueIndexCEUtilizationDataTables;
import io.harness.migrations.timescaledb.UpdateServiceGuardSchema;
import io.harness.migrations.timescaledb.data.CreateAnomaliesDataV2;
import io.harness.migrations.timescaledb.data.CreatePodCountTable;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;

@UtilityClass
@TargetModule(Module._390_DB_MIGRATION)
public class TimescaleDBMigrationList {
  public static List<Pair<Integer, Class<? extends TimeScaleDBMigration>>> getMigrations() {
    return new ImmutableList.Builder<Pair<Integer, Class<? extends TimeScaleDBMigration>>>()
        .add(Pair.of(1, InitSchemaMigration.class))
        .add(Pair.of(2, InitVerificationSchemaMigration.class))
        .add(Pair.of(3, RenameInstanceMigration.class))
        .add(Pair.of(4, DeploymentAdditionalColumns.class))
        .add(Pair.of(5, ChangeToTimeStampTZ.class))
        .add(Pair.of(6, CreateNewInstanceV2Migration.class))
        .add(Pair.of(7, AddIndexToInstanceV2Migration.class))
        .add(Pair.of(8, AddRollbackToDeployment.class))
        .add(Pair.of(9, AddingToCVDeploymentMetrics.class))
        .add(Pair.of(10, AddSchemaForServiceGuardStats.class))
        .add(Pair.of(11, AddInstancesDeployedToDeployment.class))
        .add(Pair.of(12, UpdateServiceGuardSchema.class))
        .add(Pair.of(13, AddFieldsToWorkflowCVMetrics.class))
        .add(Pair.of(14, AddFieldsToServiceGuardStats.class))
        .add(Pair.of(15, CreateBillingData.class))
        .add(Pair.of(16, CreateKubernetesUtilizationData.class))
        .add(Pair.of(17, CreateUtilizationData.class))
        .add(Pair.of(18, AlterCEUtilizationDataTables.class))
        .add(Pair.of(19, UniqueIndexCEUtilizationDataTables.class))
        .add(Pair.of(20, AddSystemCostBillingData.class))
        .add(Pair.of(21, AddCostEvents.class))
        .add(Pair.of(22, AddDeploymentTagsToDeployment.class))
        .add(Pair.of(23, AddIdleUnallocatedColumns.class))
        .add(Pair.of(24, AddMaxUtilColumns.class))
        .add(Pair.of(25, CreateBillingDataHourly.class))
        .add(Pair.of(26, AddRequestColumnToBillingData.class))
        .add(Pair.of(27, AddPercentagesToCostEvents.class))
        .add(Pair.of(28, AddIndicesForCostEvents.class))
        .add(Pair.of(29, AddNonComputeCostColumnToBillingData.class))
        .add(Pair.of(30, CreateBudgetAlerts.class))
        .add(Pair.of(31, CreatePodCountTable.class))
        .add(Pair.of(32, CreateAnomaliesData.class))
        .add(Pair.of(33, AddExplorerV2Indices.class))
        .add(Pair.of(34, CreateAggregatedBillingTable.class))
        .add(Pair.of(35, CreateAnomaliesDataV2.class))
        .add(Pair.of(36, AddAccountIdStatusIndexToDeployment.class))
        .add(Pair.of(37, AddFeedbackToAnomalies.class))
        .add(Pair.of(38, AddStorageSupportK8sUtilTable.class))
        .add(Pair.of(39, AddSlackNotificationSupportAnomalies.class))
        .add(Pair.of(40, CreatePreAggHourlyTable.class))
        .add(Pair.of(41, InitTriggerFunctions.class))
        .add(Pair.of(42, CreateInstanceStatsHourTable.class))
        .add(Pair.of(43, CreateInstanceStatsDayTable.class))
        .add(Pair.of(44, CreateDeploymentParentTable.class))
        .add(Pair.of(45, CreateDeploymentStageTable.class))
        .add(Pair.of(46, AddAlertTypeColumnToBudgetAlerts.class))
        .build();
  }
}
