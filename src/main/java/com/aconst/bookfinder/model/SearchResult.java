package com.aconst.bookfinder.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult {
    private int totalItems;
    @SerializedName("items")
    private List<Book> bookList;

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
    }
}
