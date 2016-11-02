package social;

/**
 * @author Mohanad Al Sayegh
 * This class is required to correctly populate the Tableview Object in the Controller.
 */
public class Friends {
    
    private String firstName;
    private String lastName;
    private int ID;
    private String email;
    
    public Friends(int ID, String firstName, String LastName){
        this.firstName = firstName;
        this.lastName = LastName;
        this.ID = ID;
       // this.email = email;
    }
    
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    
    public void setLastName(String LastName){
        this.lastName = LastName;
    }
    
    public void setID(int ID){
        this.ID = ID;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    
    public String getFirstName(){
        return firstName;
    }
    
    public String getLastName(){
        return lastName;
    }
    
    public int getID(){
        return ID;
    }
    
    public String getEmail(){
        return email;
    }
    
    
    
}
