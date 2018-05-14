package com.aconst.bookfinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aconst.bookfinder.model.Book;
import com.aconst.bookfinder.model.VolumeInfo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "BookListAdapter";
    private static final String BASE_URL = "http://books.google.com/books/";

    private List<Book> bookList = new ArrayList<>();

    public void setBookList(List<Book> bookList) {
        this.bookList.addAll(bookList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Book book = bookList.get(position);
        BookViewHolder bookViewHolder = (BookViewHolder) holder;
        VolumeInfo volumeInfo = book.getVolumeInfo();
        if (volumeInfo != null) {
            if (volumeInfo.getImageLinks() != null)
                downloadImage(volumeInfo.getImageLinks().getSmallThumbnail(), bookViewHolder.image);
            bookViewHolder.title.setText(volumeInfo.getTitle());
            bookViewHolder.descr.setText(volumeInfo.getDescription());
        }
    }

    public void downloadImage(String url, final ImageView imageView) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .build();

        GBookAPI bookAPI = retrofit.create(GBookAPI.class);

        Call<ResponseBody> bookCall = bookAPI.getImage(url);
        bookCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Bitmap bm = BitmapFactory.decodeStream(response.body().byteStream());
                        imageView.setImageBitmap(bm);
                    }

                } else {
                    Log.d(TAG, "Response code: " + response.code());
                    Log.d(TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public Book getItem(int position) {
        return bookList.get(position);
    }

    private class BookViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView title;
        private TextView descr;

        BookViewHolder(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.item_image);
            this.title = itemView.findViewById(R.id.item_title);
            this.descr = itemView.findViewById(R.id.item_descr);
        }
    }
}
