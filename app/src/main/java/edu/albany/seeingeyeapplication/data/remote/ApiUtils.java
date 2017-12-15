package edu.albany.seeingeyeapplication.data.remote;

/**
 * Created by Hanofsoul on 12/15/2017.
 */
public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "http://169.226.237.216:8081/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}

