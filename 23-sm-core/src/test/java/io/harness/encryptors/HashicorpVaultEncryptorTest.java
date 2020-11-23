package io.harness.encryptors;

import static io.harness.rule.OwnerRule.UTKARSH;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import io.harness.CategoryTest;
import io.harness.category.element.UnitTests;
import io.harness.data.structure.UUIDGenerator;
import io.harness.encryptors.clients.HashicorpVaultEncryptor;
import io.harness.exception.SecretManagementDelegateException;
import io.harness.helpers.ext.vault.VaultRestClient;
import io.harness.helpers.ext.vault.VaultRestClientFactory;
import io.harness.rule.Owner;
import io.harness.security.encryption.EncryptedRecord;
import io.harness.security.encryption.EncryptedRecordData;
import io.harness.security.encryption.EncryptionType;

import software.wings.beans.VaultConfig;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@Slf4j
@RunWith(PowerMockRunner.class)
@PrepareForTest({VaultRestClientFactory.class})
@PowerMockIgnore({"javax.security.*", "org.apache.http.conn.ssl.", "javax.net.ssl.", "javax.crypto.*"})
public class HashicorpVaultEncryptorTest extends CategoryTest {
  private HashicorpVaultEncryptor hashicorpVaultEncryptor;
  private VaultConfig vaultConfig;
  private VaultRestClient vaultRestClient;

  @Before
  public void setup() {
    vaultRestClient = mock(VaultRestClient.class);
    vaultConfig = VaultConfig.builder()
                      .uuid(UUIDGenerator.generateUuid())
                      .name(UUIDGenerator.generateUuid())
                      .accountId(UUIDGenerator.generateUuid())
                      .vaultUrl(UUIDGenerator.generateUuid())
                      .authToken(UUIDGenerator.generateUuid())
                      .encryptionType(EncryptionType.VAULT)
                      .isDefault(false)
                      .basePath(UUIDGenerator.generateUuid())
                      .secretEngineName(UUIDGenerator.generateUuid())
                      .secretEngineVersion(1)
                      .build();
    hashicorpVaultEncryptor = new HashicorpVaultEncryptor(new SimpleTimeLimiter());
    mockStatic(VaultRestClientFactory.class);
    PowerMockito.when(VaultRestClientFactory.create(vaultConfig)).thenReturn(vaultRestClient);
    PowerMockito.when(VaultRestClientFactory.getFullPath(eq(vaultConfig.getBasePath()), anyString()))
        .thenAnswer(invocationOnMock -> {
          String path = (String) invocationOnMock.getArguments()[1];
          return vaultConfig.getBasePath() + "/" + path;
        });
  }

  @Test
  @Owner(developers = UTKARSH)
  @Category(UnitTests.class)
  public void testCreateSecret() throws IOException {
    String name = UUIDGenerator.generateUuid();
    String plainText = UUIDGenerator.generateUuid();
    String fullPath = vaultConfig.getBasePath() + "/" + name;
    when(
        vaultRestClient.writeSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(), fullPath, plainText))
        .thenReturn(true);
    EncryptedRecord encryptedRecord =
        hashicorpVaultEncryptor.createSecret(vaultConfig.getAccountId(), name, plainText, vaultConfig);
    assertThat(encryptedRecord).isNotNull();
    assertThat(encryptedRecord.getEncryptionKey()).isEqualTo(name);
    assertThat(encryptedRecord.getEncryptedValue()).isEqualTo(name.toCharArray());
  }

  @Test
  @Owner(developers = UTKARSH)
  @Category(UnitTests.class)
  public void testCreateSecret_throwIOException() throws IOException {
    String name = UUIDGenerator.generateUuid();
    String plainText = UUIDGenerator.generateUuid();
    String fullPath = vaultConfig.getBasePath() + "/" + name;
    when(
        vaultRestClient.writeSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(), fullPath, plainText))
        .thenThrow(new IOException("Dummy error"));
    try {
      hashicorpVaultEncryptor.createSecret(vaultConfig.getAccountId(), name, plainText, vaultConfig);
      fail("Create Secret should fail");
    } catch (SecretManagementDelegateException e) {
      assertThat(e.getCause()).isOfAnyClassIn(IOException.class);
    }
  }

  @Test
  @Owner(developers = UTKARSH)
  @Category(UnitTests.class)
  public void testCreateSecret_throwSecretManagementDelegateException() throws IOException {
    String name = UUIDGenerator.generateUuid();
    String plainText = UUIDGenerator.generateUuid();
    String fullPath = vaultConfig.getBasePath() + "/" + name;
    when(
        vaultRestClient.writeSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(), fullPath, plainText))
        .thenReturn(false);
    try {
      hashicorpVaultEncryptor.createSecret(vaultConfig.getAccountId(), name, plainText, vaultConfig);
      fail("Create Secret should fail");
    } catch (SecretManagementDelegateException e) {
      assertThat(e.getMessage()).isEqualTo("encryption failed after 3 retries for vault secret " + name);
    }
  }

  @Test
  @Owner(developers = UTKARSH)
  @Category(UnitTests.class)
  public void testUpdateSecret() throws IOException {
    String name = UUIDGenerator.generateUuid();
    String plainText = UUIDGenerator.generateUuid();
    String fullPath = vaultConfig.getBasePath() + "/" + name;
    when(
        vaultRestClient.writeSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(), fullPath, plainText))
        .thenReturn(true);
    EncryptedRecord oldRecord = EncryptedRecordData.builder()
                                    .encryptedValue(UUIDGenerator.generateUuid().toCharArray())
                                    .encryptionKey(UUIDGenerator.generateUuid())
                                    .build();
    EncryptedRecord encryptedRecord =
        hashicorpVaultEncryptor.updateSecret(vaultConfig.getAccountId(), name, plainText, oldRecord, vaultConfig);
    assertThat(encryptedRecord).isNotNull();
    assertThat(encryptedRecord.getEncryptionKey()).isEqualTo(name);
    assertThat(encryptedRecord.getEncryptedValue()).isEqualTo(name.toCharArray());
  }

  @Test
  @Owner(developers = UTKARSH)
  @Category(UnitTests.class)
  public void testUpdateSecret_throwIOException() throws IOException {
    String name = UUIDGenerator.generateUuid();
    String plainText = UUIDGenerator.generateUuid();
    String fullPath = vaultConfig.getBasePath() + "/" + name;
    EncryptedRecord oldRecord = EncryptedRecordData.builder()
                                    .encryptedValue(UUIDGenerator.generateUuid().toCharArray())
                                    .encryptionKey(UUIDGenerator.generateUuid())
                                    .build();
    when(
        vaultRestClient.writeSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(), fullPath, plainText))
        .thenThrow(new IOException("Dummy error"));
    try {
      hashicorpVaultEncryptor.updateSecret(vaultConfig.getAccountId(), name, plainText, oldRecord, vaultConfig);
      fail("Update Secret should fail");
    } catch (SecretManagementDelegateException e) {
      assertThat(e.getCause()).isOfAnyClassIn(IOException.class);
    }
  }

  @Test
  @Owner(developers = UTKARSH)
  @Category(UnitTests.class)
  public void testUpdateSecret_throwSecretManagementDelegateException() throws IOException {
    String name = UUIDGenerator.generateUuid();
    String plainText = UUIDGenerator.generateUuid();
    String fullPath = vaultConfig.getBasePath() + "/" + name;
    EncryptedRecord oldRecord = EncryptedRecordData.builder()
                                    .encryptedValue(UUIDGenerator.generateUuid().toCharArray())
                                    .encryptionKey(UUIDGenerator.generateUuid())
                                    .build();
    when(
        vaultRestClient.writeSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(), fullPath, plainText))
        .thenReturn(false);
    try {
      hashicorpVaultEncryptor.updateSecret(vaultConfig.getAccountId(), name, plainText, oldRecord, vaultConfig);
      fail("Update Secret should fail");
    } catch (SecretManagementDelegateException e) {
      assertThat(e.getMessage()).isEqualTo("encryption failed after 3 retries for vault secret " + name);
    }
  }

  @Test
  @Owner(developers = UTKARSH)
  @Category(UnitTests.class)
  public void testRenameSecret() throws IOException {
    String name = UUIDGenerator.generateUuid();
    String plainText = UUIDGenerator.generateUuid();
    String fullPath = vaultConfig.getBasePath() + "/" + name;
    when(
        vaultRestClient.writeSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(), fullPath, plainText))
        .thenReturn(true);
    EncryptedRecord oldRecord = EncryptedRecordData.builder()
                                    .encryptedValue(UUIDGenerator.generateUuid().toCharArray())
                                    .encryptionKey(UUIDGenerator.generateUuid())
                                    .build();
    when(vaultRestClient.readSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(),
             vaultConfig.getBasePath() + "/" + oldRecord.getEncryptionKey()))
        .thenReturn(plainText);
    EncryptedRecord encryptedRecord =
        hashicorpVaultEncryptor.renameSecret(vaultConfig.getAccountId(), name, oldRecord, vaultConfig);
    assertThat(encryptedRecord).isNotNull();
    assertThat(encryptedRecord.getEncryptionKey()).isEqualTo(name);
    assertThat(encryptedRecord.getEncryptedValue()).isEqualTo(name.toCharArray());
  }

  @Test
  @Owner(developers = UTKARSH)
  @Category(UnitTests.class)
  public void testRenameSecret_throwSecretManagementDelegateException() throws IOException {
    String name = UUIDGenerator.generateUuid();
    EncryptedRecord oldRecord = EncryptedRecordData.builder()
                                    .encryptedValue(UUIDGenerator.generateUuid().toCharArray())
                                    .encryptionKey(UUIDGenerator.generateUuid())
                                    .build();
    when(vaultRestClient.readSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(),
             vaultConfig.getBasePath() + "/" + oldRecord.getEncryptionKey()))
        .thenReturn("");
    try {
      hashicorpVaultEncryptor.renameSecret(vaultConfig.getAccountId(), name, oldRecord, vaultConfig);
    } catch (SecretManagementDelegateException e) {
      assertThat(e.getMessage()).isEqualTo("encryption failed after 3 retries for vault secret " + name);
    }
  }

  @Test
  @Owner(developers = UTKARSH)
  @Category(UnitTests.class)
  public void testRenameSecret_throwIOException() throws IOException {
    String name = UUIDGenerator.generateUuid();
    EncryptedRecord oldRecord = EncryptedRecordData.builder()
                                    .encryptedValue(UUIDGenerator.generateUuid().toCharArray())
                                    .encryptionKey(UUIDGenerator.generateUuid())
                                    .build();
    when(vaultRestClient.readSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(),
             vaultConfig.getBasePath() + "/" + oldRecord.getEncryptionKey()))
        .thenThrow(new IOException("dummy error"));
    try {
      hashicorpVaultEncryptor.renameSecret(vaultConfig.getAccountId(), name, oldRecord, vaultConfig);
    } catch (SecretManagementDelegateException e) {
      assertThat(e.getCause()).isOfAnyClassIn(IOException.class);
    }
  }

  @Test
  @Owner(developers = UTKARSH)
  @Category(UnitTests.class)
  public void testFetchSecret() throws IOException {
    String plainText = "plainText";
    EncryptedRecord record =
        EncryptedRecordData.builder().path(UUIDGenerator.generateUuid() + "#" + UUIDGenerator.generateUuid()).build();
    when(vaultRestClient.readSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(), record.getPath()))
        .thenReturn(plainText);
    char[] value = hashicorpVaultEncryptor.fetchSecretValue(vaultConfig.getAccountId(), record, vaultConfig);
    assertThat(value).isEqualTo(plainText.toCharArray());
  }

  @Test
  @Owner(developers = UTKARSH)
  @Category(UnitTests.class)
  public void testFetchSecret_throwIOException() throws IOException {
    EncryptedRecord record = EncryptedRecordData.builder()
                                 .encryptedValue(UUIDGenerator.generateUuid().toCharArray())
                                 .encryptionKey(UUIDGenerator.generateUuid())
                                 .build();
    when(vaultRestClient.readSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(),
             vaultConfig.getBasePath() + "/" + record.getEncryptionKey()))
        .thenThrow(new IOException("dummy error"));
    try {
      hashicorpVaultEncryptor.fetchSecretValue(vaultConfig.getAccountId(), record, vaultConfig);
      fail("Fetch secret should throw exception");
    } catch (SecretManagementDelegateException e) {
      assertThat(e.getCause()).isOfAnyClassIn(IOException.class);
    }
  }

  @Test
  @Owner(developers = UTKARSH)
  @Category(UnitTests.class)
  public void testFetchSecret_throwSecretMangementDelegateException() throws IOException {
    EncryptedRecord record = EncryptedRecordData.builder()
                                 .encryptedValue(UUIDGenerator.generateUuid().toCharArray())
                                 .encryptionKey(UUIDGenerator.generateUuid())
                                 .build();
    when(vaultRestClient.readSecret(vaultConfig.getAuthToken(), vaultConfig.getSecretEngineName(),
             vaultConfig.getBasePath() + "/" + record.getEncryptionKey()))
        .thenReturn("");
    try {
      hashicorpVaultEncryptor.fetchSecretValue(vaultConfig.getAccountId(), record, vaultConfig);
      fail("Fetch secret should throw exception");
    } catch (SecretManagementDelegateException e) {
      assertThat(e.getMessage())
          .isEqualTo("Decryption failed after 3 retries for secret " + record.getEncryptionKey() + " or path null");
    }
  }
}
