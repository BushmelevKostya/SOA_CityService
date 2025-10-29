package itmo.cityservice.controller;

import itmo.cityservice.api.UtilsApi;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-16T12:00:39.661908700+03:00[Europe/Moscow]")
@Controller
@RequestMapping("${openapi.cityCollection.base-path:/api/v1}")
public class UtilsApiController implements UtilsApi {

    private final NativeWebRequest request;

    @Autowired
    public UtilsApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
