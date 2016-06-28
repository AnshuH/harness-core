package software.wings.service.impl;

import static java.util.stream.Collectors.toMap;
import static org.mongodb.morphia.mapping.Mapper.ID_KEY;
import static software.wings.beans.Infra.InfraBuilder.anInfra;
import static software.wings.beans.Infra.InfraType.STATIC;
import static software.wings.beans.Tag.Builder.aTag;

import com.google.common.collect.ImmutableMap;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import software.wings.beans.Application;
import software.wings.beans.Environment;
import software.wings.beans.SearchFilter.Operator;
import software.wings.dl.PageRequest;
import software.wings.dl.PageResponse;
import software.wings.dl.WingsPersistence;
import software.wings.service.intfc.EnvironmentService;
import software.wings.service.intfc.InfraService;
import software.wings.service.intfc.ServiceInstanceService;
import software.wings.service.intfc.ServiceTemplateService;
import software.wings.service.intfc.TagService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;

// TODO: Auto-generated Javadoc

/**
 * Created by anubhaw on 4/1/16.
 */
@Singleton
public class EnvironmentServiceImpl implements EnvironmentService {
  @Inject private WingsPersistence wingsPersistence;
  @Inject private InfraService infraService;
  @Inject private ServiceTemplateService serviceTemplateService;
  @Inject private ServiceInstanceService serviceInstanceService;
  @Inject private TagService tagService;
  @Inject private ExecutorService executorService;

  /**
   * {@inheritDoc}
   */
  @Override
  public PageResponse<Environment> list(PageRequest<Environment> request) {
    return wingsPersistence.query(Environment.class, request);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, String> listForEnum(String appId) {
    PageRequest<Environment> pageRequest = new PageRequest<>();
    pageRequest.addFilter("appId", appId, Operator.EQ);
    return list(pageRequest).stream().collect(toMap(Environment::getUuid, Environment::getName));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Environment save(Environment env) {
    env = wingsPersistence.saveAndGet(Environment.class, env);

    wingsPersistence.save(aTag()
                              .withAppId(env.getAppId())
                              .withEnvId(env.getUuid())
                              .withName(env.getName())
                              .withDescription(env.getName())
                              .withRootTag(true)
                              .build());
    wingsPersistence.save(
        anInfra().withAppId(env.getAppId()).withEnvId(env.getUuid()).withInfraType(STATIC).build()); // FIXME: stopgap
                                                                                                     // for Alpha

    UpdateOperations<Application> updateOperations =
        wingsPersistence.createUpdateOperations(Application.class).add("environments", env);
    Query<Application> updateQuery =
        wingsPersistence.createQuery(Application.class).field(ID_KEY).equal(env.getAppId());
    wingsPersistence.update(updateQuery, updateOperations);
    return env;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Environment get(String appId, String envId) {
    return wingsPersistence.get(Environment.class, envId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Environment update(Environment environment) {
    wingsPersistence.updateFields(Environment.class, environment.getUuid(),
        ImmutableMap.of("name", environment.getName(), "description", environment.getDescription()));
    return wingsPersistence.get(Environment.class, environment.getUuid());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void delete(String appId, String envId) {
    wingsPersistence.delete(
        wingsPersistence.createQuery(Environment.class).field("appId").equal(appId).field(ID_KEY).equal(envId));
    executorService.submit(() -> {
      serviceInstanceService.deleteByEnv(appId, envId);
      serviceTemplateService.deleteByEnv(appId, envId);
      tagService.deleteByEnv(appId, envId);
      infraService.deleteByEnv(appId, envId);
    });
  }

  @Override
  public void deleteByAppId(String appId) {
    List<Environment> environments =
        wingsPersistence.createQuery(Environment.class).field("appId").equal(appId).asList();
    environments.forEach(environment -> delete(appId, environment.getUuid()));
  }
}
