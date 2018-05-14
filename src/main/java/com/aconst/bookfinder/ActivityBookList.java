package com.aconst.bookfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;

import com.aconst.bookfinder.model.Book;
import com.aconst.bookfinder.model.SearchResult;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityBookList extends AppCompatActivity implements Callback<SearchResult> {
    private static final String TAG = "ActivityBookList";
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/";
    private static final int DEFAULT_PAGE_SIZE = 10;

    private String query = "";
    private boolean isLoading = false;
    private int startIndex = 0;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        Intent intent = getIntent();
        if (intent.hasExtra("query")) {
            query = intent.getStringExtra("query");
            start(query, startIndex);
        }

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.book_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BookListAdapter());

        // Подгрузка следующей порции данных при прокрутке до последнего элемента
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = recyclerView.getAdapter().getItemCount();
                int visibleItemCount = recyclerView.getChildCount();
                int firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager())
                        .findFirstVisibleItemPosition();

                if (!isLoading) {
                    if (visibleItemCount + firstVisibleItem >= totalItemCount
                            && totalItemCount >= startIndex + DEFAULT_PAGE_SIZE) {
                        isLoading = true;
                        startIndex += DEFAULT_PAGE_SIZE;
                        start(query, startIndex);
                    }
                }
            }
        });

        // Переход к просмотру подробной карточки выбранной книги
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    int position = rv.getChildAdapterPosition(rv.findChildViewUnder(e.getX(), e.getY()));
                    Intent intent = new Intent(ActivityBookList.this, ActivityBook.class);
                    Book book = ((BookListAdapter) rv.getAdapter()).getItem(position);
                    if (book != null) {
                        intent.putExtra("book", book);
                        startActivity(intent);
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    public void start(String query, int startIndex) {
        isLoading = true;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        GBookAPI bookAPI = retrofit.create(GBookAPI.class);

        Call<SearchResult> bookCall = bookAPI.getBooks(query, startIndex);
        bookCall.enqueue(this);
    }

    @Override
    public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
        isLoading = false;
        if (response.isSuccessful()) {
            SearchResult books = response.body();
            if (books.getBookList().size() > 0) {
                BookListAdapter adapter = (BookListAdapter) recyclerView.getAdapter();
                adapter.setBookList(books.getBookList());
                adapter.notifyDataSetChanged();
            }

        } else {
            Log.d(TAG, "Response code: " + response.code());
            Log.d(TAG, response.errorBody().toString());
        }
    }

    @Override
    public void onFailure(Call<SearchResult> call, Throwable t) {
        isLoading = false;
        Log.e(TAG, t.toString());
    }
}
