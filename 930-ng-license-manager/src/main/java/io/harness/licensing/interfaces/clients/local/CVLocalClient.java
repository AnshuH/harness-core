package io.harness.licensing.interfaces.clients.local;

import static io.harness.licensing.interfaces.ModuleLicenseImpl.TRIAL_DURATION;

import io.harness.exception.UnsupportedOperationException;
import io.harness.licensing.Edition;
import io.harness.licensing.LicenseStatus;
import io.harness.licensing.LicenseType;
import io.harness.licensing.beans.modules.CVModuleLicenseDTO;
import io.harness.licensing.beans.modules.CVModuleLicenseDTO.CVModuleLicenseDTOBuilder;
import io.harness.licensing.interfaces.clients.CVModuleLicenseClient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CVLocalClient implements CVModuleLicenseClient {
  @Override
  public CVModuleLicenseDTO createTrialLicense(Edition edition, String accountId) {
    long expiryTime = Instant.now().plus(TRIAL_DURATION, ChronoUnit.DAYS).toEpochMilli();
    long currentTime = Instant.now().toEpochMilli();

    CVModuleLicenseDTOBuilder<?, ?> builder = CVModuleLicenseDTO.builder()
                                                  .startTime(currentTime)
                                                  .expiryTime(expiryTime)
                                                  .selfService(true)
                                                  .status(LicenseStatus.ACTIVE);

    switch (edition) {
      case ENTERPRISE:
        return builder.licenseType(LicenseType.TRIAL).build();
      case TEAM:
        return builder.licenseType(LicenseType.TRIAL).build();
      case FREE:
        return builder.expiryTime(Long.MAX_VALUE).build();
      default:
        throw new UnsupportedOperationException("Requested edition is not supported");
    }
  }
}