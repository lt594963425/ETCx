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

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcF;

import com.etcxc.android.R;
import com.etcxc.android.utils.ToastUtils;

import static android.nfc.NfcAdapter.EXTRA_TAG;


public final class NfcManager {

    private final Activity mActivity;
    private NfcAdapter mNfcAdapter;
    private static String[][] TECHS;
    private static IntentFilter[] TAG_FILTERS;
    private int mStatus;

    static {
        TECHS = new String[][]{{IsoDep.class.getName()}, {NfcF.class.getName()},};
        try {
            TAG_FILTERS = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED, "*/*")};
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
    }

    public NfcManager(Activity activity) {
        mActivity = activity;
        mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        mStatus = getStatus();
    }

    public void onPause() {
        if (mNfcAdapter != null) mNfcAdapter.disableForegroundDispatch(mActivity);
    }

    public void onResume() {
        if (!mNfcAdapter.isEnabled()) {
            ToastUtils.showToast(mActivity.getString(R.string.please_open_NFC_function));
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(mActivity, 0, new Intent(
                mActivity, mActivity.getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mNfcAdapter != null) mNfcAdapter.enableForegroundDispatch(mActivity, pendingIntent, TAG_FILTERS, TECHS);
    }

    public boolean updateStatus() {
        int status = getStatus();
        if (status != mStatus) {
            mStatus = status;
            return true;
        }
        return false;
    }

    public boolean readCard(Intent intent, ReaderListener listener) {
        final Tag tag = intent.getParcelableExtra(EXTRA_TAG);
        if (tag != null) {
            ReaderManager.readCard(tag, listener);
            return true;
        }
        return false;
    }

    private int getStatus() {
        return (mNfcAdapter == null) ? -1 : mNfcAdapter.isEnabled() ? 1 : 0;
    }

}
