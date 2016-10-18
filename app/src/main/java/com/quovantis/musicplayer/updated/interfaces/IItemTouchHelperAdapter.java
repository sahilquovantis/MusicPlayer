package com.quovantis.musicplayer.updated.interfaces;

/**
 * Used For Swipe to Remove in RecyclerView Adapter
 */
public interface IItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void onItemMoveCompleted();
}
