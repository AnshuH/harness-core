/**
 *
 */

package software.wings.sm.states;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.wings.beans.Service;
import software.wings.common.UUIDGenerator;
import software.wings.sm.ContextElement;
import software.wings.sm.ContextElementType;
import software.wings.sm.ExecutionContextImpl;
import software.wings.sm.ExecutionResponse;
import software.wings.sm.SpawningExecutionResponse;
import software.wings.sm.StateExecutionInstance;
import software.wings.sm.states.RepeatState.RepeatStateExecutionData;
import software.wings.sm.states.RepeatState.RepeatStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rishi
 */
public class RepeatStateTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void shouldExecuteSerial() {
    String stateName = "test";

    List<ContextElement> repeatElements = getTestRepeatElements();

    ExecutionContextImpl context = prepareExecutionContext(stateName, repeatElements);
    RepeatState repeatState = new RepeatState(stateName);
    repeatState.setRepeatElementExpression("services()");
    repeatState.setRepeatElementType(ContextElementType.SERVICE);
    repeatState.setRepeatStrategy(RepeatStrategy.SERIAL);

    ExecutionResponse response = repeatState.execute(context, null);

    assertResponse(repeatElements, response, 1);
    RepeatState.RepeatStateExecutionData stateExecutionData =
        (RepeatStateExecutionData) response.getStateExecutionData();

    assertThat(stateExecutionData.getRepeatElementIndex()).isNotNull();
    assertThat(stateExecutionData.getRepeatElementIndex()).isEqualTo(0);

    assertThat(response).isInstanceOf(SpawningExecutionResponse.class);
    assertThat(((SpawningExecutionResponse) response).getStateExecutionInstanceList()).isNotNull();
    assertThat(((SpawningExecutionResponse) response).getStateExecutionInstanceList().size()).isEqualTo(1);
  }

  @Test
  public void shouldExecuteParallel() {
    List<ContextElement> repeatElements = getTestRepeatElements();
    String stateName = "test";

    ExecutionContextImpl context = prepareExecutionContext(stateName, repeatElements);

    RepeatState repeatState = new RepeatState(stateName);
    repeatState.setRepeatElementExpression("services()");
    repeatState.setRepeatElementType(ContextElementType.SERVICE);
    repeatState.setRepeatStrategy(RepeatStrategy.PARALLEL);

    ExecutionResponse response = repeatState.execute(context, null);

    assertResponse(repeatElements, response, 2);
    RepeatState.RepeatStateExecutionData stateExecutionData =
        (RepeatStateExecutionData) response.getStateExecutionData();

    assertThat(stateExecutionData.getRepeatElementIndex()).isNull();

    assertThat(response).isInstanceOf(SpawningExecutionResponse.class);
    assertThat(((SpawningExecutionResponse) response).getStateExecutionInstanceList()).isNotNull();
    assertThat(((SpawningExecutionResponse) response).getStateExecutionInstanceList().size()).isEqualTo(2);
  }

  private void assertResponse(List<ContextElement> repeatElements, ExecutionResponse response, int corrIdsExpected) {
    assertThat(response).isNotNull();
    assertThat(response.isAsynch()).as("Asynch Execution").isEqualTo(true);
    assertThat(response.getStateExecutionData()).isNotNull();
    assertThat(response.getStateExecutionData()).isInstanceOf(RepeatState.RepeatStateExecutionData.class);
    RepeatState.RepeatStateExecutionData stateExecutionData =
        (RepeatStateExecutionData) response.getStateExecutionData();

    assertThat(stateExecutionData.getRepeatElements()).isNotNull();
    assertThat(stateExecutionData.getRepeatElements()).isEqualTo(repeatElements);

    assertThat(response.getCorrelationIds()).isNotNull();
    assertThat(response.getCorrelationIds().size()).as("correlationIds").isEqualTo(corrIdsExpected);

    logger.debug("correlationIds: " + response.getCorrelationIds());
  }

  private ExecutionContextImpl prepareExecutionContext(String stateName, List<ContextElement> repeatElements) {
    StateExecutionInstance stateExecutionInstance = new StateExecutionInstance();
    stateExecutionInstance.setUuid(UUIDGenerator.getUuid());
    stateExecutionInstance.setStateName(stateName);

    ExecutionContextImpl context = mock(ExecutionContextImpl.class);
    when(context.evaluateExpression("services()")).thenReturn(repeatElements);
    when(context.getStateExecutionInstance()).thenReturn(stateExecutionInstance);
    return context;
  }

  private List<ContextElement> getTestRepeatElements() {
    List<ContextElement> repeatElements = new ArrayList<>();
    Service ui = new Service();
    ui.setName("ui");
    repeatElements.add(ui);

    Service svr = new Service();
    svr.setName("server");
    repeatElements.add(svr);
    return repeatElements;
  }
}
