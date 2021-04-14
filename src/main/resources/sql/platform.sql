
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `platform`;
CREATE DATABASE `platform`;
use `platform`;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `certificate` text NOT NULL,
  `verified` boolean DEFAULT NULL,
  `permission` varchar(255) DEFAULT NULL,
  `date` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `account` values(1, "user1", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_USER", "2017-3-16");
INSERT INTO `account` values(2, "user2", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_USER", "2017-3-16");
INSERT INTO `account` values(3, "admin1", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_ADMIN_FUND", "2017-3-16");
INSERT INTO `account` values(4, "admin2", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_ADMIN_QUALITY", "2017-3-16");
INSERT INTO `account` values(5, "admin3", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_ADMIN_FUND ROLE_ADMIN_QUALITY", "2017-3-16");


-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL UNIQUE,
  `description` text NOT NULL,
  `img_url` varchar(255) NOT NULL,
  `unit` varchar(255) NOT NULL,
  `date` timestamp NOT NULL,
  `submitter_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for stock
-- ----------------------------
DROP TABLE IF EXISTS `stock`;
CREATE TABLE `stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `batch_id` varchar(255) NOT NULL,
  `quantity` double(18,2) NOT NULL,
  `price` double(18,2) NOT NULL,
  `status` varchar(255) NOT NULL,
  `date` timestamp NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for system_order
-- ----------------------------
DROP TABLE IF EXISTS `system_order`;
CREATE TABLE `system_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `client_name` varchar(255) NOT NULL,
  `supplier_name` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `date` timestamp NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for ordered_product
-- ----------------------------
DROP TABLE IF EXISTS `ordered_product`;
CREATE TABLE `ordered_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` double(18,2) NOT NULL,
  `price` double(18,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for fabric_order
-- ----------------------------
DROP TABLE IF EXISTS `fabric_order`;
CREATE TABLE `fabric_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `batch_id` int(11) NOT NULL,
  `fabric_order_id` int(11) NOT NULL,
  `quantity` double(18,2) NOT NULL,
  `price` double(18,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;