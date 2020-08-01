package ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel;

import java.io.Serializable;

import lombok.Data;

@Data
public class Idea implements Serializable {

    private String id;



    private String title;


    private String context;


    private String content;
    private String originalID;
}
