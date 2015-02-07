package com.akifbatur.whichway;

import android.support.v4.app.DialogFragment;

interface DialogClickListener{
	public void onDialogPositiveClick(DialogFragment dialog);
}

interface DialogFavoriteListener{
	public void favoriteChosen(int id);
}