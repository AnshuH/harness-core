package io.harness.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.wings.beans.alert.Alert;
import software.wings.beans.alert.AlertFilter;
import software.wings.beans.alert.AlertNotificationRule;

public class AlertNotificationRuleCheckerImpl implements AlertNotificationRuleChecker {
  private static final Logger log = LoggerFactory.getLogger(AlertNotificationRuleCheckerImpl.class);

  @Override
  public boolean doesAlertSatisfyRule(Alert alert, AlertNotificationRule rule) {
    if (rule.getAlertCategory() != alert.getCategory()) {
      log.debug("Alert category and rule category don't match. Alert does not satisfy rule. Alert: {}, Rule: {}", alert,
          rule);
      return false;
    }

    if (null == rule.getAlertFilter()) {
      log.error(
          "[Missing Alert Filter] Alert matched the rule because rule category and alert category are same and no additional filters are present. Rule: {}, Alert: {}",
          alert, rule);
      return rule.getAlertCategory() == alert.getCategory();
    }

    AlertFilter filter = rule.getAlertFilter();
    return alertSatisfiesFilter(alert, filter);
  }

  private boolean alertSatisfiesFilter(Alert alert, AlertFilter filter) {
    // Matcher should be based on type on alert. Basic Filter Matcher will just compare alert type in alert with
    // alertType in rule. But for certain alerts like AlertType.SSOSyncFailedAlert , we might need custom checker which
    // will check that `ssoId` in rule is same as `ssoId` in alert.
    FilterMatcher matcher = new BasicFilterMatcher(filter, alert);
    return matcher.matchesCondition();
  }
}
