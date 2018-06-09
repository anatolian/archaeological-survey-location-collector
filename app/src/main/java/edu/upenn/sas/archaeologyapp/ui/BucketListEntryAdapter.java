package edu.upenn.sas.archaeologyapp.ui;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import edu.upenn.sas.archaeologyapp.models.DataEntryElement;
import edu.upenn.sas.archaeologyapp.R;
/**
 * The adapter class for populating each item in the bucket list
 * Created by eanvith on 30/12/16.
 */
public class BucketListEntryAdapter extends ArrayAdapter<DataEntryElement>
{
    // Resource ID of the layout for a bucket list entry
    private final int listItemLayoutResource;
    // A reference to MainActivity's google map
    private GoogleMap googleMap;
    /**
     * Constructor
     * @param context Current app context
     * @param listItemLayoutResource Resource ID of the layout for a bucket list entry
     */
    public BucketListEntryAdapter(final Context context, final int listItemLayoutResource)
    {
        super(context, 0);
        this.listItemLayoutResource = listItemLayoutResource;
    }

    /**
     * Get item in container
     * @param position - position in container
     * @param convertView - view type
     * @param parent - container view
     * @return Returns the item view
     */
    @Override
    @NonNull
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent)
    {
        // We need to get the best view (re-used if possible) and then
        // retrieve its corresponding ViewHolder, which optimizes lookup efficiency
        final View view = getWorkingView(convertView);
        final ViewHolder viewHolder = getViewHolder(view);
        final DataEntryElement elem = getItem(position);
        // Set the category
        String id = elem.getZone() + "."+elem.getHemisphere() + "." + elem.getEasting() + "."
                + elem.getNorthing() + "." + elem.getSample();
        viewHolder.categoryTV.setText(id);
        // Open Maps with GPS locaiton of this entry
        viewHolder.mapButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked map
             * @param v - map button
             */
            @Override
            public void onClick(View v)
            {
                if (googleMap != null)
                {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(elem.getLatitude(),
                            elem.getLongitude()), 20));
                }
            }
        });
        return view;
    }

    /**
     * A helper function to pass this adapter a reference to MainActivity's google map
     * @param map - google map
     */
    public void setMap(GoogleMap map)
    {
        googleMap = map;
    }

    /**
     * Returns the convertView re-used if possible or inflated new if not possible
     * @param convertView - An existing view which can be re-used. Pass null if new view is to be inflated.
     * @return The workingView to use
     */
    private View getWorkingView(final View convertView)
    {
        View workingView;
        if (null == convertView)
        {
            final Context context = getContext();
            final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            workingView = inflater.inflate(listItemLayoutResource, null);
        }
        else
        {
            workingView = convertView;
        }
        return workingView;
    }

    /**
     * The viewHolder allows us to avoid re-looking up view references. Since views are recycled, these
     * references will never change
     * @param workingView Current view being used
     * @return The viewHolder
     */
    private ViewHolder getViewHolder(final View workingView)
    {
        final Object tag = workingView.getTag();
        ViewHolder viewHolder;
        if (tag == null || !(tag instanceof ViewHolder))
        {
            viewHolder = new ViewHolder();
            viewHolder.categoryTV = workingView.findViewById(R.id.bucket_list_entry_category_text_view);
            viewHolder.imageView = workingView.findViewById(R.id.bucket_list_entry_image_view);
            viewHolder.mapButton = workingView.findViewById(R.id.bucket_list_entry_maps_button);
            workingView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) tag;
        }
        return viewHolder;
    }

    /**
     * ViewHolder allows us to avoid re-looking up view references
     * Since views are recycled, these references will never change
     */
    private static class ViewHolder
    {
        public TextView categoryTV;
        public ImageView imageView;
        public ImageButton mapButton;
    }
}