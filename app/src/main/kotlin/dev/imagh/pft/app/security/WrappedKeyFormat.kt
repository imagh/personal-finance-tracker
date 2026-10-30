package dev.imagh.pft.app.security

/**
 * The on-disk layout of the wrapped database key (format version 1):
 *
 * ```
 * offset  size  field
 * 0       4     magic "PFTK"
 * 4       1     format version (1)
 * 5       1     IV length (12)
 * 6       12    AES-GCM IV, chosen by the cipher (the Keystore insists on its own random IV)
 * 18      48    AES-GCM ciphertext of the 32-byte key followed by the 16-byte tag
 * ```
 *
 * The first [HEADER_LENGTH] bytes are also the GCM additional authenticated data, so an edited
 * header fails authentication rather than being reinterpreted. A file of any other length, or with
 * another magic, version or IV length, is malformed: decoding never guesses.
 */
internal object WrappedKeyFormat {
    /** The database key length: SQLCipher's raw 256-bit key. */
    const val KEY_LENGTH = 32

    /** The GCM IV length the Keystore uses. */
    const val IV_LENGTH = 12

    /** The GCM tag length in bits. */
    const val TAG_BITS = 128

    private const val VERSION: Byte = 1
    private val MAGIC = byteArrayOf('P'.code.toByte(), 'F'.code.toByte(), 'T'.code.toByte(), 'K'.code.toByte())

    /** Magic, version and IV length; authenticated as GCM AAD. */
    private val HEADER = MAGIC + VERSION + IV_LENGTH.toByte()

    /** Header length in bytes. */
    val HEADER_LENGTH = HEADER.size

    private const val TAG_LENGTH = TAG_BITS / Byte.SIZE_BITS

    /** The exact length of a version-1 file. */
    val ENCODED_LENGTH = HEADER_LENGTH + IV_LENGTH + KEY_LENGTH + TAG_LENGTH

    /** The additional authenticated data for the GCM wrap (a copy of the header). */
    fun aad(): ByteArray = HEADER.copyOf()

    /** Lays out [iv] and [sealed] (ciphertext plus tag) behind the header. */
    fun encode(
        iv: ByteArray,
        sealed: ByteArray,
    ): ByteArray {
        require(iv.size == IV_LENGTH) { "unexpected IV length" }
        require(sealed.size == KEY_LENGTH + TAG_LENGTH) { "unexpected sealed length" }
        return HEADER + iv + sealed
    }

    /** The IV and sealed bytes of a well-formed version-1 file, or null if [bytes] is anything else. */
    fun decode(bytes: ByteArray): Decoded? {
        val wellFormed = bytes.size == ENCODED_LENGTH && bytes.copyOfRange(0, HEADER_LENGTH).contentEquals(HEADER)
        if (!wellFormed) return null
        val ivEnd = HEADER_LENGTH + IV_LENGTH
        return Decoded(iv = bytes.copyOfRange(HEADER_LENGTH, ivEnd), sealed = bytes.copyOfRange(ivEnd, bytes.size))
    }

    /** A decoded file: the GCM [iv] and the [sealed] ciphertext plus tag. */
    class Decoded(
        val iv: ByteArray,
        val sealed: ByteArray,
    )
}
