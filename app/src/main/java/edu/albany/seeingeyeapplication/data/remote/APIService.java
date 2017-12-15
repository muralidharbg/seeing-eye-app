package edu.albany.seeingeyeapplication.data.remote;
import edu.albany.seeingeyeapplication.data.model.Post;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Hanofsoul on 12/15/2017.
 */

public interface APIService
{
    @Multipart
    @POST("detect-object")

    Call<Post> postFile(@Part MultipartBody.Part filePart);


}
