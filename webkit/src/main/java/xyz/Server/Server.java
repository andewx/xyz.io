package xyz.Server;

import io.javalin.Javalin;

public interface Server {
    public void loadConfig(String runtimeConfig);
    public void addRoute(String path); 
    public void startServer(int port);
    public void get();
    public void post();
    public void response();
}


