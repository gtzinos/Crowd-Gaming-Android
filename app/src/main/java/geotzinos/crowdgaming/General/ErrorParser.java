package geotzinos.crowdgaming.General;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by George Tzinos on 6/8/2016.
 */
public class ErrorParser {

    /*
        Will return a new VolleyError object with all parsed messages
     */
    public static VolleyError NetworkErrors(VolleyError volleyError)
    {
        JSONObject jsonResp = null;
        String message = "Could not connect to the server.";// default response
        if (volleyError != null && volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            try {
                        /* get the object */
                jsonResp = new JSONObject(new String(volleyError.networkResponse.data));
                message = jsonResp.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int code = -1;
            try {
                code = jsonResp.getInt("code");
            }
            catch(Exception e) { }
            return new VolleyError(new NetworkResponse(code,message.getBytes(),null,false));

        }
        else {
            if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                return new VolleyError(new NetworkResponse(-1,"Please check your internet connection.".getBytes(),null,false));
            } else if (volleyError instanceof AuthFailureError) {
                return new VolleyError(new NetworkResponse(-1,"We can't recognize you.".getBytes(),null,false));
            } else if (volleyError instanceof ServerError) {
                return new VolleyError(new NetworkResponse(-1,"Server error. Please report it.".getBytes(),null,false));
            } else if (volleyError instanceof NetworkError) {
                return new VolleyError(new NetworkResponse(-1,"Server is down. Please try later.".getBytes(),null,false));
            } else if (volleyError instanceof ParseError) {
                return new VolleyError(new NetworkResponse(-1,"Data received was an unreadable mess. Please report it.".getBytes(),null,false));
            }
        }
        return new VolleyError(message);
    }

    /*
        Will return the final message.
     */
    public static String ResponseError(VolleyError error)
    {
        String message = "Could not connect to the server.";
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            message = "Please check your internet connection.";
        } else if (error instanceof AuthFailureError) {
            message = "We can't recognize you.";
        } else if (error instanceof ServerError) {
            message = "Server error. Please report it.";
        } else if (error instanceof NetworkError) {
            message = "Server is down. Please try later.";
        } else if (error instanceof ParseError) {
            message = "Data received was an unreadable mess. Please report it.";
        }
        else {
            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                message = new String(response.data);
            }
        }
        return message;
    }

}
