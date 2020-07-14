package ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel;

import java.io.Serializable;

import lombok.Data;

@Data
public class Person implements Serializable {
    private String userId;
    private String displayName;
    private String email;
    private String dob;
    private String country;
    private String firstName;
    private String lastName;
    private String userName;
}
