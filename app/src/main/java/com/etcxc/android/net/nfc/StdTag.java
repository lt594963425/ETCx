package com.etcxc.android.net.nfc;

/**
 * NFC标准tag
 * Created by xwpeng on 2017/9/16.
 */

import android.nfc.tech.IsoDep;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class StdTag {
    private final IsoDep nfcTag;
    private Iso7816.ID id;

    public StdTag(IsoDep tag) {
        nfcTag = tag;
        id = new Iso7816.ID(tag.getTag().getId());
    }

    public Iso7816.ID getID() {
        return id;
    }

    /**
     * 读卡余额
     */
    public Iso7816.Response getBalance(int p1, boolean isEP) throws IOException {
        final byte[] cmd = {(byte) 0x80, // CLA Class
                (byte) 0x5C, // INS Instruction
                (byte) p1, // P1 Parameter 1
                (byte) (isEP ? 2 : 1), // P2 Parameter 2
                (byte) 0x04, // Le
        };

        return new Iso7816.Response(transceive(cmd));
    }

    /**
     * 读卡记录
     * @param sfi   文件标识
     * @param index 记录开始位置
     */
    public Iso7816.Response readRecord(int sfi, int index) throws IOException {
        final byte[] cmd = {(byte) 0x00, // CLA Class
                (byte) 0xB2, // INS Instruction
                (byte) index, // P1 Parameter 1
                (byte) ((sfi << 3) | 0x04), // P2 Parameter 2
                (byte) 0x00, // Le
        };
        return new Iso7816.Response(transceive(cmd));
    }

    /**
     * 读卡记录
     * @param sfi 文件标识
     */
    public Iso7816.Response readRecord(int sfi) throws IOException {
        final byte[] cmd = {(byte) 0x00, // CLA Class
                (byte) 0xB2, // INS Instruction
                (byte) 0x01, // P1 Parameter 1
                (byte) ((sfi << 3) | 0x05), // P2 Parameter 2
                (byte) 0x00, // Le
        };
        return new Iso7816.Response(transceive(cmd));
    }

    /**
     * 读透明文件
     * @param sfi 文件标识
     */
    public Iso7816.Response readBinary(int sfi) throws IOException {
        final byte[] cmd = {(byte) 0x00, // CLA Class
                (byte) 0xB0, // INS Instruction
                (byte) (0x00000080 | (sfi & 0x1F)), // P1 Parameter 1//
                (byte) 0x00, // P2 Parameter 2
                (byte) 0x00, // Le
        };
        return new Iso7816.Response(transceive(cmd));
    }

    public Iso7816.Response readData(int sfi) throws IOException {
        final byte[] cmd = {(byte) 0x80, // CLA Class
                (byte) 0xCA, // INS Instruction
                (byte) 0x00, // P1 Parameter 1
                (byte) (sfi & 0x1F), // P2 Parameter 2
                (byte) 0x00, // Le
        };
        return new Iso7816.Response(transceive(cmd));
    }

    public Iso7816.Response getData(short tag) throws IOException {
        final byte[] cmd = {
                (byte) 0x80, // CLA Class
                (byte) 0xCA, // INS Instruction
                (byte) ((tag >> 8) & 0xFF), (byte) (tag & 0xFF),
                (byte) 0x00, // Le
        };
        return new Iso7816.Response(transceive(cmd));
    }

    public Iso7816.Response readData(short tag) throws IOException {
        final byte[] cmd = {(byte) 0x80, // CLA Class
                (byte) 0xCA, // INS Instruction
                (byte) ((tag >> 8) & 0xFF), // P1 Parameter 1
                (byte) (tag & 0x1F), // P2 Parameter 2
                (byte) 0x00, // Lc
                (byte) 0x00, // Le
        };
        return new Iso7816.Response(transceive(cmd));
    }

    /**
     * 选择文件或应用
     */
    public Iso7816.Response selectByID(byte... id) throws IOException {
        ByteBuffer buff = ByteBuffer.allocate(id.length + 6);
        buff.put((byte) 0x00) // CLA Class
                .put((byte) 0xA4) // INS Instruction
                .put((byte) 0x00) // P1 Parameter 1
                .put((byte) 0x00) // P2 Parameter 2
                .put((byte) id.length) // Lc
                .put(id).put((byte) 0x00); // Le

        return new Iso7816.Response(transceive(buff.array()));
    }

    public Iso7816.Response selectByName(byte... name) throws IOException {
        ByteBuffer buff = ByteBuffer.allocate(name.length + 6);
        buff.put((byte) 0x00) // CLA Class
                .put((byte) 0xA4) // INS Instruction
                .put((byte) 0x04) // P1 Parameter 1
                .put((byte) 0x00) // P2 Parameter 2
                .put((byte) name.length) // Lc
                .put(name).put((byte) 0x00); // Le

        return new Iso7816.Response(transceive(buff.array()));
    }

    public Iso7816.Response getProcessingOptions(byte... pdol) throws IOException {
        ByteBuffer buff = ByteBuffer.allocate(pdol.length + 6);
        buff.put((byte) 0x80) // CLA Class
                .put((byte) 0xA8) // INS Instruction
                .put((byte) 0x00) // P1 Parameter 1
                .put((byte) 0x00) // P2 Parameter 2
                .put((byte) pdol.length) // Lc
                .put(pdol).put((byte) 0x00); // Le

        return new Iso7816.Response(transceive(buff.array()));
    }

    public void connect() throws IOException {
        nfcTag.connect();
    }

    public void close() throws IOException {
        nfcTag.close();
    }

    /**
     * 执行命令
     */
    public byte[] transceive(final byte[] cmd) throws IOException {
        try {
            byte[] rsp = null;
            byte c[] = cmd;
            do {
                byte[] r = nfcTag.transceive(c);
                if (r == null) break;
                int N = r.length - 2;
                if (N < 0) {
                    rsp = r;
                    break;
                }
                if (r[N] == CH_STA_LE) {
                    c[c.length - 1] = r[N + 1];
                    continue;
                }
                if (rsp == null) {
                    rsp = r;
                } else {
                    int n = rsp.length;
                    N += n;
                    rsp = Arrays.copyOf(rsp, N);
                    n -= 2;
                    for (byte i : r)
                        rsp[n++] = i;
                }
                if (r[N] != CH_STA_MORE) break;
                byte s = r[N + 1];
                if (s != 0) {
                    c = CMD_GETRESPONSE.clone();
                } else {
                    rsp[rsp.length - 1] = CH_STA_OK;
                    break;
                }
            } while (true);
            return rsp;
        } catch (Exception e) {
            return Iso7816.Response.ERROR;
        }
    }
    //命令执行成功
    private static final byte CH_STA_OK = (byte) 0x90;
    //数据太长
    private static final byte CH_STA_MORE = (byte) 0x61;
    //长度错误
    private static final byte CH_STA_LE = (byte) 0x6C;
    private static final byte CMD_GETRESPONSE[] = {0, (byte) 0xC0, 0, 0, 0,};
}