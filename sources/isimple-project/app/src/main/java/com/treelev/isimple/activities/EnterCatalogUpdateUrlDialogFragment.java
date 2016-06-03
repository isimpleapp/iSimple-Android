package com.treelev.isimple.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.treelev.isimple.R;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

public class EnterCatalogUpdateUrlDialogFragment extends DialogFragment {

	private CatalogUpdateUrlChangeListener mListener;
	
	public EnterCatalogUpdateUrlDialogFragment() {
		super();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (CatalogUpdateUrlChangeListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement AddActionDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View dialogLyaout = inflater.inflate(R.layout.dialog_enter_catalog_update_url, null);

		final EditText url = ((EditText) dialogLyaout
				.findViewById(R.id.dialog_edit_url_value));
		url.setText(SharedPreferencesManager.getUpdateFileUrl());
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(dialogLyaout)
				// Add action buttons
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								if (mListener != null) {
									mListener.onCatalogUpdateUrlChanged(url.getText().toString());
								}
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								EnterCatalogUpdateUrlDialogFragment.this.getDialog().cancel();
							}
						});
		return builder.create();
	}
	
	public interface CatalogUpdateUrlChangeListener {
		public void onCatalogUpdateUrlChanged(String url);
	}

}
