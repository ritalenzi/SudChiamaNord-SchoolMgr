package com.sudchiamanord.schoolmgr.util.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sudchiamanord.schoolmgr.util.Tags;

/**
 * Created by rita on 1/11/16.
 */
public class SearchTabFragment extends Fragment
{
    private int resource;

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        Bundle data = getArguments();
        resource = data.getInt (Tags.SEARCH_FRAGMENT_LAYOUT);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate (resource, null);
    }
}
