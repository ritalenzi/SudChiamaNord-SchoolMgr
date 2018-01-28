package com.sudchiamanord.schoolmgr.util.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sudchiamanord.schoolmgr.R;
import com.sudchiamanord.schoolmgr.info.KidInfo;
import com.sudchiamanord.schoolmgr.info.ListaAdozioni;
import com.sudchiamanord.schoolmgr.info.SearchResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by rita on 12/21/15.
 */
public class SearchResultsArrayAdapter extends ArrayAdapter<SearchResult>
{
    private static final String TAG = SearchResultsArrayAdapter.class.getSimpleName();


    public SearchResultsArrayAdapter (Context context)
    {
        super (context, R.layout.activity_kid_info);
    }

    public SearchResultsArrayAdapter (Context context, List<SearchResult> objects)
    {
        super(context, R.layout.activity_kid_info, objects);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent)
    {
        SearchResult searchResult = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from (getContext()).inflate (R.layout.search_result_row,
                    parent, false);
        }

        KidInfo kidInfo = searchResult.getKidInfo();
        Bitmap photo = kidInfo.getPhoto();
        if (photo != null) {
            ((ImageView) convertView.findViewById (R.id.searchResPhotoThumbnail)).setImageBitmap (photo);
        }
        else {
            ImageView image  = (ImageView) convertView.findViewById (R.id.searchResPhotoThumbnail);
            Resources res = getContext().getResources();
            image.setImageDrawable (res.getDrawable (R.drawable.empty_picture_icon_128));
        }

        StringBuilder sb = new StringBuilder();
        sb.append ("<b>");
        sb.append (convertView.getContext().getString (R.string.nameLabel));
        sb.append (":</b> ");
        sb.append (kidInfo.getAnome());
        if (kidInfo.getAnick() != null) {
            sb.append (" ");
            sb.append (kidInfo.getAnick());
        }
        sb.append (" ");
        sb.append (kidInfo.getAcogn());

        ((TextView) convertView.findViewById (R.id.searchResKidName)).setText (Html.fromHtml (sb.toString()));

        if ((kidInfo.getStato() == 0) || (kidInfo.getStato() == 1)) {
            String status = "<b>" + convertView.getContext().getString (R.string.statusLabel) + ":</b> " +
                    convertView.getResources().getTextArray (R.array.state)[kidInfo.getStato()].toString();
            TextView searchResKidStatus = ((TextView) convertView.findViewById (R.id.searchResKidStatus));
            searchResKidStatus.setText(Html.fromHtml(status));

            Button statusButton = ((Button) convertView.findViewById (R.id.statusIndicator));
            statusButton.setBackgroundColor (kidInfo.getStato() == 0 ? 0xFF930f15 : 0xFF19ab0e);
            statusButton.invalidate();
        }



        String newDateString = null;
        SimpleDateFormat sdf;
        try {
            sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
            Date d = sdf.parse (kidInfo.getCreil());
            sdf.applyPattern ("yyyy-MM-dd");
            newDateString = sdf.format (d);
        }
        catch (ParseException e) {
            Log.d (TAG, "Wrong date format (not yyyy-MM-dd HH:mm:ss)");
        }

        if (newDateString == null) {
            try {
                sdf = new SimpleDateFormat ("yyyy-M-dd HH:mm:ss");
                Date d = sdf.parse (kidInfo.getCreil());
                sdf.applyPattern ("yyyy-MM-dd");
                newDateString = sdf.format (d);
            }
            catch (ParseException e) {
                Log.d (TAG, "Wrong date format (not yyyy-M-dd HH:mm:ss)");
            }
        }

        TextView searchResCreatedDate = ((TextView) convertView.findViewById (R.id.searchResCreatedDate));
        String date = "<b>" + convertView.getContext().getString (R.string.createdDateLabel) +
                ":</b> " + (newDateString != null ? newDateString : kidInfo.getCreil());
        searchResCreatedDate.setText(Html.fromHtml(date));

        String schoolYear = searchResult.getSchoolYear();
        ListaAdozioni adoption = kidInfo.getAdoption (schoolYear);
        if (adoption != null) {
            String school;
            if (adoption.getScuno() != null) {
                school = "<b>" + convertView.getContext().getString (R.string.schoolNameLabel) + ":</b> "
                        + adoption.getScuno();
            }
            else {
                school = "<b>" + convertView.getContext().getString (R.string.schoolCodeLabel) + ":</b> "
                        + adoption.getIdscu();
            }
            TextView searchResSchool = ((TextView) convertView.findViewById (R.id.searchResSchool));
            searchResSchool.setText (Html.fromHtml (school));
            searchResSchool.setVisibility(View.VISIBLE);

            if ((adoption.getPclas() != null) && (!adoption.getPclas().equals ("?"))) {
                String schoolClass = "<b>" + convertView.getContext().getString (R.string.classLabel)
                        + ":</b> " + adoption.getPclas();
                TextView searchResClass = ((TextView) convertView.findViewById (R.id.searchResClass));
                searchResClass.setText (Html.fromHtml (schoolClass));
                searchResClass.setVisibility (View.VISIBLE);
            }
        }

        return convertView;
    }
}
