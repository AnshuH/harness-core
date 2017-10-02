package software.wings.sm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.wings.WingsBaseTest;
import software.wings.beans.Application;
import software.wings.beans.Environment;
import software.wings.beans.Environment.Builder;
import software.wings.beans.ErrorCode;
import software.wings.common.UUIDGenerator;
import software.wings.dl.WingsPersistence;
import software.wings.exception.WingsException;

import javax.inject.Inject;

/**
 *
 */

/**
 * The type State machine execution event manager test.
 *
 * @author Rishi
 */
public class ExecutionInterruptManagerTest extends WingsBaseTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Inject private WingsPersistence wingsPersistence;
  @InjectMocks @Inject private ExecutionInterruptManager executionInterruptManager;
  @Mock private StateMachineExecutor stateMachineExecutor;

  /**
   * Should throw invalid argument for null state execution instance.
   */
  @Test
  public void shouldThrowInvalidArgumentForNullStateExecutionInstance() {
    Application app =
        wingsPersistence.saveAndGet(Application.class, Application.Builder.anApplication().withName("App1").build());
    Environment env =
        wingsPersistence.saveAndGet(Environment.class, Builder.anEnvironment().withAppId(app.getUuid()).build());
    ExecutionInterrupt executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                                .withAppId(app.getUuid())
                                                .withExecutionInterruptType(ExecutionInterruptType.PAUSE)
                                                .withEnvId(env.getUuid())
                                                .withExecutionUuid(UUIDGenerator.getUuid())
                                                .build();
    try {
      executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);
      failBecauseExceptionWasNotThrown(WingsException.class);
    } catch (WingsException exception) {
      assertThat(exception.getParams().values()).doesNotContainNull();
      assertThat(exception.getParams().values().iterator().next())
          .isInstanceOf(String.class)
          .asString()
          .startsWith("null stateExecutionInstanceId");
      assertThat(exception).hasMessage(ErrorCode.INVALID_ARGUMENT.getCode());
    }
  }

  /**
   * Should throw invalid argument for invalid state execution instance.
   */
  @Test
  public void shouldThrowInvalidArgumentForInvalidStateExecutionInstance() {
    Application app =
        wingsPersistence.saveAndGet(Application.class, Application.Builder.anApplication().withName("App1").build());
    Environment env =
        wingsPersistence.saveAndGet(Environment.class, Builder.anEnvironment().withAppId(app.getUuid()).build());
    ExecutionInterrupt executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                                .withAppId(app.getUuid())
                                                .withExecutionInterruptType(ExecutionInterruptType.PAUSE)
                                                .withEnvId(env.getUuid())
                                                .withExecutionUuid(UUIDGenerator.getUuid())
                                                .withStateExecutionInstanceId(UUIDGenerator.getUuid())
                                                .build();
    try {
      executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);
      failBecauseExceptionWasNotThrown(WingsException.class);
    } catch (WingsException exception) {
      assertThat(exception.getParams().values()).doesNotContainNull();
      assertThat(exception.getParams().values().iterator().next())
          .isInstanceOf(String.class)
          .asString()
          .startsWith("invalid stateExecutionInstanceId");
      assertThat(exception).hasMessage(ErrorCode.INVALID_ARGUMENT.getCode());
    }
  }

  /**
   * Should throw state not for resume.
   */
  @Test
  public void shouldThrowStateNotForResume() {
    Application app =
        wingsPersistence.saveAndGet(Application.class, Application.Builder.anApplication().withName("App1").build());
    Environment env =
        wingsPersistence.saveAndGet(Environment.class, Builder.anEnvironment().withAppId(app.getUuid()).build());
    StateExecutionInstance stateExecutionInstance = StateExecutionInstance.Builder.aStateExecutionInstance()
                                                        .withAppId(app.getUuid())
                                                        .withStateName("state1")
                                                        .build();
    stateExecutionInstance = wingsPersistence.saveAndGet(StateExecutionInstance.class, stateExecutionInstance);
    ExecutionInterrupt executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                                .withAppId(app.getUuid())
                                                .withExecutionInterruptType(ExecutionInterruptType.RESUME)
                                                .withEnvId(env.getUuid())
                                                .withExecutionUuid(UUIDGenerator.getUuid())
                                                .withStateExecutionInstanceId(stateExecutionInstance.getUuid())
                                                .build();
    try {
      executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);
      failBecauseExceptionWasNotThrown(WingsException.class);
    } catch (WingsException exception) {
      assertThat(exception).hasMessage(ErrorCode.STATE_NOT_FOR_RESUME.getCode());
    }
  }

  /**
   * Should throw state not for retry.
   */
  @Test
  public void shouldThrowStateNotForRetry() {
    Application app =
        wingsPersistence.saveAndGet(Application.class, Application.Builder.anApplication().withName("App1").build());
    Environment env =
        wingsPersistence.saveAndGet(Environment.class, Builder.anEnvironment().withAppId(app.getUuid()).build());
    StateExecutionInstance stateExecutionInstance = StateExecutionInstance.Builder.aStateExecutionInstance()
                                                        .withAppId(app.getUuid())
                                                        .withStateName("state1")
                                                        .build();
    stateExecutionInstance = wingsPersistence.saveAndGet(StateExecutionInstance.class, stateExecutionInstance);
    ExecutionInterrupt executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                                .withAppId(app.getUuid())
                                                .withExecutionInterruptType(ExecutionInterruptType.RETRY)
                                                .withEnvId(env.getUuid())
                                                .withExecutionUuid(UUIDGenerator.getUuid())
                                                .withStateExecutionInstanceId(stateExecutionInstance.getUuid())
                                                .build();
    try {
      executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);
      failBecauseExceptionWasNotThrown(WingsException.class);
    } catch (WingsException exception) {
      assertThat(exception).hasMessage(ErrorCode.STATE_NOT_FOR_RETRY.getCode());
    }
  }

  /**
   * Should throw state not for pause.
   */
  @Test
  public void shouldThrowStateNotForPause() {
    Application app =
        wingsPersistence.saveAndGet(Application.class, Application.Builder.anApplication().withName("App1").build());
    Environment env =
        wingsPersistence.saveAndGet(Environment.class, Builder.anEnvironment().withAppId(app.getUuid()).build());
    StateExecutionInstance stateExecutionInstance = StateExecutionInstance.Builder.aStateExecutionInstance()
                                                        .withAppId(app.getUuid())
                                                        .withStateName("state1")
                                                        .withStatus(ExecutionStatus.SUCCESS)
                                                        .build();
    stateExecutionInstance = wingsPersistence.saveAndGet(StateExecutionInstance.class, stateExecutionInstance);
    ExecutionInterrupt executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                                .withAppId(app.getUuid())
                                                .withExecutionInterruptType(ExecutionInterruptType.PAUSE)
                                                .withEnvId(env.getUuid())
                                                .withExecutionUuid(UUIDGenerator.getUuid())
                                                .withStateExecutionInstanceId(stateExecutionInstance.getUuid())
                                                .build();
    try {
      executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);
      failBecauseExceptionWasNotThrown(WingsException.class);
    } catch (WingsException exception) {
      assertThat(exception).hasMessage(ErrorCode.STATE_NOT_FOR_PAUSE.getCode());
    }
  }

  /**
   * Should throw state not for abort.
   */
  @Test
  public void shouldThrowStateNotForAbort() {
    Application app =
        wingsPersistence.saveAndGet(Application.class, Application.Builder.anApplication().withName("App1").build());
    Environment env =
        wingsPersistence.saveAndGet(Environment.class, Builder.anEnvironment().withAppId(app.getUuid()).build());
    StateExecutionInstance stateExecutionInstance = StateExecutionInstance.Builder.aStateExecutionInstance()
                                                        .withAppId(app.getUuid())
                                                        .withStateName("state1")
                                                        .withStatus(ExecutionStatus.SUCCESS)
                                                        .build();
    stateExecutionInstance = wingsPersistence.saveAndGet(StateExecutionInstance.class, stateExecutionInstance);
    ExecutionInterrupt executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                                .withAppId(app.getUuid())
                                                .withExecutionInterruptType(ExecutionInterruptType.ABORT)
                                                .withEnvId(env.getUuid())
                                                .withExecutionUuid(UUIDGenerator.getUuid())
                                                .withStateExecutionInstanceId(stateExecutionInstance.getUuid())
                                                .build();
    try {
      executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);
      failBecauseExceptionWasNotThrown(WingsException.class);
    } catch (WingsException exception) {
      assertThat(exception).hasMessage(ErrorCode.STATE_NOT_FOR_ABORT.getCode());
    }
  }

  /**
   * Should throw abort all already.
   */
  @Test
  @Ignore
  public void shouldThrowAbortAllAlready() {
    Application app =
        wingsPersistence.saveAndGet(Application.class, Application.Builder.anApplication().withName("App1").build());
    Environment env =
        wingsPersistence.saveAndGet(Environment.class, Builder.anEnvironment().withAppId(app.getUuid()).build());

    String executionUuid = UUIDGenerator.getUuid();
    ExecutionInterrupt executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                                .withAppId(app.getUuid())
                                                .withExecutionInterruptType(ExecutionInterruptType.ABORT_ALL)
                                                .withEnvId(env.getUuid())
                                                .withExecutionUuid(executionUuid)
                                                .build();
    executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);

    executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                             .withAppId(app.getUuid())
                             .withExecutionInterruptType(ExecutionInterruptType.PAUSE_ALL)
                             .withEnvId(env.getUuid())
                             .withExecutionUuid(executionUuid)
                             .build();
    try {
      executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);
      failBecauseExceptionWasNotThrown(WingsException.class);
    } catch (WingsException exception) {
      assertThat(exception).hasMessage(ErrorCode.ABORT_ALL_ALREADY.getCode());
    }
  }

  /**
   * Should throw pause all already.
   */
  @Test
  public void shouldThrowPauseAllAlready() {
    Application app =
        wingsPersistence.saveAndGet(Application.class, Application.Builder.anApplication().withName("App1").build());
    Environment env =
        wingsPersistence.saveAndGet(Environment.class, Builder.anEnvironment().withAppId(app.getUuid()).build());

    String executionUuid = UUIDGenerator.getUuid();
    ExecutionInterrupt executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                                .withAppId(app.getUuid())
                                                .withExecutionInterruptType(ExecutionInterruptType.PAUSE_ALL)
                                                .withEnvId(env.getUuid())
                                                .withExecutionUuid(executionUuid)
                                                .build();
    executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);

    executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                             .withAppId(app.getUuid())
                             .withExecutionInterruptType(ExecutionInterruptType.PAUSE_ALL)
                             .withEnvId(env.getUuid())
                             .withExecutionUuid(executionUuid)
                             .build();
    try {
      executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);
      failBecauseExceptionWasNotThrown(WingsException.class);
    } catch (WingsException exception) {
      assertThat(exception).hasMessage(ErrorCode.PAUSE_ALL_ALREADY.getCode());
    }
  }

  /**
   * Should pause all clear previous resume all.
   */
  @Test
  public void shouldPauseAllClearPreviousResumeAll() {
    Application app =
        wingsPersistence.saveAndGet(Application.class, Application.Builder.anApplication().withName("App1").build());
    Environment env =
        wingsPersistence.saveAndGet(Environment.class, Builder.anEnvironment().withAppId(app.getUuid()).build());

    String executionUuid = UUIDGenerator.getUuid();
    ExecutionInterrupt executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                                .withAppId(app.getUuid())
                                                .withExecutionInterruptType(ExecutionInterruptType.PAUSE_ALL)
                                                .withEnvId(env.getUuid())
                                                .withExecutionUuid(executionUuid)
                                                .build();
    executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);

    ExecutionInterrupt resumeAll = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                       .withAppId(app.getUuid())
                                       .withExecutionInterruptType(ExecutionInterruptType.RESUME_ALL)
                                       .withEnvId(env.getUuid())
                                       .withExecutionUuid(executionUuid)
                                       .build();
    resumeAll = executionInterruptManager.registerExecutionInterrupt(resumeAll);

    executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                             .withAppId(app.getUuid())
                             .withExecutionInterruptType(ExecutionInterruptType.PAUSE_ALL)
                             .withEnvId(env.getUuid())
                             .withExecutionUuid(executionUuid)
                             .build();
    executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);
    assertThat(wingsPersistence.get(ExecutionInterrupt.class, resumeAll.getAppId(), resumeAll.getUuid())).isNull();
  }

  /**
   * Should throw resume all already.
   */
  @Test
  public void shouldThrowResumeAllAlready() {
    Application app =
        wingsPersistence.saveAndGet(Application.class, Application.Builder.anApplication().withName("App1").build());
    Environment env =
        wingsPersistence.saveAndGet(Environment.class, Builder.anEnvironment().withAppId(app.getUuid()).build());

    String executionUuid = UUIDGenerator.getUuid();
    ExecutionInterrupt executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                                .withAppId(app.getUuid())
                                                .withExecutionInterruptType(ExecutionInterruptType.RESUME_ALL)
                                                .withEnvId(env.getUuid())
                                                .withExecutionUuid(executionUuid)
                                                .build();
    try {
      executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);
      failBecauseExceptionWasNotThrown(WingsException.class);
    } catch (WingsException exception) {
      assertThat(exception).hasMessage(ErrorCode.RESUME_ALL_ALREADY.getCode());
    }
  }

  /**
   * Should resume all clear prev pause all.
   */
  @Test
  public void shouldResumeAllClearPrevPauseAll() {
    Application app =
        wingsPersistence.saveAndGet(Application.class, Application.Builder.anApplication().withName("App1").build());
    Environment env =
        wingsPersistence.saveAndGet(Environment.class, Builder.anEnvironment().withAppId(app.getUuid()).build());

    String executionUuid = UUIDGenerator.getUuid();

    ExecutionInterrupt pauseAll = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                      .withAppId(app.getUuid())
                                      .withExecutionInterruptType(ExecutionInterruptType.PAUSE_ALL)
                                      .withEnvId(env.getUuid())
                                      .withExecutionUuid(executionUuid)
                                      .build();
    pauseAll = executionInterruptManager.registerExecutionInterrupt(pauseAll);

    ExecutionInterrupt executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                                .withAppId(app.getUuid())
                                                .withExecutionInterruptType(ExecutionInterruptType.RESUME_ALL)
                                                .withEnvId(env.getUuid())
                                                .withExecutionUuid(executionUuid)
                                                .build();
    executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);

    assertThat(wingsPersistence.get(ExecutionInterrupt.class, pauseAll.getAppId(), pauseAll.getUuid())).isNull();
  }

  /**
   * Should throw resume all already 2.
   */
  @Test
  public void shouldThrowResumeAllAlready2() {
    Application app =
        wingsPersistence.saveAndGet(Application.class, Application.Builder.anApplication().withName("App1").build());
    Environment env =
        wingsPersistence.saveAndGet(Environment.class, Builder.anEnvironment().withAppId(app.getUuid()).build());

    String executionUuid = UUIDGenerator.getUuid();

    ExecutionInterrupt pauseAll = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                      .withAppId(app.getUuid())
                                      .withExecutionInterruptType(ExecutionInterruptType.PAUSE_ALL)
                                      .withEnvId(env.getUuid())
                                      .withExecutionUuid(executionUuid)
                                      .build();
    pauseAll = executionInterruptManager.registerExecutionInterrupt(pauseAll);

    ExecutionInterrupt executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                                                .withAppId(app.getUuid())
                                                .withExecutionInterruptType(ExecutionInterruptType.RESUME_ALL)
                                                .withEnvId(env.getUuid())
                                                .withExecutionUuid(executionUuid)
                                                .build();
    executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);

    executionInterrupt = ExecutionInterrupt.Builder.aWorkflowExecutionInterrupt()
                             .withAppId(app.getUuid())
                             .withExecutionInterruptType(ExecutionInterruptType.RESUME_ALL)
                             .withEnvId(env.getUuid())
                             .withExecutionUuid(executionUuid)
                             .build();
    try {
      executionInterrupt = executionInterruptManager.registerExecutionInterrupt(executionInterrupt);
      failBecauseExceptionWasNotThrown(WingsException.class);
    } catch (WingsException exception) {
      assertThat(exception).hasMessage(ErrorCode.RESUME_ALL_ALREADY.getCode());
    }
  }
}
