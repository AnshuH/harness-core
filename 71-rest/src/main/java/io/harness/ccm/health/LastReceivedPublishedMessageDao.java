package io.harness.ccm.health;

import static io.harness.persistence.HPersistence.upsertReturnNewOptions;
import static io.harness.persistence.HQuery.excludeAuthority;

import com.google.inject.Inject;

import io.harness.ccm.cluster.entities.LastReceivedPublishedMessage;
import io.harness.ccm.cluster.entities.LastReceivedPublishedMessage.LastReceivedPublishedMessageKeys;
import io.harness.ccm.commons.entities.LatestClusterInfo;
import io.harness.ccm.commons.entities.LatestClusterInfo.LatestClusterInfoKeys;
import io.harness.persistence.HPersistence;
import lombok.extern.slf4j.Slf4j;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.Sort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
public class LastReceivedPublishedMessageDao {
  private final HPersistence hPersistence;

  @Inject
  public LastReceivedPublishedMessageDao(HPersistence hPersistence) {
    this.hPersistence = hPersistence;
  }

  public boolean saveLatestClusterInfo(LatestClusterInfo latestClusterInfo) {
    return hPersistence.save(latestClusterInfo) != null;
  }

  public List<LatestClusterInfo> fetchLatestClusterInfo() {
    return hPersistence.createQuery(LatestClusterInfo.class, excludeAuthority).asList();
  }

  public boolean deleteLatestClusterInfo(LatestClusterInfo latestClusterInfo) {
    Query query = hPersistence.createQuery(LatestClusterInfo.class)
                      .field(LatestClusterInfoKeys.uuid)
                      .equal(latestClusterInfo.getUuid());
    return hPersistence.delete(query);
  }

  public LastReceivedPublishedMessage upsert(String accountId, String identifier) {
    return hPersistence.upsert(hPersistence.createQuery(LastReceivedPublishedMessage.class)
                                   .field(LastReceivedPublishedMessageKeys.accountId)
                                   .equal(accountId)
                                   .field(LastReceivedPublishedMessageKeys.identifier)
                                   .equal(identifier),
        hPersistence.createUpdateOperations(LastReceivedPublishedMessage.class)
            .set(LastReceivedPublishedMessageKeys.accountId, accountId)
            .set(LastReceivedPublishedMessageKeys.identifier, identifier)
            .set(LastReceivedPublishedMessageKeys.lastReceivedAt, Instant.now().toEpochMilli()),
        upsertReturnNewOptions);
  }

  public LastReceivedPublishedMessage get(String accountId, String identifier) {
    return hPersistence.createQuery(LastReceivedPublishedMessage.class)
        .field(LastReceivedPublishedMessageKeys.accountId)
        .equal(accountId)
        .field(LastReceivedPublishedMessageKeys.identifier)
        .equal(identifier)
        .get();
  }

  public LastReceivedPublishedMessage get(String accountId) {
    return hPersistence.createQuery(LastReceivedPublishedMessage.class)
        .field(LastReceivedPublishedMessageKeys.accountId)
        .equal(accountId)
        .get();
  }

  public Instant getFirstEventReceivedTime(String accountId) {
    Query<LastReceivedPublishedMessage> filteredQuery =
        hPersistence.createQuery(LastReceivedPublishedMessage.class)
            .filter(LastReceivedPublishedMessageKeys.accountId, accountId)
            .order(Sort.ascending(LastReceivedPublishedMessageKeys.createdAt));
    LastReceivedPublishedMessage lastReceivedPublishedMessage = filteredQuery.get();
    if (null != lastReceivedPublishedMessage) {
      log.info("First event received time {}", lastReceivedPublishedMessage.getCreatedAt());
      return Instant.ofEpochMilli(lastReceivedPublishedMessage.getCreatedAt()).truncatedTo(ChronoUnit.DAYS);
    }
    return null;
  }
}
