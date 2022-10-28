/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.delegate.beans.connector.azureconnector.outcome;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.delegate.beans.connector.azureconnector.AzureManagedIdentityType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@OwnedBy(HarnessTeam.CDP)
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AzureMSIAuthUAOutcomeDTO implements AzureMSIAuthOutcomeDTO {
  @NotNull AzureManagedIdentityType type;

  @NotNull AzureUserAssignedMSIAuthOutcomeDTO spec;

  @Builder
  public AzureMSIAuthUAOutcomeDTO(AzureManagedIdentityType type, AzureUserAssignedMSIAuthOutcomeDTO spec) {
    this.type = type;
    this.spec = spec;
  }
}