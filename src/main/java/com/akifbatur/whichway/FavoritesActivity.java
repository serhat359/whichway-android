package com.akifbatur.whichway;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class FavoritesActivity extends Activity{

	private enum FavoriteAction{
		Delete(0);

		public final int value;

		FavoriteAction(int i){
			value = i;
		}
	}

	private FavoriteAction[] favoriteActions;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites);

		favoriteActions = FavoriteAction.values();

		setUpFavoriteList();
	}

	private void setUpFavoriteList(){
		ListView listView = (ListView)findViewById(R.id.listView1);

		ArrayList<Favorite> favorites = getDB().getAllFavorites();

		FavoriteAdapter<Favorite> adapter = new FavoriteAdapter<Favorite>(favorites){

			@Override
			String elementToString(Favorite element){
				return element.location;
			}

			@Override
			int elementToInt(Favorite element){
				return element.id;
			}

		};

		listView.setAdapter(adapter);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				@SuppressWarnings("unchecked")
				AdapterView<FavoriteAdapter<Favorite>> adapterView = (AdapterView<FavoriteAdapter<Favorite>>)parent;
				Favorite selectedFavorite = adapterView.getAdapter().getItem(position);
				Intent intent = new Intent();
				setResult(selectedFavorite.id, intent);
				finish();
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		if(v.getId() == R.id.listView1){
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

			menu.setHeaderTitle(getDB().getFavoriteByID((int)info.id).location);

			for(int i = 0; i < favoriteActions.length; i++){
				menu.add(Menu.NONE, i, i, favoriteActions[i].name());
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int selectedActionID = item.getItemId();
		Favorite selectedFavorite = getDB().getFavoriteByID((int)info.id);

		if(selectedActionID == FavoriteAction.Delete.value){
			getDB().deleteFavorite(selectedFavorite);
			refreshActivity();
		}

		return true;
	}

	private void refreshActivity(){
		finish();
		startActivity(getIntent());
	}

	private DatabaseHandler getDB(){
		return HelloAndroidActivity.db;
	}
}
