package software.wings.service.impl;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.mongodb.morphia.mapping.Mapper.ID_KEY;
import static software.wings.collect.CollectEvent.Builder.aCollectEvent;
import static software.wings.service.intfc.FileService.FileBucket.ARTIFACTS;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import ru.vyarus.guice.validator.group.annotation.ValidationGroups;
import software.wings.beans.Application;
import software.wings.beans.Service;
import software.wings.beans.artifact.Artifact;
import software.wings.beans.artifact.Artifact.Status;
import software.wings.beans.artifact.ArtifactFile;
import software.wings.beans.artifact.ArtifactSource;
import software.wings.collect.CollectEvent;
import software.wings.core.queue.Queue;
import software.wings.dl.PageRequest;
import software.wings.dl.PageResponse;
import software.wings.dl.WingsPersistence;
import software.wings.service.intfc.ArtifactService;
import software.wings.service.intfc.FileService;
import software.wings.utils.Validator;
import software.wings.utils.validation.Create;
import software.wings.utils.validation.Update;

import java.io.File;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.executable.ValidateOnExecution;

/**
 * The Class ArtifactServiceImpl.
 */
@Singleton
@ValidateOnExecution
public class ArtifactServiceImpl implements ArtifactService {
  private static final String DEFAULT_ARTIFACT_FILE_NAME = "ArtifactFile";

  @Inject private WingsPersistence wingsPersistence;
  @Inject private FileService fileService;
  @Inject private Queue<CollectEvent> collectQueue;

  /* (non-Javadoc)
   * @see software.wings.service.intfc.ArtifactService#list(software.wings.dl.PageRequest)
   */
  @Override
  public PageResponse<Artifact> list(PageRequest<Artifact> pageRequest) {
    return wingsPersistence.query(Artifact.class, pageRequest);
  }

  /* (non-Javadoc)
   * @see software.wings.service.intfc.ArtifactService#create(software.wings.beans.artifact.Artifact)
   */
  @Override
  @ValidationGroups(Create.class)
  public Artifact create(@Valid Artifact artifact) {
    Application application = wingsPersistence.get(Application.class, artifact.getAppId());
    Validator.notNullCheck("application", application);
    ArtifactSource artifactSource = wingsPersistence.get(ArtifactSource.class, artifact.getArtifactSourceId());
    Validator.notNullCheck("artifactSource", artifactSource);

    Validator.equalCheck(application.getUuid(), artifactSource.getAppId());

    artifact.setServices(Lists.newArrayList(artifactSource.getServices()));
    artifact.setStatus(Status.QUEUED);
    String key = wingsPersistence.save(artifact);

    Artifact savedArtifact = wingsPersistence.get(Artifact.class, artifact.getAppId(), key);
    collectQueue.send(aCollectEvent().withArtifact(savedArtifact).build());

    return savedArtifact;
  }

  /* (non-Javadoc)
   * @see software.wings.service.intfc.ArtifactService#update(software.wings.beans.artifact.Artifact)
   */
  @Override
  @ValidationGroups(Update.class)
  public Artifact update(@Valid Artifact artifact) {
    wingsPersistence.update(wingsPersistence.createQuery(Artifact.class)
                                .field("appId")
                                .equal(artifact.getAppId())
                                .field(ID_KEY)
                                .equal(artifact.getUuid()),
        wingsPersistence.createUpdateOperations(Artifact.class).set("displayName", artifact.getDisplayName()));
    return wingsPersistence.get(Artifact.class, artifact.getAppId(), artifact.getUuid());
  }

  /* (non-Javadoc)
   * @see software.wings.service.intfc.ArtifactService#updateStatus(java.lang.String, java.lang.String,
   * software.wings.beans.artifact.Artifact.Status)
   */
  @Override
  public void updateStatus(String artifactId, String appId, Artifact.Status status) {
    Query<Artifact> query =
        wingsPersistence.createQuery(Artifact.class).field(ID_KEY).equal(artifactId).field("appId").equal(appId);
    UpdateOperations<Artifact> ops = wingsPersistence.createUpdateOperations(Artifact.class).set("status", status);
    wingsPersistence.update(query, ops);
  }

  /* (non-Javadoc)
   * @see software.wings.service.intfc.ArtifactService#addArtifactFile(java.lang.String, java.lang.String,
   * java.util.List)
   */
  @Override
  public void addArtifactFile(String artifactId, String appId, List<ArtifactFile> artifactFile) {
    Query<Artifact> query =
        wingsPersistence.createQuery(Artifact.class).field(ID_KEY).equal(artifactId).field("appId").equal(appId);
    UpdateOperations<Artifact> ops =
        wingsPersistence.createUpdateOperations(Artifact.class).addAll("artifactFiles", artifactFile, false);
    wingsPersistence.update(query, ops);
  }

  /* (non-Javadoc)
   * @see software.wings.service.intfc.ArtifactService#download(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public File download(String appId, String artifactId, String serviceId) {
    Artifact artifact = wingsPersistence.get(Artifact.class, appId, artifactId);
    if (artifact == null || artifact.getStatus() != Status.READY || isEmpty(artifact.getArtifactFiles())
        || !artifact.getServices()
                .stream()
                .map(Service::getUuid)
                .filter(id -> id.equals(serviceId))
                .findFirst()
                .isPresent()) {
      return null;
    }

    ArtifactFile artifactFile = artifact.getArtifactFiles().get(0);

    File tempDir = Files.createTempDir();
    String fileName = Optional.ofNullable(artifactFile.getName()).orElse(DEFAULT_ARTIFACT_FILE_NAME);

    File file = new File(tempDir, fileName);

    fileService.download(artifactFile.getFileUuid(), file, ARTIFACTS);
    return file;
  }

  /* (non-Javadoc)
   * @see software.wings.service.intfc.ArtifactService#get(java.lang.String, java.lang.String)
   */
  @Override
  public Artifact get(String appId, String artifactId) {
    Artifact artifact = wingsPersistence.get(Artifact.class, appId, artifactId);
    return artifact;
  }

  /* (non-Javadoc)
   * @see software.wings.service.intfc.ArtifactService#softDelete(java.lang.String, java.lang.String)
   */
  @Override
  public boolean delete(String appId, String artifactId) {
    return wingsPersistence.delete(
        wingsPersistence.createQuery(Artifact.class).field("appId").equal(appId).field(ID_KEY).equal(artifactId));
  }

  @Override
  public void deleteByApplication(String appId) {
    wingsPersistence.createQuery(Artifact.class)
        .field("appId")
        .equal(appId)
        .asList()
        .forEach(artifact -> delete(appId, artifact.getUuid()));
  }
}
