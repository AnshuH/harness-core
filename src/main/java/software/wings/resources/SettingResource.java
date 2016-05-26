package software.wings.resources;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static software.wings.beans.SearchFilter.Operator.EQ;

import com.google.inject.Inject;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import software.wings.beans.RestResponse;
import software.wings.beans.SettingAttribute;
import software.wings.dl.PageRequest;
import software.wings.dl.PageResponse;
import software.wings.service.intfc.SettingsService;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by anubhaw on 5/17/16.
 */

@Path("/settings")
@Timed
@ExceptionMetered
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class SettingResource {
  @Inject private SettingsService attributeService;

  @GET
  public RestResponse<PageResponse<SettingAttribute>> list(
      @QueryParam("appId") String appId, @BeanParam PageRequest<SettingAttribute> pageRequest) {
    pageRequest.addFilter("appId", appId, EQ);
    return new RestResponse<>(attributeService.list(pageRequest));
  }

  @POST
  public RestResponse<SettingAttribute> save(@QueryParam("appId") String appId, SettingAttribute variable) {
    variable.setAppId(appId);
    return new RestResponse<>(attributeService.save(variable));
  }

  @GET
  @Path("{attrId}")
  public RestResponse<SettingAttribute> get(@QueryParam("appId") String appId, @PathParam("attrId") String attrId) {
    return new RestResponse<>(attributeService.get(appId, appId));
  }

  @PUT
  @Path("{attrId}")
  public RestResponse<SettingAttribute> update(
      @QueryParam("appId") String appId, @PathParam("attrId") String attrId, SettingAttribute variable) {
    variable.setUuid(attrId);
    variable.setAppId(appId);
    return new RestResponse<>(attributeService.update(variable));
  }

  @DELETE
  @Path("{attrId}")
  public RestResponse delete(@QueryParam("appId") String appId, @PathParam("attrId") String attrId) {
    attributeService.delete(appId, attrId);
    return new RestResponse();
  }
}
