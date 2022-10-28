package io.harness.exception;

import static io.harness.annotations.dev.HarnessTeam.PL;

import io.harness.annotations.dev.OwnedBy;
import io.harness.eraro.ErrorCode;
import io.harness.eraro.ResponseMessage;

import com.mongodb.MongoExecutionTimeoutException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@OwnedBy(PL)
public class MongoExecutionTimeoutExceptionMapper implements ExceptionMapper<MongoExecutionTimeoutException> {
  private static final String ERROR_MSG = "Operation exceeded time limit. Please contact Harness Support.";

  @Override
  public Response toResponse(MongoExecutionTimeoutException exception) {
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(ResponseMessage.builder().message(ERROR_MSG).code(ErrorCode.MONGO_EXECUTION_TIMEOUT_EXCEPTION).build())
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}