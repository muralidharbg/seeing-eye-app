package edu.albany.seeingeyeapplication.data.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hanofsoul on 12/15/2017.
 */

public class Post {
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
