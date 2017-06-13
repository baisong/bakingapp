package com.example.android.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.bakingapp.MainListAdapter;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.RecipeData;

public class MainListFragment extends Fragment {
    OnCardClickListener mCallback;

    public MainListFragment() {
    }

    public interface OnCardClickListener {
        void onCardSelected(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCardClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must define OnCardClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        final View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.main_list_view);
        MainListAdapter mAdapter = new MainListAdapter(getContext(), RecipeData.getTestData());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mCallback.onCardSelected(position);
            }
        });

        return rootView;
    }

}
