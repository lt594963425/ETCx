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

import com.etcxc.android.net.nfc.bean.Card;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 卡操作封装
 */
public class StandardPboc {
    //卡MF目录，一级目录
    private final static byte[] DFI_MF = {(byte) 0x3F, (byte) 0x00};
    //卡DF目录，即EP钱包所在的目录，主应用目录
    private final static byte[] DFI_EP = {(byte) 0x10, (byte) 0x01};

    /**
     *读卡
     */
    public static Card readCard(IsoDep tech) throws InstantiationException,
            IllegalAccessException, IOException {
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
            parseCardInfo(card, CARDINFO);
            BALANCE = tag.getBalance(0, true);
            parseBalance(card, BALANCE);
        }
        if (tag.selectByID(DFI_MF).isOkey()) {
            PEOPLEINFO = tag.readBinary(22);
            parsePeopleInfo(card, PEOPLEINFO);
        }
        return card;
    }

    /**
     *解析卡余额数据
     */
    private static float parseBalance(Card card, Iso7816.Response data) {
        float ret = 0f;
        if (data.isOkey() && data.size() >= 4) {
            int n = Util.toInt(data.getBytes(), 0, 4);
            if (n > 1000000 || n < -1000000)
                n -= 0x80000000;
            ret = n / 100.0f;
        }
        card.blance = String.valueOf(ret);
        return ret;
    }

    /**
     *解析持卡人数据
     */
    private static void parsePeopleInfo(Card card, Iso7816.Response data) {
        if (!data.isOkey() || data.size() < 50) return;
        final byte[] d = data.getBytes();
        byte[] newBytes = new byte[20];
        System.arraycopy(d, 2, newBytes, 0, 20);
        try {
            card.owerName = new String(effective(newBytes), "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析卡基本信息
     */
    private static void parseCardInfo(Card card, Iso7816.Response data) {
        if (!data.isOkey() || data.size() < 40) return;
        final byte[] d = data.getBytes();
        byte[] newBytes = new byte[10];
        System.arraycopy(d, 10, newBytes, 0, 10);
        try {
            card.cardId = Util.BCDtoInt(newBytes);
            newBytes = new byte[12];
            System.arraycopy(d, 28, newBytes, 0, 12);
            card.carCardId = new String(effective(newBytes), "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 去掉零
     */
    private static byte[] effective(byte[] bs) {
        List<Byte> bytes = new ArrayList<>();
        for (byte b : bs) {
            if (b != 0) bytes.add(b);
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
    public static byte[] storeInit(StdTag tag){
        try {
            if (tag.selectByID(DFI_EP).isOkey()) {
                byte[] cmd = Util.HexStringToByteArray("805000020B01000003E8130000000001");
                byte[] data = new Iso7816.Response(tag.transceive(cmd)).data;
                return data;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
