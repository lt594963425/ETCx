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

import com.etcxc.android.net.nfc.bean.Card;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public abstract class StandardPboc {

    public static Card readCard(IsoDep tech) throws InstantiationException,
            IllegalAccessException, IOException {
        final Iso7816.StdTag tag = new Iso7816.StdTag(tech);
        tag.connect();
        Card card = readCard(tag);
        tag.close();
        return card;
    }

    protected boolean resetTag(Iso7816.StdTag tag) throws IOException {
        return tag.selectByID(DFI_MF).isOkey() || tag.selectByName(DFN_PSE).isOkey();
    }

    protected final static byte[] DFI_MF = {(byte) 0x3F, (byte) 0x00};
    protected final static byte[] DFI_EP = {(byte) 0x10, (byte) 0x01};
    protected final static byte[] DFN_PSE = {(byte) '1', (byte) 'P', (byte) 'A', (byte) 'Y',
            (byte) '.', (byte) 'S', (byte) 'Y', (byte) 'S', (byte) '.', (byte) 'D', (byte) 'D',
            (byte) 'F', (byte) '0', (byte) '1',};

    protected final static byte[] DFN_PXX = {(byte) 'P'};
    protected final static int SFI_EXTRA = 22;

    protected static int MAX_LOG = 10;
    protected static int SFI_LOG = 24;

    protected final static byte TRANS_CSU = 6;
    protected final static byte TRANS_CSU_CPX = 9;

    protected abstract Object getApplicationId();

    protected static byte[] getMainApplicationId() {
        return DFI_EP;
    }

    protected static boolean havaMainApplication(Iso7816.StdTag tag) throws IOException {
        final byte[] aid = getMainApplicationId();
        return ((aid.length == 2) ? tag.selectByID(aid) : tag.selectByName(aid)).isOkey();
    }

    protected static Card readCard(Iso7816.StdTag tag) throws IOException {
        Card card = new Card();
        Iso7816.Response CARDINFO, PEOPLEINFO, BALANCE;
        if (havaMainApplication(tag)) {
            CARDINFO = tag.readBinary(21);
            parseCardInfo(card, CARDINFO);
            BALANCE = tag.getBalance(0, true);
            parseBalance(card, BALANCE);
        }
        boolean dfi = tag.selectByID(DFI_MF).isOkey();
        if (dfi) {
            final byte[] cmd = {(byte) 0x00, // CLA Class
                    (byte) 0xB0, // INS Instruction
                    (byte) (0x00000096), // P1 Parameter 1//
                    (byte) 0x00, // P2 Parameter 2
                    (byte) 0x00, // Le
            };
            PEOPLEINFO = new Iso7816.Response(tag.transceive(cmd));
            parsePeopleInfo(card, PEOPLEINFO);
        }
        return card;
    }

    protected static float parseBalance(Iso7816.Response data) {
        float ret = 0f;
        if (data.isOkey() && data.size() >= 4) {
            int n = Util.toInt(data.getBytes(), 0, 4);
            if (n > 1000000 || n < -1000000)
                n -= 0x80000000;
            ret = n / 100.0f;
        }
        return ret;
    }

    protected static void parseBalance(Card card, Iso7816.Response... data) {
        float amount = 0f;
        for (Iso7816.Response rsp : data)
            amount += parseBalance(rsp);
        card.blance = String.valueOf(amount);
    }

    protected static void parsePeopleInfo(Card card, Iso7816.Response data) {
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

    protected static void parseCardInfo(Card card, Iso7816.Response data) {
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

}
