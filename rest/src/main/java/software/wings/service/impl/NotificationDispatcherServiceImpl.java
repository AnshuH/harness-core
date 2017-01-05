package software.wings.service.impl;

import static software.wings.common.NotificationMessageResolver.SLACK_APPROVAL_NOTIFICATION;
import static software.wings.common.NotificationMessageResolver.SLACK_APPROVAL_NOTIFICATION_STATUS;
import static software.wings.common.NotificationMessageResolver.getDecoratedNotificationMessage;

import com.google.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.wings.app.MainConfiguration;
import software.wings.beans.ApprovalNotification;
import software.wings.beans.ApprovalNotification.ApprovalStage;
import software.wings.beans.InformationNotification;
import software.wings.beans.Notification;
import software.wings.beans.NotificationChannelType;
import software.wings.beans.NotificationGroup;
import software.wings.beans.NotificationRule;
import software.wings.beans.SettingAttribute;
import software.wings.beans.SlackConfig;
import software.wings.helpers.ext.mail.EmailData;
import software.wings.service.intfc.EmailNotificationService;
import software.wings.service.intfc.NotificationDispatcherService;
import software.wings.service.intfc.NotificationSetupService;
import software.wings.service.intfc.SettingsService;
import software.wings.service.intfc.SlackNotificationService;
import software.wings.settings.SettingValue.SettingVariableTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;

/**
 * Created by rishi on 10/30/16.
 */
@Singleton
public class NotificationDispatcherServiceImpl implements NotificationDispatcherService {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Inject private NotificationSetupService notificationSetupService;
  @Inject private EmailNotificationService<EmailData> emailNotificationService;
  @Inject private SlackNotificationService slackNotificationService;
  @Inject private SettingsService settingsService;
  @Inject private MainConfiguration configuration;

  @Override
  public void dispatchNotification(Notification notification) {
    List<NotificationRule> notificationRules = notificationSetupService.listNotificationRules(notification.getAppId());
    if (notificationRules == null || notificationRules.isEmpty()) {
      logger.debug("No notification rule found for the appId: {}.. skipping dispatch for notificationID: {}",
          notification.getAppId(), notification.getUuid());
      return;
    }

    dispatchNotification(notification, notificationRules);
  }

  @Override
  public void dispatchNotification(Notification notification, List<NotificationRule> notificationRules) {
    // TODO: match the rule based on filter

    List<NotificationRule> matchingRules = notificationRules;
    for (NotificationRule notificationRule : matchingRules) {
      logger.debug("Processing notificationRule: {}", notificationRule.getUuid());
      dispatch(notification, notificationRule.getNotificationGroups());
    }
  }

  private void dispatch(Notification notification, List<NotificationGroup> notificationGroups) {
    if (notificationGroups == null) {
      return;
    }

    for (NotificationGroup notificationGroup : notificationGroups) {
      if (notificationGroup.getAddressesByChannelType() == null) {
        continue;
      }
      for (Entry<NotificationChannelType, List<String>> entry :
          notificationGroup.getAddressesByChannelType().entrySet()) {
        if (entry.getKey() == NotificationChannelType.EMAIL) {
          dispatchEmail(notification, entry.getValue());
        }
        if (entry.getKey() == NotificationChannelType.SLACK) {
          dispatchSlackMessage(notification, entry.getValue());
        }
      }
    }
  }

  private void dispatchSlackMessage(Notification notification, List<String> channels) {
    if (channels == null || channels.size() == 0) {
      return;
    }
    List<SettingAttribute> settingAttributes =
        settingsService.getGlobalSettingAttributesByType(SettingVariableTypes.SLACK.name());
    if (settingAttributes != null && settingAttributes.size() > 0) {
      SettingAttribute settingAttribute = settingAttributes.iterator().next();
      SlackConfig slackConfig = (SlackConfig) settingAttribute.getValue();

      if (notification.isActionable() && notification instanceof ApprovalNotification) {
        ApprovalNotification approvalNotification = (ApprovalNotification) notification;
        String actionUrl = configuration.getPortal().getUrl()
            + String.format(
                  configuration.getPortal().getApplicationOverviewUrlPattern(), approvalNotification.getAppId());
        Map<String, String> placeHolderData = new HashMap<>();
        placeHolderData.put("ENTITY_TYPE", approvalNotification.getEntityType().name());
        placeHolderData.put("ENTITY_NAME", approvalNotification.getEntityName());
        placeHolderData.put("ACTION_URL", actionUrl);
        String templateName;
        String notificationMessage;
        if (approvalNotification.getStage().equals(ApprovalStage.APPROVED)
            || approvalNotification.getStage().equals(ApprovalStage.REJECTED)) {
          templateName = SLACK_APPROVAL_NOTIFICATION_STATUS;
          placeHolderData.put("NOTIFICATION_STATUS", approvalNotification.getStage().name().toLowerCase());
          placeHolderData.put("USER_NAME",
              approvalNotification.getLastUpdatedBy() == null ? "Wings System"
                                                              : approvalNotification.getLastUpdatedBy().getName());
        } else {
          templateName = SLACK_APPROVAL_NOTIFICATION;
        }
        notificationMessage = getDecoratedNotificationMessage(templateName, placeHolderData);
        channels.forEach(channel
            -> slackNotificationService.sendMessage(
                slackConfig, channel, "Wings Notification Bot", notificationMessage));
      } else if (notification instanceof InformationNotification) {
        channels.forEach(channel
            -> slackNotificationService.sendMessage(slackConfig, channel, "Wings Notification Bot",
                ((InformationNotification) notification).getDisplayText()));
      }
    }
  }

  private void dispatchEmail(Notification notification, List<String> toAddress) {
    // TODO: determine the right template for the notification
    emailNotificationService.sendAsync(toAddress, null, notification.getUuid(), notification.getUuid());
  }
}
