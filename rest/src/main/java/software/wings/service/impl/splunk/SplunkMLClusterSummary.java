package software.wings.service.impl.splunk;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by rsingh on 6/30/17.
 */

@Data
public class SplunkMLClusterSummary {
  private Map<String, SplunkMLHostSummary> hostSummary;
  private String logText;
  private List<String> tags;
}
