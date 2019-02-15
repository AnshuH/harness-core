package software.wings.beans.trigger;

import static io.harness.beans.WorkflowType.PIPELINE;
import static io.harness.data.structure.EmptyPredicate.isNotEmpty;
import static software.wings.beans.trigger.TriggerConditionType.WEBHOOK;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.harness.beans.EmbeddedUser;
import io.harness.beans.WorkflowType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;
import software.wings.beans.Base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;

/**
 * Created by sgurubelli on 10/25/17.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity(value = "triggers")
@Indexes(@Index(options = @IndexOptions(name = "yaml", unique = true), fields = { @Field("appId")
                                                                                  , @Field("name") }))
public class Trigger extends Base {
  @NotEmpty private String name;
  private String description;
  @NotNull private TriggerCondition condition;
  private String pipelineId;
  private String pipelineName;
  private String workflowId;
  private String workflowName;
  private List<ArtifactSelection> artifactSelections = new ArrayList<>();
  @JsonIgnore @Indexed private String webHookToken;
  private WorkflowType workflowType;
  private Map<String, String> workflowVariables;
  private List<ServiceInfraWorkflow> serviceInfraWorkflows;
  private boolean excludeHostsWithSameArtifact;

  @Builder
  public Trigger(String uuid, String appId, EmbeddedUser createdBy, long createdAt, EmbeddedUser lastUpdatedBy,
      long lastUpdatedAt, String entityYamlPath, String name, String description, TriggerCondition condition,
      String pipelineId, String pipelineName, String workflowId, String workflowName,
      List<ArtifactSelection> artifactSelections, String webHookToken, WorkflowType workflowType,
      Map<String, String> workflowVariables, List<ServiceInfraWorkflow> serviceInfraWorkflows) {
    super(uuid, appId, createdBy, createdAt, lastUpdatedBy, lastUpdatedAt, entityYamlPath);
    this.name = name;
    this.description = description;
    this.condition = condition;
    this.pipelineId = pipelineId;
    this.pipelineName = pipelineName;
    this.workflowId = workflowId;
    this.workflowName = workflowName;
    this.artifactSelections = (artifactSelections == null) ? new ArrayList<>() : artifactSelections;
    this.webHookToken = webHookToken;
    this.workflowType = workflowType != null ? workflowType : PIPELINE;
    this.workflowVariables = workflowVariables;
    this.serviceInfraWorkflows = serviceInfraWorkflows;
  }

  public void setPipelineId(String pipelineId) {
    this.pipelineId = pipelineId;
    this.workflowId = pipelineId;
  }

  public void setPipelineName(String pipelineName) {
    this.pipelineName = pipelineName;
    this.workflowName = pipelineName;
  }

  public String getPipelineId() {
    if (this.pipelineId == null) {
      return this.workflowId;
    }
    return pipelineId;
  }

  public String getPipelineName() {
    if (this.pipelineName == null) {
      return this.workflowName;
    }
    return this.pipelineName;
  }

  public String getWorkflowId() {
    if (workflowId == null) {
      return pipelineId;
    }
    return workflowId;
  }

  public String getWorkflowName() {
    if (workflowName == null) {
      return pipelineName;
    }
    return workflowName;
  }

  public Map<String, String> getWorkflowVariables() {
    // TODO: This is temporary code till we migrate all the triggers
    if (condition != null && WEBHOOK.equals(condition.getConditionType())) {
      WebHookTriggerCondition webHookTriggerCondition = (WebHookTriggerCondition) condition;
      if (isNotEmpty(webHookTriggerCondition.getParameters())) {
        if (workflowVariables == null) {
          workflowVariables = new LinkedHashMap<>();
        }
        workflowVariables.putAll(webHookTriggerCondition.getParameters());
      }
    }
    return workflowVariables;
  }
}
