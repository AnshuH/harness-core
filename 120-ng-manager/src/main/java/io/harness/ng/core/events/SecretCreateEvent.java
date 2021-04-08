package io.harness.ng.core.events;

import static io.harness.annotations.dev.HarnessTeam.PL;
import static io.harness.audit.ResourceTypeConstants.SECRET;
import static io.harness.data.structure.EmptyPredicate.isEmpty;
import static io.harness.data.structure.EmptyPredicate.isNotEmpty;

import io.harness.annotations.dev.OwnedBy;
import io.harness.event.Event;
import io.harness.ng.core.AccountScope;
import io.harness.ng.core.OrgScope;
import io.harness.ng.core.ProjectScope;
import io.harness.ng.core.Resource;
import io.harness.ng.core.ResourceScope;
import io.harness.ng.core.dto.secrets.SecretDTOV2;

import lombok.Getter;
import lombok.NoArgsConstructor;

@OwnedBy(PL)
@Getter
@NoArgsConstructor
public class SecretCreateEvent implements Event {
  private SecretDTOV2 secret;
  private String accountIdentifier;

  public SecretCreateEvent(String accountIdentifier, SecretDTOV2 secret) {
    this.secret = secret;
    this.accountIdentifier = accountIdentifier;
  }

  public ResourceScope getResourceScope() {
    if (isNotEmpty(secret.getOrgIdentifier())) {
      if (isEmpty(secret.getProjectIdentifier())) {
        return new OrgScope(accountIdentifier, secret.getOrgIdentifier());
      } else {
        return new ProjectScope(accountIdentifier, secret.getOrgIdentifier(), secret.getProjectIdentifier());
      }
    }
    return new AccountScope(accountIdentifier);
  }

  public Resource getResource() {
    return Resource.builder().identifier(secret.getIdentifier()).type(SECRET).build();
  }

  public String getEventType() {
    return "SecretCreated";
  }
}
