# Host: 127.0.0.1  (Version: 5.5.5-10.1.13-MariaDB)
# Date: 2016-05-11 21:37:48
# Generator: MySQL-Front 5.3  (Build 4.205)

#
# Structure for table "anole_config"
#

DROP TABLE IF EXISTS `anole_config`;
CREATE TABLE `anole_config` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Key` varchar(255) NOT NULL DEFAULT '' COMMENT '配置值名',
  `Type` tinyint(3) unsigned zerofill NOT NULL DEFAULT '000' COMMENT '1: number(整数、小数), 2: bool型 3: 字符串型',
  `Creator` varchar(255) NOT NULL DEFAULT '' COMMENT '创建者用户名',
  `LastOperator` varchar(255) NOT NULL DEFAULT '' COMMENT '最后的修改者用户名',
  `Description` tinytext COMMENT '描述',
  `Project` varchar(255) NOT NULL DEFAULT '' COMMENT '所属项目名',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置信息表';

#
# Structure for table "anole_config_item"
#

DROP TABLE IF EXISTS `anole_config_item`;
CREATE TABLE `anole_config_item` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Key` varchar(255) NOT NULL DEFAULT '' COMMENT '配置值名',
  `EnvName` varchar(10) NOT NULL DEFAULT '' COMMENT '配置所属环境',
  `Value` text COMMENT '配置值',
  `LastOperator` varchar(255) NOT NULL DEFAULT '' COMMENT '最后的修改者用户名',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Index_Key_Env` (`Key`,`EnvName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='配置分环境值表';

#
# Structure for table "anole_env"
#

DROP TABLE IF EXISTS `anole_env`;
CREATE TABLE `anole_env` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(10) NOT NULL DEFAULT '' COMMENT '环境名称',
  `Description` tinytext COMMENT '说明、描述',
  `LastOperator` varchar(255) NOT NULL DEFAULT '' COMMENT '最后的修改者用户名',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for table "anole_hub"
#

DROP TABLE IF EXISTS `anole_hub`;
CREATE TABLE `anole_hub` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Type` tinyint(1) unsigned zerofill NOT NULL DEFAULT '0' COMMENT 'hub类型：1-boss； 2-worker',
  `Address` varchar(20) NOT NULL DEFAULT '' COMMENT 'worker的IP地址',
  `Identity` varchar(50) NOT NULL DEFAULT '' COMMENT 'worker的身份标识',
  `Status` tinyint(1) unsigned zerofill NOT NULL DEFAULT '0' COMMENT '0-无效，1-有效',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for table "anole_project"
#

DROP TABLE IF EXISTS `anole_project`;
CREATE TABLE `anole_project` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(255) NOT NULL DEFAULT '' COMMENT '项目名，全局唯一',
  `Description` tinytext COMMENT '描述',
  `Creator` varchar(255) NOT NULL DEFAULT '' COMMENT '创建者用户名',
  `Owner` varchar(255) DEFAULT NULL COMMENT '拥有者用户名',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Index_Env_Name` (`Name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for table "anole_user"
#

DROP TABLE IF EXISTS `anole_user`;
CREATE TABLE `anole_user` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(255) DEFAULT '' COMMENT '用户名',
  `Password` varchar(50) NOT NULL DEFAULT '' COMMENT '密码，MD5加密存储',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Index_Username` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for table "anole_user_project_map"
#

DROP TABLE IF EXISTS `anole_user_project_map`;
CREATE TABLE `anole_user_project_map` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(255) NOT NULL DEFAULT '' COMMENT '用户名',
  `Project` varchar(255) NOT NULL DEFAULT '' COMMENT '项目名',
  `Env` varchar(10) NOT NULL DEFAULT '' COMMENT '环境名',
  `Role` tinyint(3) unsigned zerofill NOT NULL DEFAULT '000' COMMENT '角色：0-陌生人：不用插入纪录，可以看Key，但是看不到value。1-访客：可以看key和value：2-管理员：可以编辑配置。（注意拥有者具有所有环境的管理员权限）',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
