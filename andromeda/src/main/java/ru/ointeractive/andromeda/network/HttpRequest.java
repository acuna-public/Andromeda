  package ru.ointeractive.andromeda.network;
  
  import upl.core.Base64;
  
  import java.io.UnsupportedEncodingException;
  import java.util.Arrays;

  public class HttpRequest {
    
    public static class Basic extends upl.core.HttpRequest.Auth {
      
      private String login, password;
      
      public Basic setLogin (String login) {
        
        this.login = login;
        return this;
        
      }
      
      public Basic setPassword (String password) {
        
        this.password = password;
        return this;
        
      }
      
      @Override
      public String toString () {
        
        try {
          return "Basic " + Arrays.toString (Base64.encode ((login + ":" + password).getBytes (upl.type.String.DEF_CHARSET), Base64.DEFAULT));
        } catch (UnsupportedEncodingException e) {
          return null;
        }
        
      }
      
    }
    
  }