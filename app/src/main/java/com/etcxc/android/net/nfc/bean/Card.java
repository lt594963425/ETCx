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

package com.etcxc.android.net.nfc.bean;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Card implements Parcelable {
    //内部卡号
    public String cardId;
    //持卡人/组织名称
    public String owerName;
    //卡关联车牌号
    public String carCardId;
    //卡内实时余额
    public String blance;

    public Card() {

    }

    protected Card(Parcel in) {
        cardId = in.readString();
        owerName = in.readString();
        carCardId = in.readString();
        blance = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardId);
        dest.writeString(owerName);
        dest.writeString(carCardId);
        dest.writeString(blance);
    }

    public boolean isAvailable() {
        return !(TextUtils.isEmpty(owerName)
                || TextUtils.isEmpty(cardId)
                || TextUtils.isEmpty(carCardId)
                || TextUtils.isEmpty(blance));
    }
}
