package com.example.diplomaapp.dataClasses;

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

import com.example.diplomaapp.R;
import com.example.diplomaapp.listeners.OnSwipeListener;


public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final Drawable iconDelete;
    private final Drawable iconChange;
    private final GradientDrawable backgroundLeft;
    private final GradientDrawable backgroundRight;
    private final OnSwipeLeftListener swipeLeftListener;
    private final OnSwipeRightListener swipeRightListener;
    private Context context;

    public interface OnSwipeLeftListener {
        void onSwipeLeft(int position);
    }

    public interface OnSwipeRightListener {
        void onSwipeRight(int position);
    }



    public SwipeToDeleteCallback(Context context, OnSwipeLeftListener swipeLeftListener, OnSwipeRightListener swipeRightListener) {
//        super(0, ItemTouchHelper.LEFT);
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.swipeLeftListener = swipeLeftListener;
        this.swipeRightListener = swipeRightListener;
        this.context = context;

        iconDelete = ContextCompat.getDrawable(this.context, R.drawable.bin_black);
        iconChange = ContextCompat.getDrawable(this.context, R.drawable.draw);

        backgroundLeft = new GradientDrawable();
        backgroundLeft.setColor(Color.argb(100,223, 0, 76)); // Устанавливаем цвет
        backgroundLeft.setCornerRadius(12); // Закругляем углы

        // Создаем GradientDrawable для фона свайпа вправо
        backgroundRight = new GradientDrawable();
        backgroundRight.setColor(Color.argb(100,164,255,164)); // Устанавливаем цвет
        backgroundRight.setCornerRadius(12); // Закругляем углы
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT && swipeLeftListener != null) {
            swipeLeftListener.onSwipeLeft(position);
        } else if (direction == ItemTouchHelper.RIGHT && swipeRightListener != null) {
            swipeRightListener.onSwipeRight(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int maxSwipeDistance = 250;
        if (Math.abs(dX) > maxSwipeDistance) {
            dX = Math.signum(dX) * maxSwipeDistance;
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;

        if (dX > 0) { // Swipe вправо
            int iconMargin = (itemView.getHeight() - iconChange.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - iconChange.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + iconChange.getIntrinsicHeight();

            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + iconChange.getIntrinsicWidth();
            iconChange.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            backgroundRight.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundRight.getIntrinsicWidth(), itemView.getBottom());
            backgroundRight.draw(c);
        } else if (dX < 0) { // Swipe влево
            int iconMargin = (itemView.getHeight() - iconDelete.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - iconDelete.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + iconDelete.getIntrinsicHeight();

            int iconLeft = itemView.getRight() - iconMargin - iconDelete.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            iconDelete.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            backgroundLeft.setBounds(itemView.getRight() + ((int) dX) - backgroundLeft.getIntrinsicWidth(), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            backgroundLeft.draw(c);
        }
        iconDelete.draw(c);
        iconChange.draw(c);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.4f;
    }


}

