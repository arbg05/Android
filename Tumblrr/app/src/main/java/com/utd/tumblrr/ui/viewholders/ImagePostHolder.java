package com.utd.tumblrr.ui.viewholders;

import android.widget.ImageView;
import android.widget.TextView;

import com.utd.tumblrr.utils.Constants;

/**
 * View Holder for photo post
 */
public class ImagePostHolder {
    public ImageView avatarView;
    public TextView titleView;
    public TextView[] textView = new TextView[Constants.IMAGE_SET_MAX_SIZE];
    public ImageView[] imageView = new ImageView[Constants.IMAGE_SET_MAX_SIZE];
}
