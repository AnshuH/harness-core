package software.wings.graphql.datafetcher.user;

import static io.harness.rule.OwnerRule.VARDAN_BANSAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableMap;

import graphql.schema.DataFetchingEnvironment;
import io.harness.CategoryTest;
import io.harness.category.element.UnitTests;
import io.harness.rule.Owner;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import software.wings.beans.User;
import software.wings.graphql.datafetcher.AuthRuleGraphQL;
import software.wings.graphql.datafetcher.BaseDataFetcher;
import software.wings.graphql.datafetcher.DataFetcherUtils;
import software.wings.graphql.datafetcher.MutationContext;
import software.wings.graphql.datafetcher.userGroup.UserGroupController;
import software.wings.graphql.schema.type.QLUser;
import software.wings.graphql.schema.type.user.QLCreateUserInput;
import software.wings.graphql.schema.type.user.QLCreateUserPayload;
import software.wings.service.intfc.UserService;

import java.util.Arrays;

public class CreateUserDataFetcherTest extends CategoryTest {
  @Mock AuthRuleGraphQL authRuleInstrumentation;
  @Mock DataFetcherUtils utils;
  @Mock UserService userService;
  @Mock UserGroupController userGroupController;
  @InjectMocks @Spy CreateUserDataFetcher createUserDataFetcher = new CreateUserDataFetcher(userService);

  @Before

  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @Owner(developers = VARDAN_BANSAL)
  @Category(UnitTests.class)
  public void test_createUser() throws Exception {
    final DataFetchingEnvironment dataFetchingEnvironment = Mockito.mock(DataFetchingEnvironment.class);
    doReturn(ImmutableMap.of("clientMutationId", "req1", "name", "userName", "email", "userEmail", "userGroupIds",
                 Arrays.asList("userGroupId1")))
        .when(dataFetchingEnvironment)
        .getArguments();
    doReturn("accountId1").when(utils).getAccountId(dataFetchingEnvironment);
    final User savedUser = User.Builder.anUser().name("userName").email("userEmail").build();
    doReturn(savedUser).when(userService).save(any(User.class), any());

    final QLCreateUserPayload qlCreateUserPayload = createUserDataFetcher.get(dataFetchingEnvironment);
    final QLUser user = qlCreateUserPayload.getUser();
    assertThat(qlCreateUserPayload.getClientMutationId()).isEqualTo("req1");
    verify(userService, times(1)).getUserByEmail(eq("userEmail"), eq("accountId1"));
    verify(authRuleInstrumentation, times(1))
        .instrumentDataFetcher(any(BaseDataFetcher.class), eq(dataFetchingEnvironment), eq(QLCreateUserPayload.class));

    verify(authRuleInstrumentation, times(1))
        .handlePostMutation(any(MutationContext.class), any(QLCreateUserInput.class), any(QLCreateUserPayload.class));

    if (user != null) {
      assertThat(user.getName()).isEqualTo(savedUser.getName());
      assertThat(user.getEmail()).isEqualTo(savedUser.getEmail());
    }
  }
}
