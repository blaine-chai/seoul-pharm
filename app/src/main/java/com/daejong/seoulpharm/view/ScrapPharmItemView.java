package com.daejong.seoulpharm.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.daejong.seoulpharm.R;
import com.daejong.seoulpharm.db.DBHelper;
import com.daejong.seoulpharm.model.PharmItem;
import com.daejong.seoulpharm.widget.NotoTextView;

/**
 * Created by Hyunwoo on 2016. 10. 28..
 */
public class ScrapPharmItemView extends FrameLayout {

    PharmItem item;

    NotoTextView pharmTitleView;
    NotoTextView pharmTelView;
    NotoTextView pharmAddressView;
    NotoTextView pharmAvailableLanguageView;
    ImageView callBtn;
    ImageView deleteScrapBtn;

    public ScrapPharmItemView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_scrap_pharm_list_item, this);

        // VIEW INITIALIZE
        pharmTitleView = (NotoTextView) findViewById(R.id.scrap_pharm_title);
        pharmTelView = (NotoTextView) findViewById(R.id.scrap_pharm_tel);
        pharmAddressView = (NotoTextView) findViewById(R.id.scrap_pharm_address);
        pharmAvailableLanguageView = (NotoTextView) findViewById(R.id.scrap_pharm_available_language);

        callBtn = (ImageView) findViewById(R.id.btn_call);
        deleteScrapBtn = (ImageView) findViewById(R.id.btn_bookmark_delete);

        callBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNum = pharmTelView.getText().toString();
                Uri uri = Uri.parse("tel:" + phoneNum);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                getContext().startActivity(intent);
            }
        });

        deleteScrapBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete item in DB
                DBHelper db = new DBHelper(getContext());
                db.deletePharmScrapped(item.getMainKey());

                // Delete item in Adapter
                mOnDeleteButtonClickListener.onDeleteButtonClicked(item.getMainKey());
            }
        });

    }

    public void initItemViewsLanguage (int currentLanguage, PharmItem item) {
        switch (currentLanguage) {
            case R.drawable.btn_kor :
                pharmTitleView.setText(item.getNameKor());
                pharmAvailableLanguageView.setText("| 외국어 가능 약국 |   "+item.getAvailLanKor());
                pharmAddressView.setText(item.getAddressKor());
                break;
            case R.drawable.btn_eng :
                pharmTitleView.setText(item.getNameEng());
                pharmAvailableLanguageView.setText("| Available language |   "+item.getAvailLanEng());
                pharmAddressView.setText(item.getAddressEng());
                break;
            case R.drawable.btn_china :
                pharmTitleView.setText(item.getNameChi());
                pharmAvailableLanguageView.setText("| 可以讲外语的药店 |   "+item.getAvailLanChi());
                pharmAddressView.setText(item.getAddressEng());
                break;
        }
        pharmTelView.setText(item.getTel());
    }

    public void setScrappedPharmItemView (PharmItem item) {
        this.item = item;

        // Language 에 따른 수정 필요
        pharmTitleView.setText(item.getNameKor());
        pharmTelView.setText(item.getTel());
        pharmAddressView.setText(item.getAddressKor());
    }

    public interface OnDeleteButtonClickListener {
        public void onDeleteButtonClicked(String deleteKey);
    }
    OnDeleteButtonClickListener mOnDeleteButtonClickListener;
    public void setOnDeleteButtonClickListener (OnDeleteButtonClickListener listener) {
        this.mOnDeleteButtonClickListener = listener;
    }

}
