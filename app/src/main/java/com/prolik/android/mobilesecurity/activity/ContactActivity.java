package com.prolik.android.mobilesecurity.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.prolik.android.mobilesecurity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactActivity extends AppCompatActivity {

    private ListView mListView;
    private List<Map<String,String>> mContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        mContactList = readContact();
        mListView = (ListView) findViewById(R.id.lv_contact);
        /*
        mListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mContactList.size();
            }

            @Override
            public Object getItem(int position) {
                return mContactList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = View.inflate(ContactActivity.this,R.layout.contact_list_item,null);
                TextView tvName = (TextView) view.findViewById(R.id.tv_name);
                TextView tvPhone = (TextView) view.findViewById(R.id.tv_phone);
                tvName.setText(mContactList.get(position).get("name"));
                tvPhone.setText(mContactList.get(position).get("phone"));
                return view;
            }
        });
        */

        mListView.setAdapter(new SimpleAdapter(this,mContactList,R.layout.contact_list_item
                ,new String[]{"name","phone"},new int[]{R.id.tv_name,R.id.tv_phone}));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone  = mContactList.get(position).get("phone");
                Intent intent = new Intent();
                intent.putExtra("phone",phone);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    private List<Map<String,String>> readContact(){
        List<Map<String,String>> result = new ArrayList<Map<String,String>>();
        // 首先,从raw_contacts中读取联系人的id("contact_id")
        // 其次, 根据contact_id从data表中查询出相应的电话号码和联系人名称
        // 然后,根据mimetype来区分哪个是联系人,哪个是电话号码
        Uri rawContactsUri = Uri
                .parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        Cursor rawCursor = getContentResolver().query(rawContactsUri, new String[]{"contact_id"}, null, null, null);
        if(rawCursor!= null){
            while (rawCursor.moveToNext()){
                String contactId = rawCursor.getString(0);
                Cursor dataCursor = getContentResolver().query(dataUri
                        , new String[]{"data1", "mimetype"}, "contact_id = ?", new String[]{contactId},null);
                if(dataCursor != null){
                   Map<String, String> map = new HashMap<String, String>();
                    while (dataCursor.moveToNext()){

                        String data1 = dataCursor.getString(0);
                        String mimetype = dataCursor.getString(1);
                        if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            map.put("phone", data1);
                        } else if ("vnd.android.cursor.item/name"
                                .equals(mimetype)) {
                            map.put("name", data1);
                        }
                    }
                    result.add(map);
                    dataCursor.close();
                }
            }
            rawCursor.close();

        }

        return result;

    }
}
