package com.voicetodo.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class SwipeToActionCallback extends ItemTouchHelper.SimpleCallback {

    private Context context;
    private Paint paint;
    private ColorDrawable background;
    private int backgroundColor;
    private Drawable deleteIcon;
    private Drawable completeIcon;
    private Drawable dragIcon;
    private int intrinsicWidth;
    private int intrinsicHeight;

    protected SwipeToActionCallback(Context context) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.context = context;
        
        // Initialize paint and background
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(48f);
        paint.setAntiAlias(true);
        
        background = new ColorDrawable();
        backgroundColor = Color.LTGRAY;
        
        // Load icons
        deleteIcon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete);
        completeIcon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_edit);
        dragIcon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_sort_by_size);
        
        if (deleteIcon != null) {
            intrinsicWidth = deleteIcon.getIntrinsicWidth();
            intrinsicHeight = deleteIcon.getIntrinsicHeight();
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // Handle drag and drop
        onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }
    
    // Abstract method for handling item move
    public abstract void onItemMove(int fromPosition, int toPosition);

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        if (dX > 0) {
            // Swiping to the right - Complete action
            background.setColor(Color.parseColor("#4CAF50")); // Green
            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
            background.draw(c);

            if (completeIcon != null) {
                int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int iconMargin = (itemHeight - intrinsicHeight) / 2;
                int iconLeft = itemView.getLeft() + iconMargin;
                int iconRight = itemView.getLeft() + iconMargin + intrinsicWidth;
                int iconBottom = iconTop + intrinsicHeight;

                completeIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                completeIcon.draw(c);
            }

            // Draw text
            c.drawText("Tamamla", itemView.getLeft() + 120, itemView.getTop() + itemHeight / 2f + 20, paint);

        } else if (dX < 0) {
            // Swiping to the left - Delete action
            background.setColor(Color.parseColor("#F44336")); // Red
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            if (deleteIcon != null) {
                int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int iconMargin = (itemHeight - intrinsicHeight) / 2;
                int iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
                int iconRight = itemView.getRight() - iconMargin;
                int iconBottom = iconTop + intrinsicHeight;

                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(c);
            }

            // Draw text
            c.drawText("Sil", itemView.getRight() - 120, itemView.getTop() + itemHeight / 2f + 20, paint);
        }
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.3f; // 30% swipe to trigger action
    }
}