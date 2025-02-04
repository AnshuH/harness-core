/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.cvng.cdng.beans.v2;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonTypeName("DEPLOYMENT_NODE")
public class AnalysedDeploymentNode extends AbstractAnalysedNode {
  String nodeIdentifier;
  VerificationResult verificationResult;
  Long failedMetrics;
  Long failedLogClusters;
  Long failedErrorClusters;
}
