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

import android.nfc.tech.IsoDep;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.etcxc.android.net.nfc.bean.Card;
import com.etcxc.android.ui.activity.BleStoreActivity;
import com.etcxc.android.utils.LogUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * 命令处理类
 */
public class CmdHandler {
    //卡MF目录，一级目录
    private final static byte[] DFI_MF = {(byte) 0x3F, (byte) 0x00};
    //卡DF目录，即EP钱包所在的目录，主应用目录
    private final static byte[] DFI_EP = {(byte) 0x10, (byte) 0x01};

    public static Card readCard(IsoDep tech) throws Exception {
        final StdTag tag = new StdTag(tech);
        tag.connect();
        Card card = readCard(tag);
        tag.close();
        return card;
    }

    /**
     * 读卡，0016文件持卡人信息,0015文件卡基本信息，卡余额
     */
    private static Card readCard(StdTag tag) throws IOException {
        Card card = new Card();
        Iso7816.Response CARDINFO, PEOPLEINFO, BALANCE;
        if (tag.selectByID(DFI_EP).isOkey()) {
            CARDINFO = tag.readBinary(21);
            Pair<String, String> p = parseCardInfo(CARDINFO);
            if (p != null) {
                card.cardId = p.first;
                card.carCardId = p.second;
            }
            BALANCE = tag.getBalance(0, true);
            card.blance = String.valueOf(parseBalance(BALANCE));
        }
        if (tag.selectByID(DFI_MF).isOkey()) {
            PEOPLEINFO = tag.readBinary(22);
            card.owerName = parsePeopleInfo(PEOPLEINFO);
        }
        return card;
    }

    /**
     * 解析卡余额数据
     */
    private static float parseBalance(Iso7816.Response data) {
        float ret = 0f;
        if (data.isOkey() && data.size() >= 4) {
            int n = Util.toInt(data.getBytes(), 0, 4);
            if (n > 1000000 || n < -1000000)
                n -= 0x80000000;
            ret = n / 100.0f;
        }
        return ret;
    }

    /**
     * 解析持卡人数据
     */
    @Nullable
    private static String parsePeopleInfo(Iso7816.Response data) {
        if (!data.isOkey() || data.size() < 50) return null;
        final byte[] d = data.getBytes();
        byte[] newBytes = new byte[20];
        System.arraycopy(d, 2, newBytes, 0, 20);
        try {
            return new String(effective(newBytes), "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析卡基本信息
     */
    @Nullable
    public static Pair<String, String> parseCardInfo(Iso7816.Response data) {
        if (!data.isOkey() || data.size() < 40) return null;
        final byte[] d = data.getBytes();
        byte[] newBytes = new byte[10];
        System.arraycopy(d, 10, newBytes, 0, 10);
        try {
            String cardId = Util.toHexString(newBytes, 0, newBytes.length);
            newBytes = new byte[12];
            System.arraycopy(d, 28, newBytes, 0, 12);
            String carCardId = new String(effective(newBytes), "GB2312");
            return new Pair<>(cardId, carCardId);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e(TAG, "parseCardInfo", e);
        }
        return null;
    }

    /**
     * 金额去掉前面的零
     */
    private static byte[] effective(byte[] bs) {
        boolean flag = false;
        List<Byte> bytes = new ArrayList<>();
        for (byte b : bs) {
            if (!flag && b != 0) flag = true;
            if (flag) bytes.add(b);
        }
        bs = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            bs[i] = bytes.get(i);
        }
        return bs;
    }

    /**
     * 圈存初始化获得MAC1
     */
    @Nullable
    private static String storeMac1(StdTag tag) throws Exception {
        if (tag.selectByID(DFI_EP).isOkey()) {
            byte[] pin = Util.HexStringToByteArray("0020000003123456");
            Iso7816.Response pinResponse = new Iso7816.Response(tag.transceive(pin));
            if (!pinResponse.isOkey()) return null;
            byte[] mac1Init = Util.HexStringToByteArray(BleStoreActivity.storeInitCmd(1000));
            Iso7816.Response mac1Response = new Iso7816.Response(tag.transceive(mac1Init));
            if (!mac1Response.isOkey()) return null;
            return Util.ByteArrayToHexString(mac1Response.data);
        }
        return null;
    }

    /**
     * 拿卡号
     */
    @Nullable
    private static String getCardId(StdTag tag) throws IOException {
        if (tag.selectByID(DFI_EP).isOkey()) {
            Iso7816.Response CARDINFO = tag.readBinary(21);
            Pair<String, String> p = parseCardInfo(CARDINFO);
            tag.close();
            if (p == null) return null;
            else return p.first;
        }
        return null;
    }

    public static String getCardId(IsoDep tech) throws IOException {
        final StdTag tag = new StdTag(tech);
        tag.connect();
        String cardId = getCardId(tag);
        tag.close();
        return cardId;
    }

    public static String storeMac1(IsoDep tech) throws Exception {
        final StdTag tag = new StdTag(tech);
        tag.connect();
        String mac1 = storeMac1(tag);
        tag.close();
        return mac1;
    }

}
