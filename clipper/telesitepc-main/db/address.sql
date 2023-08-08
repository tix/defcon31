/*
 Navicat Premium Data Transfer

 Source Server         : Full
 Source Server Type    : MySQL
 Source Server Version : 100406
 Source Host           : localhost:3306
 Source Schema         : buchananapp_pc

 Target Server Type    : MySQL
 Target Server Version : 100406
 File Encoding         : 65001

 Date: 27/10/2022 08:16:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `BTC` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRC` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `ERC` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `RX_BTC` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `RX_TRC` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `RX_ERC` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `created_at` datetime(0) NULL DEFAULT NULL,
  `updated_at` datetime(0) NULL DEFAULT NULL,
  `INITIAL_TIME` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, '35b4KU2NBPVGd8nwB8esTmishqdU2PPUrP_PC', 'TSh9dQg466eMTyQpiQJ8HbXPwPkVxWZcfm', '0x276a84565dcF98b615ff2FB12c42b1E9Caaf7685', '3QtB81hG69yaiHkBCTfPKeZkR8i2yWe8bm', 'TDsmKZ7a5LVnCSW8TVXChWMbAjU8wmCi1Y', '0x31bdE5A8Bf959CD0f1d4006c15eE48055ece3A5c', NULL, '2022-10-25 22:37:07', 1);

SET FOREIGN_KEY_CHECKS = 1;
