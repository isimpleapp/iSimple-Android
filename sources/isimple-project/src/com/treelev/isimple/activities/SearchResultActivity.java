package com.treelev.isimple.activities;

import org.holoeverywhere.widget.ExpandableListView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.RadioGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.itemstreecursoradapter.SearchItemTreeCursorAdapter;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.cursorloaders.SelectSectionsItems;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.utils.managers.ProxyManager;

public class SearchResultActivity extends BaseExpandableListActivity implements
		RadioGroup.OnCheckedChangeListener,
		LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {

	public static Integer categoryID;
	public static String locationId;
	public static String SEARCH_QUERY = "search_query";

	private String mLocationId;
	private String mQuery;

	private SearchItemTreeCursorAdapter mTreeSearchAdapter;
	private SearchView mSearchView;

	private View darkView;

	private int mSortBy = ProxyManager.SORT_NAME_AZ;

	int i = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);
		mLocationId = locationId;
		if (locationId == null) {
			setCurrentCategory(0);
		} else {
			setCurrentCategory(1);
		}
		createDrawableMenu();
		RadioGroup rg = (RadioGroup) findViewById(R.id.sort_group);
		rg.setOnCheckedChangeListener(this);

		darkView = findViewById(R.id.category_dark_view);
		darkView.setVisibility(View.GONE);
		darkView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});

		handledIntent(getIntent());

		mTreeSearchAdapter = new SearchItemTreeCursorAdapter(this, null,
				getSupportLoaderManager(), mQuery, categoryID, locationId,
				mSortBy);
		getExpandableListView().setAdapter(mTreeSearchAdapter);
		getSupportLoaderManager().restartLoader(0, null, this);
		getExpandableListView().setOnChildClickListener(this);
		disableOnGroupClick();
	}

	public void showNotFoundView(Cursor cursor, int by) {

		if (cursor != null && cursor.getCount() == 0) {

			if (by == 1 || by == 2) {
				i++;
			}
		}

		if (i == 2) {
			findViewById(R.id.tv_not_found).setVisibility(View.VISIBLE);
			findViewById(R.id.img_not_found).setVisibility(View.VISIBLE);
			findViewById(R.id.sort_group).setVisibility(View.GONE);
			

		} else {
			findViewById(R.id.tv_not_found).setVisibility(View.GONE);
			findViewById(R.id.img_not_found).setVisibility(View.GONE);
			findViewById(R.id.sort_group).setVisibility(View.VISIBLE);

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mEventChangeDataBase) {
			mTreeSearchAdapter.notifyDataSetChanged();
			mEventChangeDataBase = false;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		Analytics.screen_Search(this);
	}

//	@Override
//	public void createNavigationMenuBar() {
//		super.createNavigationMenuBar();
//		if (locationId != null) {
//			getSupportActionBar().setIcon(R.drawable.menu_ico_shop);
//		}
//	}

	@Override
	protected void onNewIntent(Intent newIntent) {
		setIntent(newIntent);
		finish();
		startActivity(getIntent());
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return query.trim().length() < LENGTH_SEARCH_QUERY;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.search, menu);
		final MenuItem mItemSearch = menu.findItem(R.id.menu_search);
		SearchManager searcMenager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		mSearchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		mSearchView.setSearchableInfo(searcMenager
				.getSearchableInfo(getComponentName()));
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setSubmitButtonEnabled(true);
		mSearchView
				.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							darkView.setVisibility(View.VISIBLE);
							darkView.getBackground().setAlpha(150);
							mSearchView.setQuery(mSearchView.getQuery(), true);
						} else {
							darkView.setVisibility(View.GONE);
							mItemSearch.collapseActionView();
							mSearchView.setQuery("", false);
						}
					}
				});
		MenuItem menuItem = menu.findItem(R.id.menu_search);
		menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				darkView.setVisibility(View.GONE);
				return true; // Return true to collapse action view
			}

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				return true;
			}
		});
		mSearchView.setOnQueryTextListener(this);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		i = 0;
		switch (item.getItemId()) {
		case android.R.id.home:
			back();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		back();
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int rgb) {
		int mS = 0;
		switch (rgb) {
		case R.id.alphabet_sort:
			mSortBy = ProxyManager.SORT_NAME_AZ;
			break;
		case R.id.price_sort:
			mSortBy = ProxyManager.SORT_PRICE_UP;
			break;
		}
		if (getExpandableListView().getCount() > 0) {
			mTreeSearchAdapter.setSortBy(mSortBy);
			getSupportLoaderManager().restartLoader(0, null, this);
		} else {

		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Cursor product = mTreeSearchAdapter.getChild(groupPosition,
				childPosition);
		Intent startIntent;
		int itemCountIndex = product.getColumnIndex("count");
		if (product.getInt(itemCountIndex) > 1) {
			int itemDrinkIdIndex = product
					.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_ID);
			startIntent = new Intent(this, CatalogSubCategoryTree.class);
			startIntent.putExtra(SEARCH_QUERY, mQuery);
			startIntent.putExtra(CatalogByCategoryActivityDepracated.DRINK_ID,
					product.getString(itemDrinkIdIndex));
			startIntent.putExtra(ShopInfoActivity.LOCATION_ID, mLocationId);
			startActivity(startIntent);
		} else {
			startIntent = new Intent(this, ProductInfoActivity.class);
			startIntent.putExtra(ProductInfoActivity.ITEM_ID_TAG,
					product.getString(0));
			startActivityForResult(startIntent, 0);
		}
		overridePendingTransition(R.anim.start_show_anim,
				R.anim.start_back_anim);
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}

	private void handledIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String mDrinkId = intent
					.getStringExtra(CatalogListActivityNew.DRINK_ID);
			mQuery = intent.getStringExtra(SearchManager.QUERY);

			if (!mQuery.contains("%")) {

				mQuery = mQuery.trim().replace("_", "").replace("%", "")
						.replace('\\', '_');
			}

		}
	}

	private void back() {
		finish();
		overridePendingTransition(R.anim.finish_show_anim,
				R.anim.finish_back_anim);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new SelectSectionsItems(this,
				ProxyManager.TYPE_SECTION_FILTRATION_SEARCH);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		mTreeSearchAdapter.setGroupCursor(cursor);
		mTreeSearchAdapter.notifyDataSetChanged();
		expandAllGroup();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}
}
