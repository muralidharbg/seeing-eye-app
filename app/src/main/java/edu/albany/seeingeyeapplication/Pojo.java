package edu.albany.seeingeyeapplication;

/**
 * Created by Hanofsoul on 12/15/2017.
 */
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pojo
{
        @SerializedName("content")
        @Expose
        private List<String> content = null;

        public List<String> getContent() {
            return content;
        }

        public void setContent(List<String> content) {
            this.content = content;
        }
}
