package edu.upenn.sas.archaeologyapp.ui;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.upenn.sas.archaeologyapp.R;
import edu.upenn.sas.archaeologyapp.models.PathElement;
/**
 * The adapter class for populating each item in the bucket list
 * @author Created by eanvith on 30/12/16.
 */
public class PathEntryAdapter extends ArrayAdapter<PathElement>
{
    // Resource ID of the layout for a path list entry
    private final int listItemLayoutResource;
    // A reference to MainActivity's google map
    private GoogleMap googleMap;
    /**
     * Constructor
     * @param context Current app context
     * @param listItemLayoutResource Resource ID of the layout for a bucket list entry
     */
    public PathEntryAdapter(final Context context, final int listItemLayoutResource)
    {
        super(context, 0);
        this.listItemLayoutResource = listItemLayoutResource;
    }
    /**
     * Create view
     * @param position - position in container
     * @param convertView - this item's view
     * @param parent - container view
     * @return Returns the contained view
     */
    @Override
    @NonNull
    public View getView(final int position, final View convertView, @NonNull final ViewGroup parent)
    {
        // We need to get the best view (re-used if possible) and then retrieve its corresponding
        // ViewHolder, which optimizes lookup efficiency
        final View view = getWorkingView(convertView);
        final ViewHolder viewHolder = getViewHolder(view);
        final PathElement elem = getItem(position);
        // Set the startTime
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        viewHolder.startTime.setText(sdf.format(new Date(elem.getBeginTime())));
        // Set the teamMember
        viewHolder.teamMember.setText("Path by " +  elem.getTeamMember());
        // Open Maps with GPS locaiton of this entry
        viewHolder.mapButton.setOnClickListener(new View.OnClickListener() {
            /**
             * User clicked the map
             * @param v - map button
             */
            @Override
            public void onClick(View v)
            {
                if (googleMap != null)
                {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(elem.getBeginLatitude(),
                            elem.getBeginLongitude()), 20));
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
     * @param convertView An existing view which can be re-used. Pass null if new view is to be inflated.
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
     * The viewHolder allows us to avoid re-looking up view references. Since views are recycled,
     * these references will never change
     * @param workingView Current view being used
     * @return The viewHolder
     */
    private ViewHolder getViewHolder(final View workingView)
    {
        final Object tag = workingView.getTag();
        ViewHolder viewHolder;
        if (null == tag || !(tag instanceof ViewHolder))
        {
            viewHolder = new ViewHolder();
            viewHolder.startTime = workingView.findViewById(R.id.paths_list_entry_start_time_text_view);
            viewHolder.teamMember = workingView.findViewById(R.id.paths_list_entry_team_member_text_view);
            viewHolder.mapButton= workingView.findViewById(R.id.paths_list_entry_maps_button);
            workingView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) tag;
        }
        return viewHolder;
    }

    /**
     * ViewHolder allows us to avoid re-looking up view references. Since views are recycled, these
     * references will never change
     */
    private static class ViewHolder
    {
        public TextView startTime;
        public TextView teamMember;
        public ImageButton mapButton;
    }
}