package itmo.cityservice.controller;

import itmo.cityservice.ejb.business.CityBusinessServiceRemote;
import itmo.cityservice.ejb.model.dto.*;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/cities")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class CityController {

    @EJB
    private CityBusinessServiceRemote cityService;

    @GET
    public Response getCities(
            @QueryParam("sort") List<String> sort,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize,
            @QueryParam("filter") String filter) {

        try {
            CitiesResponseDto response = cityService.getCities(sort, page, pageSize, filter);
            return Response.ok(response).build();
        } catch (itmo.cityservice.ejb.exception.BadRequestException e) {
            ErrorResponse error = new ErrorResponse("Ошибка в url запроса");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getCityById(@PathParam("id") Long id) {
        try {
            CityDto city = cityService.getCityById(id);
            return Response.ok(city).build();
        } catch (itmo.cityservice.ejb.exception.NotFoundException e) {
            ErrorResponse error = new ErrorResponse("Объект не найден");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        } catch (itmo.cityservice.ejb.exception.BadRequestException e) {
            ErrorResponse error = new ErrorResponse("Ошибка в url запроса");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @POST
    public Response createCity(CityCreateRequestDto cityDto) {
        try {
            CityDto createdCity = cityService.createCity(cityDto);
            return Response.status(Response.Status.CREATED).entity(createdCity).build();
        } catch (itmo.cityservice.ejb.exception.ValidationException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } catch (itmo.cityservice.ejb.exception.BadRequestException e) {
            ErrorResponse error = new ErrorResponse(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateCity(@PathParam("id") Long id, CityCreateRequestDto dto) {
        try {
            CityDto updatedCity = cityService.updateCity(id, dto);
            return Response.ok(updatedCity).build();
        } catch (itmo.cityservice.ejb.exception.NotFoundException e) {
            ErrorResponse error = new ErrorResponse("Объект не найден");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        } catch (itmo.cityservice.ejb.exception.ValidationException e) {
            ErrorResponse error = new ErrorResponse("Ошибка валидации тела запроса");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } catch (itmo.cityservice.ejb.exception.BadRequestException e) {
            ErrorResponse error = new ErrorResponse("Ошибка в url запроса");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCity(@PathParam("id") Long id) {
        try {
            cityService.deleteCity(id);
            return Response.noContent().build();
        } catch (itmo.cityservice.ejb.exception.NotFoundException e) {
            ErrorResponse error = new ErrorResponse("Объект не найден");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        } catch (itmo.cityservice.ejb.exception.BadRequestException e) {
            ErrorResponse error = new ErrorResponse("Ошибка в url запроса");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @DELETE
    @Path("/sea-level")
    public Response deleteCitiesBySeaLevel(@QueryParam("metersAboveSeaLevel") Double metersAboveSeaLevel) {
        try {
            DeleteResultDto result = cityService.deleteCitiesBySeaLevel(metersAboveSeaLevel);
            return Response.ok(result).build();
        } catch (itmo.cityservice.ejb.exception.BadRequestException e) {
            ErrorResponse error = new ErrorResponse("Ошибка в url запроса");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    @GET
    @Path("/car-code/avg")
    public Response getAverageCarCode() {
        try {
            AverageResultDto result = cityService.getAverageCarCode();
            return Response.ok(result).build();
        } catch (itmo.cityservice.ejb.exception.NotFoundException e) {
            ErrorResponse error = new ErrorResponse("Объект не найден");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }

    @GET
    @Path("/name/min")
    public Response getCityWithMinName() {
        try {
            CityDto city = cityService.getCityWithMinName();
            return Response.ok(city).build();
        } catch (itmo.cityservice.ejb.exception.NotFoundException e) {
            ErrorResponse error = new ErrorResponse("Объект не найден");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
    }

    @GET
    @Path("/health")
    @Produces(MediaType.APPLICATION_XML)
    public Response healthCheck() {
        try {
            return Response.ok("<health><status>UP</status><service>city-service</service></health>")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("<health><status>DOWN</status><service>city-service</service></health>")
                    .build();
        }
    }

}
