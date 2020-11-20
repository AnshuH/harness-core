package io.harness.connector.mappers.docker;

import static io.harness.encryption.Scope.ACCOUNT;
import static org.assertj.core.api.Assertions.assertThat;

import io.harness.CategoryTest;
import io.harness.category.element.UnitTests;
import io.harness.connector.entities.embedded.docker.DockerConnector;
import io.harness.connector.entities.embedded.docker.DockerUserNamePasswordAuthentication;
import io.harness.connector.mappers.SecretRefHelper;
import io.harness.delegate.beans.connector.docker.DockerAuthType;
import io.harness.delegate.beans.connector.docker.DockerAuthenticationDTO;
import io.harness.delegate.beans.connector.docker.DockerConnectorDTO;
import io.harness.delegate.beans.connector.docker.DockerUserNamePasswordDTO;
import io.harness.rule.Owner;
import io.harness.rule.OwnerRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class DockerEntityToDTOTest extends CategoryTest {
  @InjectMocks DockerEntityToDTO dockerEntityToDTO;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @Owner(developers = OwnerRule.DEEPAK)
  @Category(UnitTests.class)
  public void createConnectorDTOTest() {
    String dockerRegistryUrl = "url";
    String dockerUserName = "dockerUserName";
    String passwordRef = ACCOUNT.getYamlRepresentation() + ".passwordRef";

    DockerConnector dockerConnector = DockerConnector.builder()
                                          .authType(DockerAuthType.USER_PASSWORD)
                                          .url(dockerRegistryUrl)
                                          .dockerAuthentication(DockerUserNamePasswordAuthentication.builder()
                                                                    .username(dockerUserName)
                                                                    .passwordRef(passwordRef)
                                                                    .build())
                                          .build();
    DockerConnectorDTO dockerConnectorDTO = dockerEntityToDTO.createConnectorDTO(dockerConnector);
    assertThat(dockerConnectorDTO).isNotNull();
    assertThat(dockerConnectorDTO.getDockerRegistryUrl()).isEqualTo(dockerRegistryUrl);
    DockerAuthenticationDTO dockerAuthenticationDTO = dockerConnectorDTO.getAuth();
    assertThat(dockerAuthenticationDTO).isNotNull();
    assertThat(dockerAuthenticationDTO.getAuthType()).isEqualTo(DockerAuthType.USER_PASSWORD);
    DockerUserNamePasswordDTO dockerAuthCredentialsDTO =
        (DockerUserNamePasswordDTO) dockerAuthenticationDTO.getCredentials();
    assertThat(dockerAuthCredentialsDTO).isNotNull();
    assertThat(dockerAuthCredentialsDTO.getUsername()).isEqualTo(dockerUserName);
    assertThat(dockerAuthCredentialsDTO.getPasswordRef()).isEqualTo(SecretRefHelper.createSecretRef(passwordRef));
  }
}
