package com.aconst.bookfinder;

import com.aconst.bookfinder.model.SearchResult;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GBookAPI {
    @GET("volumes")
    Call<SearchResult> getBooks(@Query("q") String query, @Query("startIndex") int startIndex);

    @GET
    Call<ResponseBody> getImage(@Url String url);
}
