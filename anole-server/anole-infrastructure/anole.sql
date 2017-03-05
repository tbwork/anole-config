/*
SQLyog Trial v12.4.0 (64 bit)
MySQL - 10.1.21-MariaDB : Database - anole
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`anole` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `anole`;

/*Table structure for table `anole_config` */

DROP TABLE IF EXISTS `anole_config`;

CREATE TABLE `anole_config` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Key` varchar(255) NOT NULL DEFAULT '' COMMENT '配置值名',
  `Type` tinyint(3) unsigned zerofill NOT NULL DEFAULT '000' COMMENT '1: number(整数、小数), 2: bool型（开关） 3: 字符串型 4.JSON类型',
  `Creator` varchar(255) NOT NULL DEFAULT '' COMMENT '创建者用户名',
  `LastOperator` varchar(255) NOT NULL DEFAULT '' COMMENT '最后的修改者用户名',
  `Description` tinytext COMMENT '描述',
  `Project` varchar(255) NOT NULL DEFAULT '' COMMENT '所属项目名',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='配置信息表';

/*Data for the table `anole_config` */

insert  into `anole_config`(`Id`,`Key`,`Type`,`Creator`,`LastOperator`,`Description`,`Project`,`CreateTime`,`UpdateTime`) values 

(1,'ip',003,'tangbo','tangbo',NULL,'order-main-service','2017-03-03 11:40:13','2017-03-03 11:40:13'),

(2,'port',001,'tangbo','tangbo',NULL,'order-main-service','2017-03-03 11:40:10','2017-03-03 11:40:10'),

(3,'cs',003,'tangbo','tangbo',NULL,'','2016-06-13 19:37:48','2016-06-13 19:37:48'),

(4,'key2',001,'admin','admin','11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111','order-main-service','2017-03-03 13:13:17','2017-03-03 13:13:17');

/*Table structure for table `anole_config_item` */

DROP TABLE IF EXISTS `anole_config_item`;

CREATE TABLE `anole_config_item` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Key` varchar(255) NOT NULL DEFAULT '' COMMENT '配置值名',
  `EnvName` varchar(10) NOT NULL DEFAULT '' COMMENT '配置所属环境',
  `Value` text NOT NULL COMMENT '配置值',
  `LastOperator` varchar(255) NOT NULL DEFAULT '' COMMENT '最后的修改者用户名',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Index_Key_Env` (`Key`,`EnvName`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='配置分环境值表';

/*Data for the table `anole_config_item` */

insert  into `anole_config_item`(`Id`,`Key`,`EnvName`,`Value`,`LastOperator`,`CreateTime`,`UpdateTime`) values 

(1,'ip','develop','192.168.0.1','tangbo','2017-03-03 11:35:31','2017-03-03 11:35:31'),

(2,'port','develop','8080','tangbo','2017-03-03 11:35:35','2017-03-03 11:35:35'),

(3,'key1','test','value1','admin','2017-02-27 20:46:43','2017-02-28 14:08:58'),

(4,'key2','develop','123','admin','2017-03-02 12:56:31','2017-03-02 12:56:31'),

(5,'key2','test','123','admin','2017-03-02 12:56:31','2017-03-02 12:56:31'),

(6,'key2','product','123','admin','2017-03-02 12:56:31','2017-03-02 12:56:31');

/*Table structure for table `anole_env` */

DROP TABLE IF EXISTS `anole_env`;

CREATE TABLE `anole_env` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(10) NOT NULL DEFAULT '' COMMENT '环境名称',
  `Description` tinytext COMMENT '说明、描述',
  `LastOperator` varchar(255) NOT NULL DEFAULT '' COMMENT '最后的修改者用户名',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Data for the table `anole_env` */

insert  into `anole_env`(`Id`,`Name`,`Description`,`LastOperator`,`CreateTime`,`UpdateTime`) values 

(1,'develop',NULL,'admin','2017-02-16 09:37:13','2017-02-16 09:37:13'),

(2,'test',NULL,'admin','2017-02-16 09:37:13','2017-02-16 09:37:13'),

(3,'product',NULL,'admin','2017-02-16 09:37:13','2017-02-16 09:37:13');

/*Table structure for table `anole_product_line` */

DROP TABLE IF EXISTS `anole_product_line`;

CREATE TABLE `anole_product_line` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) DEFAULT NULL COMMENT '名称',
  `Description` varchar(500) DEFAULT NULL COMMENT '描述',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `anole_product_line` */

insert  into `anole_product_line`(`Id`,`Name`,`Description`,`CreateTime`,`UpdateTime`) values 

(1,'order',NULL,'2017-02-16 09:44:29','2017-02-16 09:44:29');

/*Table structure for table `anole_project` */

DROP TABLE IF EXISTS `anole_project`;

CREATE TABLE `anole_project` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `ProductLine` varchar(255) DEFAULT NULL COMMENT '产品线',
  `Name` varchar(255) NOT NULL DEFAULT '' COMMENT '项目名，全局唯一',
  `Description` tinytext COMMENT '描述',
  `Creator` varchar(255) NOT NULL DEFAULT '' COMMENT '创建者用户名',
  `Owner` varchar(255) DEFAULT NULL COMMENT '拥有者用户名',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Index_Env_Name` (`Name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `anole_project` */

insert  into `anole_project`(`Id`,`ProductLine`,`Name`,`Description`,`Creator`,`Owner`,`CreateTime`,`UpdateTime`) values 

(1,'order','order-main-service',NULL,'admin','admin','2017-02-16 09:44:29','2017-02-16 09:44:29');

/*Table structure for table `anole_user` */

DROP TABLE IF EXISTS `anole_user`;

CREATE TABLE `anole_user` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(255) DEFAULT '' COMMENT '用户名',
  `Password` varchar(50) NOT NULL DEFAULT '' COMMENT '密码，MD5加密存储',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Index_Username` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Data for the table `anole_user` */

insert  into `anole_user`(`Id`,`Username`,`Password`,`CreateTime`,`UpdateTime`) values 

(1,'worker1','e10adc3949ba59abbe56e057f20f883e','2017-02-14 18:45:39','2017-02-14 18:45:39'),

(2,'tangbo','202cb962ac59075b964b07152d234b70','2017-02-14 18:51:31','2017-02-14 18:51:31'),

(3,'admin','202cb962ac59075b964b07152d234b70','2017-02-15 18:48:32','2017-02-15 18:48:32');

/*Table structure for table `anole_user_project_map` */

DROP TABLE IF EXISTS `anole_user_project_map`;

CREATE TABLE `anole_user_project_map` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(255) NOT NULL DEFAULT '' COMMENT '用户名',
  `Project` varchar(255) NOT NULL DEFAULT '' COMMENT '项目名',
  `Env` varchar(10) NOT NULL DEFAULT '' COMMENT '环境名',
  `Role` tinyint(3) unsigned zerofill NOT NULL DEFAULT '000' COMMENT '角色： 0-stranger 陌生人：可以看Key，但是看不到value； 1-vistor 访客：可以看key和value； 2-manager 管理人：可以编辑配置。 3-owner 可以删除配置。（注意拥有者具有所有环境的管理员权限）',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `anole_user_project_map` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
