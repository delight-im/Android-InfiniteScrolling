package im.delight.android.infinitescrolling;

/**
 * Copyright 2014 www.delight.im <info@delight.im>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/** OnScrollListener which can be added to ListViews or GridViews in order to enable infinite scrolling */
abstract public class InfiniteScrollListener implements OnScrollListener {

	protected static final int DEFAULT_MIN_ITEMS_LEFT = 10;
	protected static final int DEFAULT_MAX_PAGES = 4;
	protected final int mMinItemsLeft;
	protected final int mMaxPages;
	protected int mPagesLoaded;
	protected int mItemsTotal;
	protected boolean mIsLoading;
	protected boolean mEnabled;

	public InfiniteScrollListener() {
		this(DEFAULT_MIN_ITEMS_LEFT);
	}

	public InfiniteScrollListener(int minItemsLeft) {
		this(minItemsLeft, DEFAULT_MAX_PAGES);
	}

	public InfiniteScrollListener(int minItemsLeft, int maxPages) {
		mMinItemsLeft = minItemsLeft;
		mMaxPages = maxPages;
		mPagesLoaded = -1;
		mItemsTotal = 0;
		mIsLoading = true;
		mEnabled = true;
	}

	abstract public void onReloadItems(int pageToRequest);
	abstract public void onReloadFinished();

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int itemsTotal) {
		if (mEnabled) {
			if (mIsLoading) { // if we still seem to be reloading new items
				if (itemsTotal > mItemsTotal) { // if the list now has more items than we thought
					onReloadFinished();
					mIsLoading = false; // loading seems to have finished as we have more items now
					mItemsTotal = itemsTotal; // update the total item count
					mPagesLoaded++; // we have successfully loaded a new page
				}
			}
			else if (mPagesLoaded < mMaxPages) { // if we are not currently reloading new items and we may still load new pages
				if ((firstVisibleItem+visibleItemCount) > (itemsTotal-mMinItemsLeft)) { // if we have crossed the threshold for reloading
					onReloadItems(mPagesLoaded+1); // notify the callback that we need to reload new items
					mIsLoading = true; // we are now waiting for the reload to finish
				}
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) { }

	public void setEnabled(boolean enabled) {
		mEnabled = enabled;
	}

}