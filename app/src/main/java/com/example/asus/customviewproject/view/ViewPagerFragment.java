package com.example.asus.customviewproject.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.asus.customviewproject.R;

import java.util.ArrayList;


/**
 * Create by 陈健宇 at 2018/8/18
 */
public class ViewPagerFragment extends Fragment {

    private View mView;
    private ListView mListView;
    private ArrayAdapter mArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_view_pager, container, false);
        mListView = mView.findViewById(R.id.list_view);
        mArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, getDates());
        mListView.setAdapter(mArrayAdapter);
        return mView;
    }

    public View getmView() {
        return mView;
    }

    private ArrayList<String> getDates() {
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i = 0; i < 30; i++){
            arrayList.add(String.valueOf(i + 1));
        }
        return arrayList;
    }
}
