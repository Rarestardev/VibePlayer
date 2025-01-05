package com.rarestardev.vibeplayer.TabFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rarestardev.vibeplayer.Adapters.ImagesViewerAdapter.ImagesFolderAdapter;
import com.rarestardev.vibeplayer.Model.ImageFolder;
import com.rarestardev.vibeplayer.R;
import com.rarestardev.vibeplayer.Utilities.ImagesRetriever;

import java.util.List;

public class PicturesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    String mParam1;
    String mParam2;

    public PicturesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pictures, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ImageFolder> imageFolders = ImagesRetriever.getImageFolders(getContext());

        ImagesFolderAdapter adapter = new ImagesFolderAdapter(getContext(), imageFolders);
        recyclerView.setAdapter(adapter);

        return view;
    }
}