package io.harness.steps.plugin;

import static io.harness.annotations.dev.HarnessTeam.PIPELINE;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import io.harness.annotation.RecasterAlias;
import io.harness.annotations.dev.OwnedBy;
import io.harness.plancreator.steps.internal.PmsAbstractStepNode;
import io.harness.steps.StepSpecTypeConstants;
import io.harness.yaml.core.StepSpecType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName(StepSpecTypeConstants.CONTAINER_STEP)
@TypeAlias("ContainerStepNode")
@OwnedBy(PIPELINE)
@RecasterAlias("io.harness.steps.plugin.ContainerStepNode")
public class ContainerStepNode extends PmsAbstractStepNode {
  @JsonProperty("type") @NotNull StepType type = StepType.Container;
  @NotNull
  @JsonProperty("spec")
  @JsonTypeInfo(use = NAME, property = "type", include = EXTERNAL_PROPERTY, visible = true)
  ContainerStepInfo containerStepInfo;
  @Override
  public String getType() {
    return StepSpecTypeConstants.CONTAINER_STEP;
  }

  @Override
  public StepSpecType getStepSpecType() {
    return containerStepInfo;
  }

  enum StepType {
    Container(StepSpecTypeConstants.CONTAINER_STEP);
    @Getter String name;
    StepType(String name) {
      this.name = name;
    }
  }
}