/* NFCard is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

NFCard is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Wget.  If not, see <http://www.gnu.org/licenses/>.

Additional permission under GNU GPL version 3 section 7 */

package com.etcxc.android.net.nfc;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;

import com.etcxc.android.net.nfc.bean.Card;

public final class ReaderManager extends AsyncTask<Tag, Integer, Card> {
	private ReaderListener mReadListener;

	public static void readCard(Tag tag, ReaderListener listener) {
		new ReaderManager(listener).execute(tag);
	}

	private ReaderManager(ReaderListener listener) {
		mReadListener = listener;
	}

	@Override
	protected Card doInBackground(Tag... detectedTag) {
		return readCard(detectedTag[0]);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	if (mReadListener != null) mReadListener.onReadEvent(values[0]);

	}

	@Override
	protected void onPostExecute(Card card) {
		if (mReadListener != null)
			mReadListener.onReadEvent(SPEC.FINISHED, card);
	}

	private Card readCard(Tag tag) {
		 Card card = new Card();
		try {
			publishProgress(SPEC.READING);
			final IsoDep isodep = IsoDep.get(tag);
			if (isodep != null) card = StandardPboc.readCard(isodep);
		} catch (Exception e) {
			publishProgress(SPEC.READING);
		}
		return card;
	}
}
