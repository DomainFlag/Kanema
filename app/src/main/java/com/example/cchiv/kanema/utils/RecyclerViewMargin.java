package com.example.cchiv.kanema.utils;

import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewMargin extends RecyclerView.ItemDecoration {

    private int verticalMargin;

    public RecyclerViewMargin(@IntRange (from = 0) int verticalMargin) {
        this.verticalMargin = verticalMargin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
            state) {
        outRect.bottom = verticalMargin;
        outRect.top = verticalMargin;
    }
}
