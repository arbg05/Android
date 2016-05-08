package com.utd.tumblrr.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.utd.tumblrr.R;
import com.utd.tumblrr.ui.viewholders.ImagePostHolder;
import com.utd.tumblrr.utils.Constants;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;

import java.util.List;

/**
 * Custom adapter which backs the dashboard
 */
public class CustomAdapter extends ArrayAdapter<Post> {
    private final static String TAG = "CustomAdapter";
    private Context mContext;
    private int mResource;
    private LayoutInflater mInflater;

    private static final int VIEW_TYPE_PHOTO = 0;
    private static final int VIEW_TYPE_DEFAULT = 1;


    /**
     * constructor for custom adapter
     * @param context
     * @param resource
     */
    public CustomAdapter(Context context, int resource, List<Post> data) {
        super(context, resource, data);
        mContext = context;
        mResource = resource;
        mInflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getViewTypeCount() {
        //currently handles only type text and Photo
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position).getType().equals(Constants.TYPE_PHOTO)) {
            return VIEW_TYPE_PHOTO;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    /**
     * Method for loading item layouts in list view
     * {@inheritDoc}
     *
     * @param position
     * @param convertView
     * @param parent
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //handle PhotoSetPost
        if(getItemViewType(position) == VIEW_TYPE_PHOTO) {
            return updatePhotoData((PhotoPost)getItem(position), convertView, parent);
        } else {
            //handle default Post (text)
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(mResource, null);
                holder = new ViewHolder();
                holder.avatarView = (ImageView) convertView.findViewById(R.id.avatarView);
                holder.titleView = (TextView) convertView.findViewById(R.id.titleView);
                holder.headerView = (TextView) convertView.findViewById(R.id.headerView);
                holder.bodyView = (TextView) convertView.findViewById(R.id.bodyView);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            //Log.d(TAG, "Getting the view at position:" + position);

            Post post = getItem(position);

            String blogName = post.getBlogName();
            //String url = Constants.BLOG_URL_PREFIX + blogName + Constants.BLOG_URL_SUFFIX;
            //Log.d(TAG, "blog name: "+ blogName+ "url: "+ url);

            if (blogName != null) {
                Glide.with(mContext)
                        .load(Constants.BLOG_URL_PREFIX + blogName + Constants.BLOG_URL_SUFFIX)
                        .placeholder(R.drawable.placeholder)
                        .into(holder.avatarView);
                holder.titleView.setText(post.getBlogName());
            }

            //Log.d(TAG, post.getType());
            //TEXT
            if (post.getType().equals(Constants.TYPE_TEXT)) {
                TextPost textPost = (TextPost) post;
                if (textPost.getTitle() != null) {
                    holder.headerView.setText("");
                    holder.headerView.setText(Html.fromHtml(textPost.getTitle()));
                }else{
                    holder.headerView.setVisibility(View.GONE);
                }
                if (textPost.getBody() != null) {
                    holder.bodyView.setText("");
                    //holder.bodyView.setText(Html.fromHtml(textPost.getBody()));

                    Spanned htmlDescription = Html.fromHtml(textPost.getBody());
                    String descriptionWithOutExtraSpace = new String(htmlDescription.toString()).trim();

                    holder.bodyView.setText(htmlDescription.subSequence(0, descriptionWithOutExtraSpace.length()));

                }
            }
            return convertView;
        }
    }

    /**
     * View Holder to hold the item layout for
     * performance
     */
    private class ViewHolder {
        ImageView avatarView;
        TextView titleView, headerView, bodyView;
    }

    /**
     * handle loading photo set data
     * @return
     */
    private View updatePhotoData(PhotoPost post, View convertView, ViewGroup parent) {
        ImagePostHolder holder = null;
        convertView = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.image_post_layout, null);
            LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayoutId);
            holder = new ImagePostHolder();

            holder.avatarView = (ImageView) convertView.findViewById(R.id.avatarView);
            holder.titleView = (TextView) convertView.findViewById(R.id.titleView);

            for(int i = 0; i < Constants.IMAGE_SET_MAX_SIZE; i++) {
                holder.textView[i] = new TextView(mContext);
                holder.textView[i].setPadding(4,0,0,0);
                holder.textView[i].setTextColor(0);
                linearLayout.addView(holder.textView[i]);
                holder.imageView[i] = new ImageView(mContext);
                holder.imageView[i].setAdjustViewBounds(true);
                holder.imageView[i].setScaleType(ImageView.ScaleType.FIT_XY);
                holder.imageView[i].setPadding(0, 16, 0, 0);
                linearLayout.addView(holder.imageView[i]);
            }
            convertView.setTag(holder);
        }
        holder = (ImagePostHolder) convertView.getTag();
        for(int i = 0; i < Constants.IMAGE_SET_MAX_SIZE; i++) {
            holder.textView[i].setVisibility(View.GONE);
            holder.imageView[i].setImageDrawable(null);
            holder.imageView[i].setVisibility(View.GONE);
        }
        //Log.d(TAG, "Getting the view at position:" + position);

        String blogName = post.getBlogName();
        //String urld = Constants.BLOG_URL_PREFIX + blogName + Constants.BLOG_URL_SUFFIX;
        //Log.d(TAG, "blog name: "+ blogName+ "url: "+ urld);

        if(blogName != null) {
            Glide.with(mContext)
                    .load(Constants.BLOG_URL_PREFIX + blogName + Constants.BLOG_URL_SUFFIX)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.avatarView);
            holder.titleView.setText(post.getBlogName());
        }

        int i = 0;
        for(Photo p : post.getPhotos()) {
            //Log.d(TAG, "cap: "+ p.getCaption()+ " "+ p.getOriginalSize().getUrl() );
            if(p.getCaption() != null && p.getCaption().length() > 0) {
                holder.textView[i].setText(p.getCaption());
                holder.textView[i].setVisibility(View.VISIBLE);
            }
            String url = p.getOriginalSize().getUrl();
            int wdt = p.getOriginalSize().getWidth();
            int ht = p.getOriginalSize().getHeight();
            if(url != null) {
                holder.imageView[i].setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(url)
                        .override(wdt, ht)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(holder.imageView[i]);
            }
            i++;
        }
        return convertView;
    }

}
