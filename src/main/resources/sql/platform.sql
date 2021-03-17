
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
INSERT INTO `account` values(2, "admin1", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_ADMIN_FUND", "2017-3-16");
INSERT INTO `account` values(3, "admin2", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_ADMIN_QUALITY", "2017-3-16");
INSERT INTO `account` values(4, "admin3", "$2a$10$FJKY91H0r0tw1yps0r7QoO/UXPdxuG50JN3RLudhkklwszzLmPwH6", "2fsli3jf", false, "ROLE_ADMIN_FUND ROLE_ADMIN_QUALITY", "2017-3-16");