  package pro.acuna.andromeda;
  /*
   Created by Acuna on 20.01.2019
  */
  
  public class User {
    
    private String name, fullName, avatar;
    
    public User setName (String name) {
      
      this.name = name;
      return this;
      
    }
    
    public User setFullName (String name) {
      
      this.fullName = name;
      return this;
      
    }
    
    public User setAvatar (String avatar) {
      
      this.avatar = avatar;
      return this;
      
    }
    
    public String getName () {
      return name;
    }
    
    public String toString () {
      return getName ();
    }
    
    public String getFullName () {
      return fullName;
    }
    
    public String getAvatar () {
      return avatar;
    }
    
  }