package com.example.diplomaapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomaapp.listeners.OnSwipeListener;


public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final Drawable icon;
    private final GradientDrawable backgroundLeft;
//    private final GradientDrawable backgroundRight;
    private final OnSwipeListener swipeListener;
    private Context context;


    public SwipeToDeleteCallback(Context context, OnSwipeListener listener) {
        super(0, ItemTouchHelper.LEFT);
//        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        swipeListener = listener;
        this.context = context;
        icon = ContextCompat.getDrawable(this.context, R.drawable.bin_black);
        backgroundLeft = new GradientDrawable();
        backgroundLeft.setColor(Color.argb(100,223, 0, 76)); // Устанавливаем цвет
        backgroundLeft.setCornerRadius(12); // Закругляем углы

//        // Создаем GradientDrawable для фона свайпа вправо
//        backgroundRight = new GradientDrawable();
//        backgroundRight.setColor(Color.GREEN); // Устанавливаем цвет
//        backgroundRight.setCornerRadius(12); // Закругляем углы
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (swipeListener != null) {
            swipeListener.onSwipe(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int maxSwipeDistance = 400;
        if (Math.abs(dX) > maxSwipeDistance) {
            dX = Math.signum(dX) * maxSwipeDistance;
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();
//        if (dX > 0) { // Swipe вправо
//            int iconLeft = itemView.getLeft() + iconMargin;
//            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
//            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
//
//            backgroundRight.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundRight.getIntrinsicWidth(), itemView.getBottom());
//            backgroundRight.draw(c);
//        } else if (dX < 0) { // Swipe влево
        if (dX < 0) { // Swipe влево
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            backgroundLeft.setBounds(itemView.getRight() + ((int) dX) - backgroundLeft.getIntrinsicWidth(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            backgroundLeft.draw(c);
        }
        icon.draw(c);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.4f;
    }


}

