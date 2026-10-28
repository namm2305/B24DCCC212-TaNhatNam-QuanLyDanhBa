package com.example.thuchanh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class ContactAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private ArrayList<Contact> contacts = new ArrayList<>();
    public ContactAdapter(Context context) { inflater = LayoutInflater.from(context); }
    public void submit(ArrayList<Contact> values) { contacts = values; notifyDataSetChanged(); }
    @Override public int getCount() { return contacts.size(); }
    @Override public Contact getItem(int position) { return contacts.get(position); }
    @Override public long getItemId(int position) { return position; }
    @Override public View getView(int position, View view, ViewGroup parent) {
        if (view == null) view = inflater.inflate(R.layout.item_contact, parent, false);
        Contact c = getItem(position);
        ScreenUi.avatar(view.findViewById(R.id.avatar), c);
        ((TextView) view.findViewById(R.id.contactName)).setText(c.getFullName());
        ((TextView) view.findViewById(R.id.contactPhone)).setText(c.getPhoneNumber());
        ((TextView) view.findViewById(R.id.contactGroup)).setText(c.getGroup());
        view.findViewById(R.id.star).setVisibility(c.isFavorite() ? View.VISIBLE : View.GONE);
        return view;
    }
}
