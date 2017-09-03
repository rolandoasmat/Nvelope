package com.rolandoasmat.nvelope.models;

/**
 * Created by rolandoasmat on 9/2/17.
 */

public interface CompletionClosure <T> {
    public void onComplete(T response);
}