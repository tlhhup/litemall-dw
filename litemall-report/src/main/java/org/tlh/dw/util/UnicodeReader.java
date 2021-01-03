package org.tlh.dw.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

/**
 * @author 离歌笑
 * @desc
 * @date 2021-01-03
 */
public class UnicodeReader extends Reader {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final Charset UTF16BE = Charset.forName("UTF-16BE");
    private static final Charset UTF16LE = Charset.forName("UTF-16LE");

    PushbackInputStream internalIn;
    InputStreamReader internalIn2 = null;

    private static final int BOM_SIZE = 3;

    /**
     * @param in InputStream to be read
     */
    public UnicodeReader(InputStream in) {
        internalIn = new PushbackInputStream(in, BOM_SIZE);
    }

    /**
     * Get stream encoding or NULL if stream is uninitialized. Call init() or
     * read() method to initialize it.
     *
     * @return the name of the character encoding being used by this stream.
     */
    public String getEncoding() throws IOException {
        return internalIn2.getEncoding();
    }

    /**
     * Read-ahead four bytes and check for BOM marks. Extra bytes are unread
     * back to the stream, only BOM bytes are skipped.
     *
     * @throws IOException if InputStream cannot be created
     */
    protected void init() throws IOException {
        if (internalIn2 != null)
            return;

        Charset encoding;
        byte bom[] = new byte[BOM_SIZE];
        int n, unread;
        n = internalIn.read(bom, 0, bom.length);

        if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
            encoding = UTF8;
            unread = n - 3;
        } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
            encoding = UTF16BE;
            unread = n - 2;
        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
            encoding = UTF16LE;
            unread = n - 2;
        } else {
            // Unicode BOM mark not found, unread all bytes
            encoding = UTF8;
            unread = n;
        }

        if (unread > 0)
            internalIn.unread(bom, (n - unread), unread);

        // Use given encoding
        CharsetDecoder decoder = encoding.newDecoder().onUnmappableCharacter(
                CodingErrorAction.REPORT);
        internalIn2 = new InputStreamReader(internalIn, decoder);
    }

    public void close() throws IOException {
        init();
        internalIn2.close();
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        init();
        return internalIn2.read(cbuf, off, len);
    }
}
