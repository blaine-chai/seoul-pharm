package com.daejong.seoulpharm.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.daejong.seoulpharm.R;
import com.daejong.seoulpharm.activity.ComponentActivity;
import com.daejong.seoulpharm.activity.ScrapActivity;
import com.daejong.seoulpharm.adapter.ScrapComponentListAdapter;
import com.daejong.seoulpharm.adapter.ScrapPharmListAdapter;
import com.daejong.seoulpharm.db.DBHelper;
import com.daejong.seoulpharm.model.MedicineInfo;
import com.daejong.seoulpharm.model.PharmItem;


public class ScrapFragment extends Fragment {

    /**
     * show Scrapped Datas
     */

    // FRAGMENT TYPE
    public static final String KEY_FRAGMENT_TYPE = "KEY_FRAGMENT_TYPE";
    public static final String TYPE_PHARMS = "TYPE_PHARMS";
    public static final String TYPE_COMPONENT = "TYPE_COMPONENT";

    private String type;

    // VIEW
    ListView listView;
    ScrapPharmListAdapter mPharmListAdapter;
    ScrapComponentListAdapter mComponentListAdapter;

    // DB
    DBHelper db;

    public ScrapFragment() {
        // Required empty public constructor
    }

    public static ScrapFragment newInstance(String type) {
        ScrapFragment fragment = new ScrapFragment();
        Bundle args = new Bundle();
        args.putString(KEY_FRAGMENT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(KEY_FRAGMENT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scrap, container, false);

        db = new DBHelper(getActivity());

        listView = (ListView) view.findViewById(R.id.listView);
        mPharmListAdapter = new ScrapPharmListAdapter();
        mComponentListAdapter = new ScrapComponentListAdapter();

        switch (type) {
            case TYPE_PHARMS:
                // 약국 스크랩 목록
                listView.setAdapter(mPharmListAdapter);
                for (PharmItem item : db.getScrappedPharms()) {
                    mPharmListAdapter.addScrappedPharm(item);
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        mListener.onScrappedPharmItemClicked((PharmItem) mPharmListAdapter.getItem(position));
                    }
                });

                break;
            case TYPE_COMPONENT:
                // 의약품,성분 스크랩 목록
                listView.setAdapter(mComponentListAdapter);
                for (MedicineInfo medicineInfo : db.getScrappedMedicine()) {
                    mComponentListAdapter.addScrappedMedicine(medicineInfo);
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startActivity(new Intent(getActivity(), ComponentActivity.class).putExtra("barcode", ((MedicineInfo) mComponentListAdapter.getItem(position)).getBarcode()));
                    }
                });

                break;
        }

        this.setOnScrappedPharmListItemClickListener((ScrapActivity)getActivity());

        return view;
    }


    public interface OnScrappedPharmListItemClickListener {
        public void onScrappedPharmItemClicked(PharmItem item);
    }
    OnScrappedPharmListItemClickListener mListener;
    public void setOnScrappedPharmListItemClickListener(OnScrappedPharmListItemClickListener listener) {
        mListener = listener;
    }

}
