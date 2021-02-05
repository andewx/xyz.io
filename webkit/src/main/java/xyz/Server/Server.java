package xyz.Server;


import java.util.HashMap;

//Server Interface - Hosts Application Protocols over HTTP Connection
//Configures Server interface to be a functional handler. Attaches an endpoint to http request.
public interface Server {

    int httpInitConnection(HashMap<String,String> configuration);
    int listenRequest(HashMap<String,String> listenProperties);
    int preRequest(HttpCallback handler);
    int postRequest(HttpCallback handler);
    int preResponse(HttpCallback handler);
    int postResponse(HttpCallback handler);
    int httpResponse(HttpCallback handler);

}


