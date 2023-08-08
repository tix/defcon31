/*
 Navicat Premium Data Transfer

 Source Server         : rxphoto
 Source Server Type    : MySQL
 Source Server Version : 100413
 Source Host           : localhost:3306
 Source Schema         : buchananapp

 Target Server Type    : MySQL
 Target Server Version : 100413
 File Encoding         : 65001

 Date: 03/07/2022 08:13:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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

SET FOREIGN_KEY_CHECKS = 1;
