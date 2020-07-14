package ab.lasalle.androidfinalproject.ui.activities.useraccess.data;


import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel.Idea;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel.Person;
import lombok.Data;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Data
public class LoggedInUser implements Serializable {

    private String userId;
    private String displayName;
    private String email;
    private String dob;
    private String country;
    private String firstName;
    private String lastName;
private String userName;

private List<Idea> savedIdeasAsToDo;
private  List<Person> peopleFollowed;
private List<Idea> personalIdeas;


    public LoggedInUser(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }
    public LoggedInUser( String displayName) {

        this.displayName = displayName;
    }
    public LoggedInUser( ) {


    }
    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}