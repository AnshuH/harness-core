package io.harness.cdng.inputset.services.impl;

import static io.harness.rule.OwnerRule.NAMAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.Inject;

import io.harness.category.element.UnitTests;
import io.harness.cdng.CDNGBaseTest;
import io.harness.cdng.inputset.beans.entities.InputSetEntity;
import io.harness.cdng.inputset.beans.resource.InputSetListType;
import io.harness.cdng.inputset.mappers.InputSetElementMapper;
import io.harness.cdng.inputset.mappers.InputSetFilterHelper;
import io.harness.exception.InvalidRequestException;
import io.harness.ngpipeline.overlayinputset.beans.BaseInputSetEntity;
import io.harness.ngpipeline.overlayinputset.beans.entities.OverlayInputSetEntity;
import io.harness.rule.Owner;
import io.harness.utils.PageUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InputSetEntityServiceImplTest extends CDNGBaseTest {
  @Inject InputSetEntityServiceImpl inputSetEntityService;

  @Test
  @Owner(developers = NAMAN)
  @Category(UnitTests.class)
  public void testValidatePresenceOfRequiredFields() {
    assertThatThrownBy(() -> inputSetEntityService.validatePresenceOfRequiredFields("", null, "2"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("One of the required fields is null.");
  }

  @Test
  @Owner(developers = NAMAN)
  @Category(UnitTests.class)
  public void testServiceLayerOnInputSet() {
    String ORG_IDENTIFIER = "orgId";
    String PROJ_IDENTIFIER = "projId";
    String PIPELINE_IDENTIFIER = "pipeline_identifier";
    String IDENTIFIER = "identifier";
    String ACCOUNT_ID = "account_id";

    InputSetEntity inputSetEntity = InputSetEntity.builder().build();
    inputSetEntity.setAccountId(ACCOUNT_ID);
    inputSetEntity.setOrgIdentifier(ORG_IDENTIFIER);
    inputSetEntity.setProjectIdentifier(PROJ_IDENTIFIER);
    inputSetEntity.setPipelineIdentifier(PIPELINE_IDENTIFIER);
    inputSetEntity.setIdentifier(IDENTIFIER);
    inputSetEntity.setName("Input Set");

    // Create
    BaseInputSetEntity createdInputSet = inputSetEntityService.create(inputSetEntity);
    assertThat(createdInputSet).isNotNull();
    assertThat(createdInputSet.getAccountId()).isEqualTo(inputSetEntity.getAccountId());
    assertThat(createdInputSet.getOrgIdentifier()).isEqualTo(inputSetEntity.getOrgIdentifier());
    assertThat(createdInputSet.getProjectIdentifier()).isEqualTo(inputSetEntity.getProjectIdentifier());
    assertThat(createdInputSet.getIdentifier()).isEqualTo(inputSetEntity.getIdentifier());
    assertThat(createdInputSet.getName()).isEqualTo(inputSetEntity.getName());

    // Get
    Optional<BaseInputSetEntity> getInputSet =
        inputSetEntityService.get(ACCOUNT_ID, ORG_IDENTIFIER, PROJ_IDENTIFIER, PIPELINE_IDENTIFIER, IDENTIFIER, false);
    assertThat(getInputSet).isPresent();
    assertThat(getInputSet.get()).isEqualTo(createdInputSet);

    // Update
    InputSetEntity updatedInputSetEntity = InputSetEntity.builder().build();
    updatedInputSetEntity.setAccountId(ACCOUNT_ID);
    updatedInputSetEntity.setOrgIdentifier(ORG_IDENTIFIER);
    updatedInputSetEntity.setProjectIdentifier(PROJ_IDENTIFIER);
    updatedInputSetEntity.setPipelineIdentifier(PIPELINE_IDENTIFIER);
    updatedInputSetEntity.setIdentifier(IDENTIFIER);
    updatedInputSetEntity.setName("Input Set Updated");

    BaseInputSetEntity updatedInputSetResponse = inputSetEntityService.update(updatedInputSetEntity);
    assertThat(updatedInputSetResponse.getAccountId()).isEqualTo(updatedInputSetEntity.getAccountId());
    assertThat(updatedInputSetResponse.getOrgIdentifier()).isEqualTo(updatedInputSetEntity.getOrgIdentifier());
    assertThat(updatedInputSetResponse.getProjectIdentifier()).isEqualTo(updatedInputSetEntity.getProjectIdentifier());
    assertThat(updatedInputSetResponse.getIdentifier()).isEqualTo(updatedInputSetEntity.getIdentifier());
    assertThat(updatedInputSetResponse.getName()).isEqualTo(updatedInputSetEntity.getName());
    assertThat(updatedInputSetResponse.getDescription()).isEqualTo(updatedInputSetEntity.getDescription());

    // Update non existing entity
    updatedInputSetEntity.setAccountId("newAccountId");
    assertThatThrownBy(() -> inputSetEntityService.update(updatedInputSetEntity))
        .isInstanceOf(InvalidRequestException.class);
    updatedInputSetEntity.setAccountId(ACCOUNT_ID);

    // Delete
    boolean delete =
        inputSetEntityService.delete(ACCOUNT_ID, ORG_IDENTIFIER, PROJ_IDENTIFIER, PIPELINE_IDENTIFIER, IDENTIFIER);
    assertThat(delete).isTrue();

    Optional<BaseInputSetEntity> deletedInputSet =
        inputSetEntityService.get(ACCOUNT_ID, ORG_IDENTIFIER, PROJ_IDENTIFIER, PIPELINE_IDENTIFIER, IDENTIFIER, false);
    assertThat(deletedInputSet.isPresent()).isFalse();
  }

  @Test
  @Owner(developers = NAMAN)
  @Category(UnitTests.class)
  public void testServiceLayerOnOverlayInputSet() {
    String ORG_IDENTIFIER = "orgId";
    String PROJ_IDENTIFIER = "projId";
    String PIPELINE_IDENTIFIER = "pipeline_identifier";
    String IDENTIFIER = "identifier";
    String ACCOUNT_ID = "account_id";

    OverlayInputSetEntity overlayInputSetEntity = OverlayInputSetEntity.builder().build();
    overlayInputSetEntity.setAccountId(ACCOUNT_ID);
    overlayInputSetEntity.setOrgIdentifier(ORG_IDENTIFIER);
    overlayInputSetEntity.setProjectIdentifier(PROJ_IDENTIFIER);
    overlayInputSetEntity.setPipelineIdentifier(PIPELINE_IDENTIFIER);
    overlayInputSetEntity.setIdentifier(IDENTIFIER);
    overlayInputSetEntity.setName("Input Set");

    // Create
    BaseInputSetEntity createdInputSet = inputSetEntityService.create(overlayInputSetEntity);
    assertThat(createdInputSet).isNotNull();
    assertThat(createdInputSet.getAccountId()).isEqualTo(overlayInputSetEntity.getAccountId());
    assertThat(createdInputSet.getOrgIdentifier()).isEqualTo(overlayInputSetEntity.getOrgIdentifier());
    assertThat(createdInputSet.getProjectIdentifier()).isEqualTo(overlayInputSetEntity.getProjectIdentifier());
    assertThat(createdInputSet.getIdentifier()).isEqualTo(overlayInputSetEntity.getIdentifier());
    assertThat(createdInputSet.getName()).isEqualTo(overlayInputSetEntity.getName());

    // Get
    Optional<BaseInputSetEntity> getInputSet =
        inputSetEntityService.get(ACCOUNT_ID, ORG_IDENTIFIER, PROJ_IDENTIFIER, PIPELINE_IDENTIFIER, IDENTIFIER, false);
    assertThat(getInputSet).isPresent();
    assertThat(getInputSet.get()).isEqualTo(createdInputSet);

    // Update
    OverlayInputSetEntity updatedOverlayInputSetEntity = OverlayInputSetEntity.builder().build();
    updatedOverlayInputSetEntity.setAccountId(ACCOUNT_ID);
    updatedOverlayInputSetEntity.setOrgIdentifier(ORG_IDENTIFIER);
    updatedOverlayInputSetEntity.setProjectIdentifier(PROJ_IDENTIFIER);
    updatedOverlayInputSetEntity.setPipelineIdentifier(PIPELINE_IDENTIFIER);
    updatedOverlayInputSetEntity.setIdentifier(IDENTIFIER);
    updatedOverlayInputSetEntity.setName("Input Set Updated");

    BaseInputSetEntity updatedInputSetResponse = inputSetEntityService.update(updatedOverlayInputSetEntity);
    assertThat(updatedInputSetResponse.getAccountId()).isEqualTo(updatedOverlayInputSetEntity.getAccountId());
    assertThat(updatedInputSetResponse.getOrgIdentifier()).isEqualTo(updatedOverlayInputSetEntity.getOrgIdentifier());
    assertThat(updatedInputSetResponse.getProjectIdentifier())
        .isEqualTo(updatedOverlayInputSetEntity.getProjectIdentifier());
    assertThat(updatedInputSetResponse.getIdentifier()).isEqualTo(updatedOverlayInputSetEntity.getIdentifier());
    assertThat(updatedInputSetResponse.getName()).isEqualTo(updatedOverlayInputSetEntity.getName());
    assertThat(updatedInputSetResponse.getDescription()).isEqualTo(updatedOverlayInputSetEntity.getDescription());

    // Update non existing entity
    updatedOverlayInputSetEntity.setAccountId("newAccountId");
    assertThatThrownBy(() -> inputSetEntityService.update(updatedOverlayInputSetEntity))
        .isInstanceOf(InvalidRequestException.class);
    updatedOverlayInputSetEntity.setAccountId(ACCOUNT_ID);

    // Delete
    boolean delete =
        inputSetEntityService.delete(ACCOUNT_ID, ORG_IDENTIFIER, PROJ_IDENTIFIER, PIPELINE_IDENTIFIER, IDENTIFIER);
    assertThat(delete).isTrue();

    Optional<BaseInputSetEntity> deletedInputSet =
        inputSetEntityService.get(ACCOUNT_ID, ORG_IDENTIFIER, PROJ_IDENTIFIER, PIPELINE_IDENTIFIER, IDENTIFIER, false);
    assertThat(deletedInputSet.isPresent()).isFalse();
  }

  @Test
  @Owner(developers = NAMAN)
  @Category(UnitTests.class)
  public void testList() {
    final String ORG_IDENTIFIER = "orgId";
    final String PROJ_IDENTIFIER = "projId";
    final String PIPELINE_IDENTIFIER = "pipeline_identifier";
    final String IDENTIFIER = "Identifier";
    final String IDENTIFIER_2 = "Identifier2";
    final String OVERLAY_IDENTIFIER = "overlayIdentifier";
    final String ACCOUNT_ID = "account_id";

    InputSetEntity inputSetEntity = InputSetEntity.builder().build();
    inputSetEntity.setAccountId(ACCOUNT_ID);
    inputSetEntity.setOrgIdentifier(ORG_IDENTIFIER);
    inputSetEntity.setProjectIdentifier(PROJ_IDENTIFIER);
    inputSetEntity.setPipelineIdentifier(PIPELINE_IDENTIFIER);
    inputSetEntity.setIdentifier(IDENTIFIER);
    inputSetEntity.setName("Input Set");

    OverlayInputSetEntity overlayInputSetEntity = OverlayInputSetEntity.builder().build();
    overlayInputSetEntity.setAccountId(ACCOUNT_ID);
    overlayInputSetEntity.setOrgIdentifier(ORG_IDENTIFIER);
    overlayInputSetEntity.setProjectIdentifier(PROJ_IDENTIFIER);
    overlayInputSetEntity.setPipelineIdentifier(PIPELINE_IDENTIFIER);
    overlayInputSetEntity.setIdentifier(OVERLAY_IDENTIFIER);
    overlayInputSetEntity.setName("Input Set");

    BaseInputSetEntity createdInputSet = inputSetEntityService.create(inputSetEntity);
    BaseInputSetEntity createdOverlayInputSet = inputSetEntityService.create(overlayInputSetEntity);

    Criteria criteriaFromFilter = InputSetFilterHelper.createCriteriaForGetList(
        ACCOUNT_ID, ORG_IDENTIFIER, PROJ_IDENTIFIER, PIPELINE_IDENTIFIER, InputSetListType.ALL, "", false);
    Pageable pageRequest = PageUtils.getPageRequest(0, 10, null);
    Page<BaseInputSetEntity> list = inputSetEntityService.list(criteriaFromFilter, pageRequest);

    assertThat(list.getContent()).isNotNull();
    assertThat(list.getContent().size()).isEqualTo(2);
    assertThat(InputSetElementMapper.writeSummaryResponseDTO(list.getContent().get(0)))
        .isEqualTo(InputSetElementMapper.writeSummaryResponseDTO(createdInputSet));
    assertThat(InputSetElementMapper.writeSummaryResponseDTO(list.getContent().get(1)))
        .isEqualTo(InputSetElementMapper.writeSummaryResponseDTO(createdOverlayInputSet));

    // Add another entity.
    InputSetEntity inputSetEntity2 = InputSetEntity.builder().build();
    inputSetEntity2.setAccountId(ACCOUNT_ID);
    inputSetEntity2.setOrgIdentifier(ORG_IDENTIFIER);
    inputSetEntity2.setProjectIdentifier(PROJ_IDENTIFIER);
    inputSetEntity2.setPipelineIdentifier(PIPELINE_IDENTIFIER);
    inputSetEntity2.setIdentifier(IDENTIFIER_2);
    inputSetEntity2.setName("Input Set");

    BaseInputSetEntity createdInputSet2 = inputSetEntityService.create(inputSetEntity2);
    List<BaseInputSetEntity> givenInputSetList = inputSetEntityService.getGivenInputSetList(ACCOUNT_ID, ORG_IDENTIFIER,
        PROJ_IDENTIFIER, PIPELINE_IDENTIFIER, Stream.of(IDENTIFIER_2, OVERLAY_IDENTIFIER).collect(Collectors.toSet()));
    assertThat(givenInputSetList).containsExactlyInAnyOrder(createdInputSet2, overlayInputSetEntity);
  }
}