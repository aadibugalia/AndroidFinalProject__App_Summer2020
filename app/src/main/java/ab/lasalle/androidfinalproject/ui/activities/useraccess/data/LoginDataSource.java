package ab.lasalle.androidfinalproject.ui.activities.useraccess.data;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import ab.lasalle.androidfinalproject.server.callbacks.ApiResponseCallback;
import ab.lasalle.androidfinalproject.server.threads.HttpServiceThread;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel.Idea;
import ab.lasalle.androidfinalproject.ui.activities.useraccess.data.servermodel.Person;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource implements ApiResponseCallback {
    LoginResultCallback loginResultCallback;

    public void login(String username, String password, LoginResultCallback loginResultCallback) {

        this.loginResultCallback = loginResultCallback;
        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put("userName", username);
            object.put("password", getMD5(username + password));

        } catch (Exception e) {


        }

        HttpServiceThread thread = new HttpServiceThread("attemptLogin", object.toString(), this);
        thread.start();

    }

    public void register(String username, String password, String dob, String location, String email, String firstName, String lastName, LoginResultCallback loginResultCallback) {
        // handle login
        this.loginResultCallback = loginResultCallback;


        JSONObject object = null;
        try {
            object = new JSONObject();
            object.put("userName", username);
            object.put("password", getMD5(username + password));
            object.put("email", email);
            object.put("dob", dob);
            object.put("firstName", firstName);
            object.put("lastName", lastName);
            object.put("country", location);


        } catch (Exception e) {


        }

        HttpServiceThread thread = new HttpServiceThread("registerUser", object.toString(), this);
        thread.start();

    }


    public void logout() {
        // TODO: revoke authentication
    }

    public String getMD5(String input) {
        String output = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            output = number.toString(16);
            while (output.length() < 32)
                output = "0" + output;

        } catch (Exception e) {
            Log.e("MD5", e.getLocalizedMessage());
            output = "";
        }
        return output.toUpperCase();

    }

    @Override
    public void onApiResponseRecieved(JSONObject responseObject) {
        try {
            if (responseObject.get("status").toString().equalsIgnoreCase("0")) {
                LoggedInUser loggedInUser = new LoggedInUser();
                JSONObject personObject = responseObject.getJSONObject("person");
                loggedInUser.setCountry(personObject.getString("country"));
                loggedInUser.setDob(personObject.getString("dob"));
                loggedInUser.setEmail(personObject.getString("email"));
                loggedInUser.setFirstName(personObject.getString("firstName"));
                loggedInUser.setLastName(personObject.getString("lastName"));
                loggedInUser.setUserName(personObject.getString("userName"));
                loggedInUser.setUserId(personObject.getString("id"));
                loggedInUser.setDisplayName(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());

                List list = new ArrayList<Idea>();
                JSONArray jsonArray = (JSONArray) personObject.getJSONArray("savedIdeasAsToDo");
                if (jsonArray != null) {
                    int len = jsonArray.length();
                    for (int i = 0; i < len; i++) {
                        Idea idea = new Idea();
                        JSONObject obj = jsonArray.getJSONObject(i);
                        idea.setContent(obj.getString("content"));
                        idea.setContext(obj.getString("context"));
                        idea.setTitle(obj.getString("title"));

                        list.add(idea);
                    }
                }
                loggedInUser.setSavedIdeasAsToDo(list);


                list = new ArrayList<Person>();
                jsonArray = (JSONArray) personObject.getJSONArray("peopleFollowed");
                if (jsonArray != null) {
                    int len = jsonArray.length();
                    for (int i = 0; i < len; i++) {
                        Person person = new Person();
                        JSONObject obj = jsonArray.getJSONObject(i);
                        person.setCountry(obj.getString("country"));
                        person.setUserId(obj.getString("id"));
                        person.setUserName(obj.getString("userName"));

                        list.add(person);
                    }
                }

                loggedInUser.setPeopleFollowed(list);

                list = new ArrayList<Idea>();
                jsonArray = (JSONArray) personObject.getJSONArray("personalIdeas");
                if (jsonArray != null) {
                    int len = jsonArray.length();
                    for (int i = 0; i < len; i++) {
                        Idea idea = new Idea();
                        JSONObject obj = jsonArray.getJSONObject(i);
                        idea.setContent(obj.getString("content"));
                        idea.setContext(obj.getString("context"));
                        idea.setTitle(obj.getString("title"));

                        list.add(idea);
                    }
                }
                loggedInUser.setPersonalIdeas(list);
                loginResultCallback.onLoginPerformed(new Result.Success<>(loggedInUser));
            } else {
                loginResultCallback.onLoginPerformed(new Result.Error((responseObject.get("status").toString() + " " + responseObject.get("message").toString())));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            loginResultCallback.onLoginPerformed(new Result.Error("Error Parsing Response"));


        }
    }
}