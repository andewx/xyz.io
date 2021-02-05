package xyz.Endpoint;

import xyz.Server.HttpCallback;
import java.util.HashMap;

//Endpoints are Server Response Callback Methods in the API
//We need to route URL requests to these endpoints
//Endpoint need to know users and access.
public interface Endpoint extends HttpCallback {
    int ingestURL(String url);
    HashMap<String,String> endpointMethodURL(); //Maps endpoint methods to URLs
    HashMap<String,String> getEndpointParamMap();
    HashMap<String,String> endpointStaticData();
    HashMap<String,Integer> endpointGroupID();
}
