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

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.treelev.isimple.R;
import com.treelev.isimple.adapters.itemstreecursoradapter.CatalogItemTreeCursorAdapter;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.cursorloaders.SelectSectionsItems;
import com.treelev.isimple.data.DatabaseSqlHelper;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.utils.managers.LocationTrackingManager;
import com.treelev.isimple.utils.managers.ProxyManager;

public class CatalogListActivity extends BaseExpandableListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	public final static String CATEGORY_ID = "category_id";
	private View darkView;
	private final static int NAVIGATE_CATEGORY_ID = 0;
	public final static String DRINK_ID = "drink_id";
	public final static String FILTER_WHERE_CLAUSE = "filter_where_clauses";
	private CatalogItemTreeCursorAdapter mListCategoriesAdapter;
	private SearchView mSearchView;
//	private android.widget.SearchView mSearchViewV11;

	@Override
	protected void onCreate(Bundle sSavedInstanceState) {
		super.onCreate(sSavedInstanceState);

		// hook location
		LocationTrackingManager.getInstante().getCurrentLocation(
				getApplicationContext());

		setContentView(R.layout.catalog_list_layout);
		setCurrentCategory(NAVIGATE_CATEGORY_ID);
		createDrawableMenu();
		darkView = findViewById(R.id.dark_view);
		darkView.setVisibility(View.GONE);
		darkView.setOnClickListener(null);
		ExpandableListView expandableView = getExpandableListView();
		View mHeader = getLayoutInflater().inflate(
				R.layout.catalog_list_header_view, expandableView, false);
		expandableView.addHeaderView(mHeader, null, false);
		mListCategoriesAdapter = new CatalogItemTreeCursorAdapter(
				CatalogListActivity.this, null, getSupportLoaderManager(),
				ProxyManager.SORT_NAME_AZ);
		getExpandableListView().setAdapter(mListCategoriesAdapter);
		expandableView.setOnChildClickListener(this);
		disableOnGroupClick();
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void createDrawableMenu() {
		super.createDrawableMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Analytics.screen_Catalog(this);
	}

	@Override
	protected void onResume() {
//		startUpdateService();
		if (mEventChangeDataBase) {
			mListCategoriesAdapter.refresh();
			mEventChangeDataBase = false;
		}
		super.onResume();
	}

	private void initSherlockSaerchView(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.search, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		mSearchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		mSearchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		mSearchView.setIconifiedByDefault(false);
		final MenuItem mItemSearch = menu.findItem(R.id.menu_search);
		mSearchView
				.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							darkView.setVisibility(View.VISIBLE);
							darkView.getBackground().setAlpha(150);
							mSearchView.setQuery(mSearchView.getQuery(), false);
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
		mSearchView
				.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
					@Override
					public boolean onQueryTextSubmit(String query) {
						SearchResultActivity.categoryID = null;
						SearchResultActivity.locationId = null;
						return query.trim().length() < LENGTH_SEARCH_QUERY;
					}

					@Override
					public boolean onQueryTextChange(String newText) {
						return false;
					}
				});
	}

//	private void initSearchView(Menu menu) {
//		getSupportMenuInflater().inflate(R.menu.search_v11, menu);
//		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//		mSearchViewV11 = (android.widget.SearchView) menu.findItem(
//				R.id.menu_search).getActionView();
//		mSearchViewV11.setSearchableInfo(searchManager
//				.getSearchableInfo(getComponentName()));
//		mSearchViewV11.setIconifiedByDefault(false);
//		final MenuItem mItemSearch = menu.findItem(R.id.menu_search);
//		mSearchViewV11
//				.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//					@Override
//					public void onFocusChange(View v, boolean hasFocus) {
//						if (hasFocus) {
//							darkView.setVisibility(View.VISIBLE);
//							darkView.getBackground().setAlpha(150);
//							mSearchViewV11.setQuery(mSearchView.getQuery(),
//									false);
//						} else {
//							darkView.setVisibility(View.GONE);
//							mItemSearch.collapseActionView();
//							mSearchViewV11.setQuery("", false);
//						}
//					}
//				});
//		MenuItem menuItem = menu.findItem(R.id.menu_search);
//		menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//
//			@Override
//			public boolean onMenuItemActionCollapse(MenuItem item) {
//				darkView.setVisibility(View.GONE);
//				return true; // Return true to collapse action view
//			}
//
//			@Override
//			public boolean onMenuItemActionExpand(MenuItem item) {
//				return true;
//			}
//		});
//		mSearchViewV11
//				.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
//					@Override
//					public boolean onQueryTextSubmit(String query) {
//						SearchResultActivity.categoryID = null;
//						SearchResultActivity.locationId = null;
//						return query.trim().length() < LENGTH_SEARCH_QUERY;
//					}
//
//					@Override
//					public boolean onQueryTextChange(String newText) {
//						return false;
//					}
//				});
//	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		initSherlockSaerchView(menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void onClickCategoryButt(View v) {
		Intent startIntent = new Intent(getApplicationContext(),
				CatalogByCategoryActivity.class);
		Integer category = DrinkCategory.getItemCategoryByButtonId(v.getId());
		startIntent.putExtra(CATEGORY_ID, category);
		startActivity(startIntent);
		overridePendingTransition(R.anim.start_show_anim,
				R.anim.start_back_anim);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Cursor product = mListCategoriesAdapter.getChild(groupPosition,
				childPosition);
		Intent startIntent;
		int itemCountIndex = product.getColumnIndex("count");
		if (product.getInt(itemCountIndex) > 1) {
			int itemDrinkIdIndex = product
					.getColumnIndex(DatabaseSqlHelper.ITEM_DRINK_ID);
			startIntent = new Intent(this, CatalogSubCategoryTree.class);
			startIntent.putExtra(DRINK_ID, product.getString(itemDrinkIdIndex));
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

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new SelectSectionsItems(this, ProxyManager.TYPE_SECTION_MAIN);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		mListCategoriesAdapter.setGroupCursor(cursor);
		expandAllGroup();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
	}

//	private void startUpdateService() {
//		boolean downloadDataTaskRunning = DownloadDataService.isDownloadDataTaskRunning();
//		
//		if (!downloadDataTaskRunning && SharedPreferencesManager.isPreparationUpdate(getApplicationContext())) {
//			// This means app crashed, was killed or updated when update was in
//			// progress. Thus we should clear all download flags.
//			Log.v("Test log", "CatalogListActivity clear download update flags");
//			SharedPreferencesManager.setPreparationUpdate(this, false);
//		}
//		if (!SharedPreferencesManager.isPreparationUpdate(getApplicationContext())
//				&& !SharedPreferencesManager.isUpdateReady(getApplicationContext())) {
//			startService(new Intent(getApplicationContext(), DownloadDataService.class));
//		}
//	}
	
}
