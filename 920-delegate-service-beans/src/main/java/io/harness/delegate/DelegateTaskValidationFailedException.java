package io.harness.delegate;

import static io.harness.eraro.ErrorCode.INVALID_REQUEST;

import io.harness.eraro.ErrorCode;
import io.harness.eraro.Level;
import io.harness.exception.FailureType;
import io.harness.exception.WingsException;

import java.util.EnumSet;

public class DelegateTaskValidationFailedException extends WingsException {
  private static final String MESSAGE_ARG = "message";

  public DelegateTaskValidationFailedException(String taskId) {
    super(taskId, null, ErrorCode.DELEGATE_TASK_VALIDATION_FAILED, Level.ERROR, null,
        EnumSet.of(FailureType.VERIFICATION_FAILURE));
    super.param(MESSAGE_ARG, taskId);
  }
  public DelegateTaskValidationFailedException(String message, EnumSet<ReportTarget> reportTargets) {
    super(message, null, INVALID_REQUEST, Level.ERROR, reportTargets, null);
    super.param(MESSAGE_ARG, message);
  }
}