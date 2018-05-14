package com.aconst.bookfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.aconst.bookfinder.model.Book;
import com.aconst.bookfinder.model.IndustryIdentifier;

import java.util.List;

public class ActivityBook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Intent intent = getIntent();
        if (intent.hasExtra("book")) {
            Book book = (Book) intent.getSerializableExtra("book");
            if (book != null) {
                ((TextView) findViewById(R.id.tv_title)).setText(book.getVolumeInfo().getTitle());

                BookListAdapter adapter = new BookListAdapter();
                ImageView imageView = findViewById(R.id.iv_thumbnail);
                adapter.downloadImage(book.getVolumeInfo().getImageLinks().getThumbnail(),
                        imageView);

                if (book.getVolumeInfo().getAuthors() != null)
                    ((TextView) findViewById(R.id.tv_author)).setText(
                            TextUtils.join(", ", book.getVolumeInfo().getAuthors()));

                String publishData = book.getVolumeInfo().getPublisher();
                if (publishData == null)
                    publishData = "";
                String publishedDate = book.getVolumeInfo().getPublishedDate();
                if (publishedDate == null)
                    publishedDate = "";
                publishData += (publishData.length() == 0 || publishedDate.length() == 0?
                    publishedDate : ", " + publishedDate);
                int pageCount = book.getVolumeInfo().getPageCount();
                if (pageCount > 0) {
                    publishData += (publishData.length() > 0? " - ": "")
                            + getResources().getString(R.string.pageCount)
                            + Integer.toString(pageCount);
                }
                ((TextView) findViewById(R.id.tv_publishData)).setText(publishData);

                ((TextView) findViewById(R.id.tv_descr)).setText(
                        book.getVolumeInfo().getDescription());

                List<IndustryIdentifier> industryIdentifiers = book.getVolumeInfo().getIndustryIdentifiers();
                if (industryIdentifiers != null) {
                    String codes = "";
                    for (int i = 0; i < industryIdentifiers.size(); i++)
                        codes += (i > 0 ? ", " : "")
                                + book.getVolumeInfo().getIndustryIdentifiers().get(i).getIdentifier();
                    ((TextView) findViewById(R.id.tv_codes)).setText(codes);
                }
            }
        }
    }
}
