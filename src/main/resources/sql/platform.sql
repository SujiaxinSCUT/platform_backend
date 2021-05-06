
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

INSERT INTO `account` values(6, "Mp1", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "-----BEGIN CERTIFICATE-----
MIICcjCCAhmgAwIBAgIQE1YzNASlHLrn7hXdXQRxtzAKBggqhkjOPQQDAjB8MQsw
CQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZy
YW5jaXNjbzEcMBoGA1UEChMTb3JnTXAxYS5leGFtcGxlLmNvbTEiMCAGA1UEAxMZ
dGxzY2Eub3JnTXAxYS5leGFtcGxlLmNvbTAeFw0yMTAzMjQwMjUwMDBaFw0zMTAz
MjIwMjUwMDBaMF4xCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYw
FAYDVQQHEw1TYW4gRnJhbmNpc2NvMSIwIAYDVQQDExlwZWVyMC5vcmdNcDFhLmV4
YW1wbGUuY29tMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEDomgcj5NQc5ENwKn
Ykr45oOK8k03EmRfVuK8ZdxzS7dzu3rUgs6hThNDUZ8S9AVlpwH3FYd7/dK51LF7
o33we6OBmjCBlzAOBgNVHQ8BAf8EBAMCBaAwHQYDVR0lBBYwFAYIKwYBBQUHAwEG
CCsGAQUFBwMCMAwGA1UdEwEB/wQCMAAwKwYDVR0jBCQwIoAgawlCrViHXbC1ghPu
IYpa8gZiGVcXQCXSmuCIUAsC9ocwKwYDVR0RBCQwIoIZcGVlcjAub3JnTXAxYS5l
eGFtcGxlLmNvbYIFcGVlcjAwCgYIKoZIzj0EAwIDRwAwRAIgWOOB8G2qlkJpCKCs
iHTsSq3Pf0N3+cIlZaNVIp1j05kCIAgCu/kU9JCtBT2xyb7Igh1njajqAXK/TQhW
0cs9Txa0
-----END CERTIFICATE-----
", false, "ROLE_USER", "2017-3-16");
--INSERT INTO `account` values(2, "Mp2", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_USER", "2017-3-16");
--INSERT INTO `account` values(3, "Mp3", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_USER", "2017-3-16");
INSERT INTO `account` values(7, "Mm1", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "D://Desktop//keyAndCertFile//Mm1//server.crt", false, "ROLE_USER", "2017-3-16");
--INSERT INTO `account` values(5, "Mm2", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_USER", "2017-3-16");
--INSERT INTO `account` values(1, "Mm3", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_USER", "2017-3-16");
INSERT INTO `account` values(8, "Mr1", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "D://Desktop//keyAndCertFile//Mr1//server.crt", false, "ROLE_USER", "2017-3-16");
--INSERT INTO `account` values(3, "Mr2", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_USER", "2017-3-16");
--INSERT INTO `account` values(3, "Mr3", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_USER", "2017-3-16");

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL UNIQUE,
  `description` text NOT NULL,
  `img_url` varchar(255) DEFAULT NULL,
  `unit` varchar(255) NOT NULL,
  `date` timestamp NOT NULL,
  `submitter_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- ----------------------------
-- Table structure for stock
-- ----------------------------
DROP TABLE IF EXISTS `stock`;
CREATE TABLE `stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` varchar(255) NOT NULL,
  `product_id` int(11) NOT NULL,
  `batch_id` varchar(255) NOT NULL,
  `quantity` double(18,2) NOT NULL,
  `price` double(18,2) NOT NULL,
  `status` varchar(255) NOT NULL,
  `date` timestamp NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `stock` values (1, "Mp1", 001,  "20210421000", 1000, 12, "free", "2021-04-21");
INSERT INTO `stock` values (2, "Mm1", 002,  "20210421001", 1000, 65, "free", "2021-04-21");

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
  `product_sign` text DEFAULT NULL,
  `fund_sign` text DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for product_material
-- ----------------------------
DROP TABLE IF EXISTS `product_material`;
CREATE TABLE `product_material` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(255) NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `product_batch_id` varchar(255) NOT NULL,
  `material_name` varchar(255) NOT NULL,
  `material_batch_id` varchar(255) NOT NULL,
  `product_quantity` double(18,2) NOT NULL,
  `material_quantity` double(18,2) NOT NULL,
  `date` timestamp NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;