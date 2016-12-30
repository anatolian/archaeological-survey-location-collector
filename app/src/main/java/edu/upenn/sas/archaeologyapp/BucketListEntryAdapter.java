package edu.upenn.sas.archaeologyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * The adapter class for populating each item in the bucket list
 * Created by eanvith on 30/12/16.
 */

public class BucketListEntryAdapter extends ArrayAdapter<BucketEntry> {

    /**
     * Resource ID of the layout for a bucket list entry
     */
    private final int listItemLayoutResource;

    /**
     * Constructor
     * @param context Current app context
     * @param listItemLayoutResource Resource ID of the layout for a bucket list entry
     */
    public BucketListEntryAdapter(final Context context, final int listItemLayoutResource) {

        super(context, 0);

        this.listItemLayoutResource = listItemLayoutResource;

    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        // We need to get the best view (re-used if possible) and then
        // retrieve its corresponding ViewHolder, which optimizes lookup efficiency
        final View view = getWorkingView(convertView);
        final ViewHolder viewHolder = getViewHolder(view);
        final BucketEntry entry = getItem(position);

        // Set all the fields of a list entry below
        viewHolder.titleTV.setText(entry.getTitle());

        return view;

    }

    /**
     * Returns the convertView re-used if possible or inflated new if not possible
     * @param convertView An existing view which can be re-used. Pass null if new view is to be
     *                    inflated.
     * @return The workingView to use
     */
    private View getWorkingView(final View convertView) {

        View workingView;

        if(null == convertView) {

            final Context context = getContext();
            final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            workingView = inflater.inflate(listItemLayoutResource, null);

        } else {

            workingView = convertView;

        }

        return workingView;

    }

    /**
     * The viewHolder allows us to avoid re-looking up view references
     * Since views are recycled, these references will never change
     * @param workingView Current view being used
     * @return The viewHolder
     */
    private ViewHolder getViewHolder(final View workingView) {

        final Object tag = workingView.getTag();
        ViewHolder viewHolder;


        if(null == tag || !(tag instanceof ViewHolder)) {

            viewHolder = new ViewHolder();
            viewHolder.titleTV = (TextView) workingView.findViewById(R.id.bucket_list_entry_title_text_view);
            workingView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) tag;

        }

        return viewHolder;

    }

    /**
     * ViewHolder allows us to avoid re-looking up view references
     * Since views are recycled, these references will never change
     */
    private static class ViewHolder {

        public TextView titleTV;

    }

}
