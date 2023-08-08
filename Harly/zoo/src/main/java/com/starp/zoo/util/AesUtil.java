package com.starp.zoo.util;

import com.starp.zoo.constant.Constant;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * @author curry by 2022/11/1
 */
public class AesUtil {
	private static final String KEY = "vTiQ51WWHd0wWm1ownYXTA==";

	private String rc4Key = "YjdjeWxFTzhob1BQZnF6OA==";

	private String greyNewKey0718 = "NztbOJwyUUDO7fjcIzKBCXFXkiIzTTggtq86GuoVg7X/CdVlBX1efg8xc1m5LqdxeBElvpMHkJSBY2ym9u8hhOFQPUnjVUD/HNUJkIUhKlj1dvBI+2DskSr/BxLePmM5rcHjIiUyOqwGEJfHX4s2qmVCZ/xAnpPBpfBjnEkJJPPDLC6pxv6QldxKUmZoDreaI5tGtcdTfWvF7WBebc2826gwKmEN/aeipPGS3dzqFMD7C2G5KC/OthxaWYKoFJNzuacL3DuVqt+gsWyvIN1ByH7ypIXY96crBjlzIygOA1ULJhT76WGVjKiXkVC9an+4BWy1LwikmQh1VJGEo7BlN7sDV7ro90CllsVGetMGZMdy83g/JNPAZ/0hh6PctDGi4o8GqnkwueEgoKRQd8u+31kUtMzzgK2pDnf2iGJpaIJlPzfXRop2MfYpF0HceN7+xPe75hi0frtBrEgcUymAavGA1cUn4+kaq+C9YKyk2nYZKFsUFhFiAxMTq/naY5DUWQVh4v5xq/WWwufGWow54zOkRXE42tlF2s0cteyOSndVA/hcubVGyWlbZYlKXqOZnCoOsGpSw9jjt8Pj5xljcHlkqa/WKTsV/sXX8RfvenEq9IF9Jt6bRPawdpEf84FXq7nD67lyVu1CfU30ED8RbqQnrUoOHS8WOZRW7AcQJEM=";

	private static final String IV = "EPWKTF18pzql2eoUXDO7Mw==";

	private SecretKeySpec secretKeySpec = null;

	private IvParameterSpec ivParameterSpec = null;

	private byte[] sm4Key = new byte[]{-40, 70, 89, -119, 121, 102, 83, 94, -19, 23, 81, -35, -40, 69, 40, 122};

	private boolean sm4Flag = false;

	private boolean greyNewFlag0718 = false;

	public AesUtil() {
		secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(KEY), "AES");
		ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(IV));
	}

	public AesUtil(byte[] key) {
		sm4Key = key.clone();
		sm4Flag = true;
		greyNewFlag0718 = false;
	}

	public AesUtil(String key) {
		rc4Key = key;
		sm4Flag = false;
		greyNewFlag0718 = false;
	}

	public AesUtil(String aesKey, String aesIv) {
		if (ZooConstant.GREY_NEW_ENCODE_0718.equals(aesIv)) {
			greyNewKey0718 = aesKey;
			greyNewFlag0718 = true;
		} else {
			secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(aesKey), "AES");
			ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(aesIv));
			greyNewFlag0718 = false;
		}
		sm4Flag = false;
	}

	private static final String ALGORITHM_NAME = "SM4";

	private static final String ALGORITHM_ECB_PKCS5PADDING = "SM4/ECB/PKCS5Padding";

	static {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) != null) {
			Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
		}
		Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}

	/**
	 * 加密
	 *
	 * @param data
	 * @param type 1: AES   chet    2: SM4   grey
	 * @return byte[]
	 * @author Curry
	 * @date 2022/11/1
	 */
	public byte[] encode(String data, int type) throws Exception {
		if (sm4Flag) {
			return sm4(data.getBytes(StandardCharsets.UTF_8), sm4Key, Cipher.ENCRYPT_MODE);
		}
		if (greyNewFlag0718) {
			return displacementEncryptionAndDecryption(data.getBytes(StandardCharsets.UTF_8), greyNewKey0718.getBytes(StandardCharsets.UTF_8));
		}
		if (type == NumberEnum.ONE.getNum()) {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
			return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
		} else {
			return encrypt(data, rc4Key).getBytes(StandardCharsets.UTF_8);
		}
	}


	/**
	 * 解密
	 *
	 * @param data
	 * @param type
	 * @return byte[]
	 * @author Curry
	 * @date 2022/11/1
	 */
	public byte[] decode(byte[] data, int type) throws Exception {
		if (sm4Flag) {
			return sm4(data, sm4Key, Cipher.DECRYPT_MODE);
		}
		if (greyNewFlag0718) {
			return displacementEncryptionAndDecryption(data, greyNewKey0718.getBytes(StandardCharsets.UTF_8));
		}
		if (type == NumberEnum.ONE.getNum()) {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
			return cipher.doFinal(data);
		} else {
			return decrypt(data, rc4Key);
		}
	}

	/**
	 * 生成 16 位密钥
	 *
	 * @return byte[]
	 * @author Curry
	 * @date 2022/11/1
	 */
	public static byte[] generateKey() throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
		kg.init(128, new SecureRandom());
		return kg.generateKey().getEncoded();
	}

	/**
	 * randomKey
	 * @return java.lang.String
	 * @author Curry
	 * @date 2023/7/18
	 */
	public static String randomKey() {
		SecureRandom random = new SecureRandom();
		byte[] randomBytes = new byte[NumberEnum.FIVE_HUNDRED_AND_ONE_TWO.getNum()];
		random.nextBytes(randomBytes);
		String randomStr = Base64.getEncoder()
				.encodeToString(randomBytes);
		return randomStr;
	}

	/**
	 * displacementEncryptionAndDecryption
	 * @param bytes
	 * @param key
	 * @return byte[]
	 * @author Curry
	 * @date 2023/7/18
	 */
	public static byte[] displacementEncryptionAndDecryption(byte[] bytes, byte[] key) {
		byte[] newBytes = bytes.clone();
		for (int i = 0; i < newBytes.length; i++) {
			newBytes[i] = (byte) (newBytes[i] ^ key[i % key.length]);
		}
		return newBytes;
	}

	/**
	 * SM4对称加解密
	 *
	 * @param input 明文（加密模式）或密文（解密模式）
	 * @param key   密钥
	 * @param mode  Cipher.ENCRYPT_MODE - 加密；Cipher.DECRYPT_MODE - 解密
	 * @return 密文（加密模式）或明文（解密模式）
	 * @throws Exception 加解密异常
	 */
	private static byte[] sm4(byte[] input, byte[] key, int mode)
			throws Exception {
		SecretKeySpec sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
		Cipher cipher = Cipher.getInstance(ALGORITHM_ECB_PKCS5PADDING, BouncyCastleProvider.PROVIDER_NAME);
		cipher.init(mode, sm4Key);
		return cipher.doFinal(input);
	}

	/**
	 * rc4 加解密
	 *
	 * @param handleStr
	 * @param key
	 * @return java.lang.String
	 * @author Curry
	 * @date 2023/7/5
	 */
	@SuppressFBWarnings("REC_CATCH_EXCEPTION")
	private static String rc4(String handleStr, String key, boolean isEncrypt) throws Exception {
		try {
			int[] iS = new int[NumberEnum.TWO_HUNDRED_AND_FIVE_SIXTY.getNum()];
			byte[] iK = new byte[NumberEnum.TWO_HUNDRED_AND_FIVE_SIXTY.getNum()];

			for (int i = 0; i < NumberEnum.TWO_HUNDRED_AND_FIVE_SIXTY.getNum(); i++) {
				iS[i] = i;
			}

			for (short i = 0; i < NumberEnum.TWO_HUNDRED_AND_FIVE_SIXTY.getNum(); i++) {
				iK[i] = (byte) key.charAt((i % key.length()));
			}

			int j = 0;

			for (int i = 0; i < NumberEnum.TWO_HUNDRED_AND_FIVE_FIVE.getNum(); i++) {
				j = (j + iS[i] + iK[i]) % NumberEnum.TWO_HUNDRED_AND_FIVE_SIXTY.getNum();
				int temp = iS[i];
				iS[i] = iS[j];
				iS[j] = temp;
			}

			int i = 0;
			j = 0;
			char[] iInputChar = handleStr.toCharArray();
			char[] iOutputChar = new char[iInputChar.length];
			for (short x = 0; x < iInputChar.length; x++) {
				i = (i + NumberEnum.ONE.getNum()) % NumberEnum.TWO_HUNDRED_AND_FIVE_SIXTY.getNum();
				j = (j + iS[i]) % NumberEnum.TWO_HUNDRED_AND_FIVE_SIXTY.getNum();
				int temp = iS[i];
				iS[i] = iS[j];
				iS[j] = temp;
				int t = (iS[i] + (iS[j] % NumberEnum.TWO_HUNDRED_AND_FIVE_SIXTY.getNum())) % NumberEnum.TWO_HUNDRED_AND_FIVE_SIXTY.getNum();
				int iY = iS[t];
				char iCY = (char) iY;
				iOutputChar[x] = (char) (iInputChar[x] ^ iCY);
			}
			return new String(iOutputChar);
		} catch (Exception e) {
			if (isEncrypt) {
				return ERR_MARK + new String(encrypt(handleStr.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
			} else {
				return ERR_MARK + new String(decrypt(handleStr.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
			}
		}
	}

	private static final String ERR_MARK = "~!@#$%^&*(<.,';[}'_=-+|(/':?><:-+.}>[@";

	public static String encrypt(String str, String key) throws Exception {
		if (str.startsWith(ERR_MARK)) {
			return new String(encrypt(str.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
		} else {
			return rc4(str, key, true);
		}
	}

	public static byte[] decrypt(byte[] bytes, String key) throws Exception {
		String str = new String(bytes, StandardCharsets.UTF_8);
		if (str.startsWith(ERR_MARK)) {
			return decrypt(bytes, key.getBytes(StandardCharsets.UTF_8));
		} else {
			return rc4(str, key, false).getBytes(StandardCharsets.UTF_8);
		}
	}

	private static byte[] encrypt(byte[] str, byte[] key) throws Exception {
		byte[] result;
		// 初始化AES算法,key长度为256bit
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		// 对字符串 AES 加密
		result = cipher.doFinal(str);
		return result;
	}

	private static byte[] decrypt(byte[] bytes, byte[] key) throws Exception {
		// 初始化AES算法
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		// AES解密
		byte[] result = cipher.doFinal(bytes);
		return result;
	}
}
