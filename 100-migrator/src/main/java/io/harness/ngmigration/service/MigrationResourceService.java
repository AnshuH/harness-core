/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ngmigration.service;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.ngmigration.beans.NGYamlFile;
import io.harness.ngmigration.dto.BaseImportDTO;
import io.harness.ngmigration.dto.ImportConnectorDTO;

import software.wings.ngmigration.NGMigrationEntityType;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@OwnedBy(HarnessTeam.CDC)
public class MigrationResourceService {
  @Inject private ConnectorImportService connectorImportService;

  public List<NGYamlFile> migrateCgEntityToNG(String authToken, BaseImportDTO importDTO) {
    if (NGMigrationEntityType.CONNECTOR.equals(importDTO.getType())) {
      return connectorImportService.importConnectors(authToken, (ImportConnectorDTO) importDTO);
    }
    return new ArrayList<>();
  }
}