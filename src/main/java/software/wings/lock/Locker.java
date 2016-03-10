package software.wings.lock;

import java.util.Date;

public interface Locker {
  public boolean acquireLock(Class entityClass, String entityId);
  public boolean acquireLock(Class entityClass, String entityId, Date expiryDate);
  public boolean acquireLock(String entityType, String entityId);
  public boolean acquireLock(String entityType, String entityId, Date expiryDate);

  public boolean releaseLock(Class entityClass, String entityId);
  public boolean releaseLock(String entityType, String entityId);
}
