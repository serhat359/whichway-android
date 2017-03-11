package com.akifbatur.whichway;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public abstract class FavoriteAdapter<E>extends BaseAdapter{
	private final List<E> mData;

	public FavoriteAdapter(List<E> map){
		mData = map;
	}

	@Override
	public int getCount(){
		return mData.size();
	}

	@Override
	public E getItem(int position){
		return mData.get(position);
	}

	@Override
	public long getItemId(int position){
		return elementToInt(mData.get(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		final View result;

		if(convertView == null){
			result = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_expandable_list_item_1,
					parent, false);
		}
		else{
			result = convertView;
		}

		E element = getItem(position);

		((TextView)result.findViewById(android.R.id.text1)).setText(elementToString(element));

		return result;
	}

	abstract String elementToString(E element);

	abstract int elementToInt(E element);
}