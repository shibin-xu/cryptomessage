package crypto;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Header {
    /**
     * Header format:
     *                |----------------|
     *                | positions[0]   |
     *                | lengths[0]     |
     *                | .              |        |-----------|
     *                | .              |        | AES(file) |
     *                | .              |        |           |
     *                | positions[n-1] |        |-----------|
     *                | lengths[n-1]   |   ===> | header    |
     *                |----------------|        |-----------|
     *                | nameKeys[0]    |
     *                | .              |
     *                | .              |
     *                | .              |
     *                | nameKeys[m-1]  |
     *                |----------------|
     *                | n              |
     *                |----------------|
     *                | m              |
     *                |----------------|
     *                | offset         |
     *                |----------------|
     *
     */

    // let f be the physical file where this Header is stored in
    private int pointer;             // offset in f where this Header starts
    private List<Integer> positions; // offsets in f for encryption blocks
    private List<Integer> lengths;   // lengths of encryption blocks
    private List<NameKey> nameKeys;  // {username, pub(AES)} pairs

    /**
     * Construct an empty Header with {pointer, keyEncrypted} unset.
     */
    public Header() {
        this.positions = new ArrayList<Integer>();
        this.lengths = new ArrayList<Integer>();
        this.nameKeys = new ArrayList<NameKey>();
    }

    /**
     * Construct a Header from bytes read from in.
     * Assume in has file format:
     *                            |-----------|
     *                            | AES(file) |
     *                            |           |
     *                            |-----------|
     *                            | header    |
     *                            |-----------|
     *
     * @param in input stream
     * @throws IOException
     */
    public Header(RandomAccessFile in) throws IOException {
        in.seek(in.length() - 4);
        int pointer = in.readInt();
        in.seek(pointer);
        byte[] arr = new byte[(int)in.length() - pointer];
        in.seek(pointer);
        in.read(arr);
        setupHeaderFromBytes(arr);
    }

    /**
     * Construct a Header from bytes.
     * Assume arr are created by toBytes().
     *
     * @param arr byte representation of a Header
     */
    public Header(byte[] arr) {
        setupHeaderFromBytes(arr);
    }

    /**
     * Reset this Header from bytes.
     * Assume arr are created by toBytes().
     *
     * @param arr byte representation of a Header
     */
    private void setupHeaderFromBytes(byte[] arr) {
        this.positions = new ArrayList<Integer>();
        this.lengths = new ArrayList<Integer>();
        this.nameKeys = new ArrayList<NameKey>();
        ByteBuffer buffer = ByteBuffer.allocate(arr.length);
        buffer.put(arr);
        buffer.rewind();
        int n = buffer.getInt(arr.length - 4*3);
        int m = buffer.getInt(arr.length - 4*2);
        this.pointer =  buffer.getInt(arr.length - 4);

        for (int i = 0; i < n; i++) {
            positions.add(buffer.getInt());
            lengths.add(buffer.getInt());
        }
        for (int i = 0; i < m; i++) {
            byte[] name = new byte[NameKey.NAME_SIZE];
            byte[] key = new byte[NameKey.KEY_SIZE];
            buffer.get(name);
            buffer.get(key);
            nameKeys.add(new NameKey(name, key));
        }
    }

    /**
     * Set pointer of this Header.
     *
     * @param pointer to set
     * @throws IllegalArgumentException
     */
    public void setPointer(int pointer) throws IllegalArgumentException {
        if (pointer < 0)
            throw new IllegalArgumentException("Header.setPointer() pointer must be >= 0");
        this.pointer = pointer;
    }

    /**
     * Add {position, length} to this Header.
     *
     * @param position the offset of AES encryption block in f
     * @param length the size of AES encryption block in f
     * @throws IllegalArgumentException
     */
    public void add(int position, int length) throws IllegalArgumentException {
        if (position < 0)
            throw new IllegalArgumentException("Header.add() position must be >= 0");
        if (length <= 0)
            throw new IllegalArgumentException("Header.add() length must be > 0");

        positions.add(position);
        lengths.add(length);
    }

    /**
     * Add a {username, pub(AES)} pair
     *
     * @param nameKey
     * @throws NullPointerException
     */
    public void addNameKey(NameKey nameKey) throws NullPointerException {
        if (nameKey == null)
            throw new NullPointerException("Header.addNameKey() nameKey must not be null");

        nameKeys.add(nameKey);
    }

    /**
     * Convert this Header to bytes.
     * Refer header format.
     *
     * @return byte representation of this Header.
     */
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(positions.size()*4 + lengths.size()*4 +
                nameKeys.size()*(NameKey.SIZE) + 4*3);
        for (int i = 0; i < positions.size(); i++) {
            buffer.putInt(positions.get(i));
            buffer.putInt(lengths.get(i));
        }
        for (NameKey each : nameKeys) {
            buffer.put(each.getName());
            buffer.put(each.getKey());
        }

        buffer.putInt(positions.size());
        buffer.putInt(nameKeys.size());
        buffer.putInt(pointer);
        byte[] arr = new byte[buffer.capacity()];
        buffer.rewind();
        buffer.get(arr, 0, arr.length);

        return arr;
    }

    /**
     * @return a copy of positions
     */
    public List<Integer> getPositions() {
        return new ArrayList<Integer>(positions);
    }

    /**
     * @return a copy of lengths
     */
    public List<Integer> getLengths() {
        return new ArrayList<Integer>(lengths);
    }

    /**
     * @return a copy of namekeys
     */
    public List<NameKey> getNameKeys() {
        return new ArrayList<NameKey>(nameKeys);
    }

    /**
     * @return pointer
     */
    public int getPointer() {
        return pointer;
    }
}