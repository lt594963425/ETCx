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

import java.util.Arrays;

/**
 * iso7816协议标准，封装命令固定部分
 */
public class Iso7816 {
    private static final byte[] EMPTY = {0};
    protected byte[] data;

    protected Iso7816() {
        data = Iso7816.EMPTY;
    }

    protected Iso7816(byte[] bytes) {
        data = (bytes == null) ? Iso7816.EMPTY : bytes;
    }

    public boolean match(byte[] bytes) {
        return match(bytes, 0);
    }

    public boolean match(byte[] bytes, int start) {
        final byte[] data = this.data;
        if (data.length > bytes.length - start) return false;
        for (final byte v : data) {
            if (v != bytes[start++]) return false;
        }
        return true;
    }

    public boolean match(byte tag) {
        return (data.length == 1 && data[0] == tag);
    }

    public boolean match(short tag) {
        final byte[] data = this.data;
        if (data.length == 2) {
            final byte d0 = (byte) (0x000000FF & (tag >> 8));
            final byte d1 = (byte) (0x000000FF & tag);
            return (data[0] == d0 && data[1] == d1);
        }
        return (tag >= 0 && tag <= 255) ? match((byte) tag) : false;
    }

    public int size() {
        return data.length;
    }

    public byte[] getBytes() {
        return data;
    }

    public byte[] getBytes(int start, int count) {
        return Arrays.copyOfRange(data, start, start + count);
    }

    public int toInt() {
        return Util.toInt(getBytes());
    }

    public int toIntR() {
        return Util.toIntR(getBytes());
    }

    @Override
    public String toString() {
        return Util.toHexString(data, 0, data.length);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Iso7816)) return false;
        return match(((Iso7816) obj).getBytes(), 0);
    }

    public final static class ID extends Iso7816 {
        public ID(byte... bytes) {
            super(bytes);
        }
    }

    /**
     * 执行命令后的响应
     */
    public static class Response extends Iso7816 {
        public static final byte[] EMPTY = {};
        public static final byte[] ERROR = {0x6F, 0x00}; // SW_UNKNOWN,Sw1 + Sw2

        public Response(byte[] bytes) {
            super((bytes == null || bytes.length < 2) ? Response.ERROR : bytes);
        }

        public byte getSw1() {
            return data[data.length - 2];
        }

        public byte getSw2() {
            return data[data.length - 1];
        }

        /**
         * 返回hexString的sw12值
         */
        public String getSw12String() {
            int sw1 = getSw1() & 0x000000FF;
            int sw2 = getSw2() & 0x000000FF;
            return String.format("0x%02X%02X", sw1, sw2);
        }

        /**
         * 返回二进制的sw12值
         */
        public short getSw12() {
            final byte[] d = this.data;
            int n = d.length;
            return (short) ((d[n - 2] << 8) | (0xFF & d[n - 1]));
        }

        public boolean isOkey() {
            return equalsSw12(SW_NO_ERROR);
        }

        public boolean equalsSw12(short val) {
            return getSw12() == val;
        }

        public int size() {
            return data.length - 2;
        }

        public byte[] getBytes() {
            return isOkey() ? Arrays.copyOfRange(data, 0, size()) : Response.EMPTY;
        }
    }

    //命令执行成功
    public static final short SW_NO_ERROR = (short) 0x9000;
    //
    public static final short SW_DESFIRE_NO_ERROR = (short) 0x9100;
    //
    public static final short SW_BYTES_REMAINING_00 = 0x6100;
    //Lc 或 Le 长度错
    public static final short SW_WRONG_LENGTH = 0x6700;
    //不满足安全状态
    public static final short SW_SECURITY_STATUS_NOT_SATISFIED = 0x6982;
    //认证方法锁定
    public static final short SW_FILE_INVALID = 0x6983;
    //引用数据无效（未申请随机数）
    public static final short SW_DATA_INVALID = 0x6984;
    //使用条件不满足
    public static final short SW_CONDITIONS_NOT_SATISFIED = 0x6985;
    //没有选择当前文件
    public static final short SW_COMMAND_NOT_ALLOWED = 0x6986;
    //
    public static final short SW_APPLET_SELECT_FAILED = 0x6999;
    //数据域参数不正确
    public static final short SW_WRONG_DATA = 0x6A80;
    //功能不支持
    public static final short SW_FUNC_NOT_SUPPORTED = 0x6A81;
    //未找到文件
    public static final short SW_FILE_NOT_FOUND = 0x6A82;
    public static final short SW_RECORD_NOT_FOUND = 0x6A83;
    public static final short SW_INCORRECT_P1P2 = 0x6A86;
    public static final short SW_WRONG_P1P2 = 0x6B00;
    public static final short SW_CORRECT_LENGTH_00 = 0x6C00;
    public static final short SW_INS_NOT_SUPPORTED = 0x6D00;
    public static final short SW_CLA_NOT_SUPPORTED = 0x6E00;
    public static final short SW_UNKNOWN = 0x6F00;
    public static final short SW_FILE_FULL = 0x6A84;
}
