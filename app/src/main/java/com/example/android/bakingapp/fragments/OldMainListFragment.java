package com.example.android.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.adapters.RecipeListAdapter;
import com.example.android.bakingapp.tools.RecipeRecordCollection;

import java.util.List;

public class OldMainListFragment extends Fragment {
    OnCardClickListener mCallback;

    private List<String> mRecipeNames;
    private RecipeRecordCollection mRecipeData;
    RecipeListAdapter mAdapter;
    private ListView mListView;

    public void setRecipeNames(List<String> data) {
        if (mRecipeNames != null) {
            mRecipeNames.clear();
            mRecipeNames.addAll(data);
        }
        mAdapter.notifyDataSetChanged();
    }

    public OldMainListFragment() {
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
            throw new ClassCastException(context.toString() + " must define OnStepClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //mListView = (ListView) rootView.findViewById(R.id.main_list_view);
/*
        RecipeRecordCollection emptyCollection = new RecipeRecordCollection();
        mAdapter = new RecipeListAdapter(getContext(), emptyCollection);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mCallback.onCardSelected(position);
            }
        });
*/
        return rootView;
    }

}
