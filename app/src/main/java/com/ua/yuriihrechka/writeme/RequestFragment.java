package com.ua.yuriihrechka.writeme;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private View requestFragmentView;
    private RecyclerView requesList;



    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        requestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);

        requesList = (RecyclerView)requestFragmentView.findViewById(R.id.chat_request_list);
        requesList.setLayoutManager(new LinearLayoutManager(getContext()));
        return requestFragmentView;
    }

}
