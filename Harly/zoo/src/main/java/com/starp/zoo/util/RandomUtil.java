package com.starp.zoo.util;

import com.starp.zoo.constant.Constant;
import com.starp.zoo.constant.NumberEnum;

import java.util.Arrays;
import java.util.Random;

/**
 * @author starp
 */
public class RandomUtil {
	private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyz";

	/**
	 * @param min
	 * @param max
	 * @param n
	 * @return
	 */
	public static int[] randomArray(int min, int max, int n) {
		int len = max - min + 1;
		if (max < min || n > len) {
			return null;
		}
		// 初始化给定范围的待选数组
		int[] source = new int[len];
		for (int i = min; i < min + len; i++) {
			source[i - min] = i;
		}
		int[] result = new int[n];
		Random rd = new Random();
		int index = 0;
		for (int i = 0; i < result.length; i++) {
			// 待选数组0到(len-2)随机一个下标
			index = Math.abs(rd.nextInt(len--));
			// 将随机到的数放入结果集
			result[i] = source[index];
			// 将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
			source[index] = source[len];
		}
		 Arrays.sort(result);
		 return result;
	}

	public static String getRandomNum(int len) {
		Random random = new Random();
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0; i< len ;i ++){
			stringBuffer.append(random.nextInt(10));
		}
		return stringBuffer.toString();
	}


	/**
	 * 生成随机字符串
	 * @param length
	 * @return java.lang.String
	 * @author Curry
	 * @date 2023/8/1
	 */
	public static String generateRandomString(int length) {
		Random random = new Random();
		StringBuilder stringBuilder = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int randomIndex = random.nextInt(CHARACTERS.length());
			char randomCharacter = CHARACTERS.charAt(randomIndex);
			stringBuilder.append(randomCharacter);
		}

		return stringBuilder.toString();
	}

	/**
	 * 随机从接口集合中拿取一个名称
	 * @return java.lang.String
	 * @author Curry
	 * @date 2023/8/2
	 */
	public static String getInterFaceName() {
		Random random = new Random();
		return Constant.ZOO_JUMP_INTERFACE_LIST.get(random.nextInt(NumberEnum.SIX.getNum()));
	}

}
