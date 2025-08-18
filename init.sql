-- ---------------------------------
-- 1. 创建数据库和表（保持不变）
-- ---------------------------------
CREATE DATABASE IF NOT EXISTS `kaola` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `kaola`;

CREATE TABLE `t_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户的唯一标识符',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户的登录名',
  `password` VARCHAR(255) NOT NULL COMMENT '用户的加密密码',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '用户头像在阿里云OSS上的地址',
  `create_time` DATETIME NOT NULL COMMENT '用户的创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE `t_department` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '系的唯一标识符',
  `name` VARCHAR(100) NOT NULL UNIQUE COMMENT '系的名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系表';

CREATE TABLE `t_file` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件的唯一标识符',
  `user_id` BIGINT NOT NULL COMMENT '上传该文件的用户ID',
  `department_id` BIGINT NOT NULL COMMENT '该文件所属的系/学院ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '文件的名称',
  `file_url` VARCHAR(255) NOT NULL COMMENT '文件在阿里云OSS上的存储地址',
  `file_type` VARCHAR(50) NOT NULL COMMENT '文件的类型，如pdf, doc, ppt',
  `download_count` INT NOT NULL DEFAULT '0' COMMENT '文件的下载次数',
  `create_time` DATETIME NOT NULL COMMENT '文件的上传时间',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`),
  FOREIGN KEY (`department_id`) REFERENCES `t_department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';

-- ---------------------------------
-- 2. 重新插入系表数据
-- ---------------------------------
-- 删除旧数据
DELETE FROM `t_department`;
-- 插入新数据
INSERT INTO `t_department` (`id`, `name`) VALUES
(1, '航海技术系'),
(2, '轮机工程系'),
(3, '交通工程系'),
(4, '交通运输系'),
(5, '物流管理系'),
(6, '国际航运系'),
(7, '法律系'),
(8, '物流经济与统计系'),
(9, '国际贸易与金融系'),
(10, '财务与会计学系'),
(11, '工商与旅游管理系'),
(12, '管理科学系'),
(13, '机械工程系'),
(14, '工业工程系'),
(15, '电气自动化系'),
(16, '计算机科学系'),
(17, '电子工程系'),
(18, '海洋环境系'),
(19, '安全科学与工程系'),
(20, '港口航道与海岸工程系'),
(21, '船舶与海洋工程系'),
(22, '海洋材料系'),
(23, '英语系'),
(24, '翻译系'),
(25, '商务英语系'),
(26, '日语系'),
(27, '法语系'),
(28, '数学系'),
(29, '思政系'),
(30, '工业设计系'),
(31, '视觉传达设计系'),
(32, '绘画系');


-- ---------------------------------
-- 3. 重新插入模拟用户数据（10条）
-- ---------------------------------
DELETE FROM `t_user`;
INSERT INTO `t_user` (`id`, `username`, `password`, `avatar_url`, `create_time`) VALUES
(1, 'zhangsan', 'd404559f602eab6fd602ac7680dacbfaadd13630335e95156642d7fb2e244d60', 'http://example.com/avatars/user1.jpg', '2025-08-18 09:00:00'),
(2, 'lisi', 'd404559f602eab6fd602ac7680dacbfaadd13630335e95156642d7fb2e244d60', 'http://example.com/avatars/user2.jpg', '2025-08-18 09:05:00'),
(3, 'wangwu', 'd404559f602eab6fd602ac7680dacbfaadd13630335e95156642d7fb2e244d60', 'http://example.com/avatars/user3.jpg', '2025-08-18 09:10:00'),
(4, 'zhaoliu', 'd404559f602eab6fd602ac7680dacbfaadd13630335e95156642d7fb2e244d60', 'http://example.com/avatars/user4.jpg', '2025-08-18 09:15:00'),
(5, 'qianqi', 'd404559f602eab6fd602ac7680dacbfaadd13630335e95156642d7fb2e244d60', 'http://example.com/avatars/user5.jpg', '2025-08-18 09:20:00'),
(6, 'sunba', 'd404559f602eab6fd602ac7680dacbfaadd13630335e95156642d7fb2e244d60', 'http://example.com/avatars/user6.jpg', '2025-08-18 09:25:00'),
(7, 'zhoujiu', 'd404559f602eab6fd602ac7680dacbfaadd13630335e95156642d7fb2e244d60', 'http://example.com/avatars/user7.jpg', '2025-08-18 09:30:00'),
(8, 'wushi', 'd404559f602eab6fd602ac7680dacbfaadd13630335e95156642d7fb2e244d60', 'http://example.com/avatars/user8.jpg', '2025-08-18 09:35:00'),
(9, 'zhensi', 'd404559f602eab6fd602ac7680dacbfaadd13630335e95156642d7fb2e244d60', 'http://example.com/avatars/user9.jpg', '2025-08-18 09:40:00'),
(10, 'fengtian', 'd404559f602eab6fd602ac7680dacbfaadd13630335e95156642d7fb2e244d60', 'http://example.com/avatars/user10.jpg', '2025-08-18 09:45:00');

-- ---------------------------------
-- 4. 重新插入模拟文件数据（10条）
-- ---------------------------------
DELETE FROM `t_file`;
INSERT INTO `t_file` (`id`, `user_id`, `department_id`, `file_name`, `file_url`, `file_type`, `download_count`, `create_time`) VALUES
(1, 1, 16, '计算机组成原理.pdf', 'http://example.com/files/1.pdf', 'pdf', 123, '2025-08-18 10:00:00'),
(2, 2, 16, '数据结构与算法.docx', 'http://example.com/files/2.docx', 'docx', 98, '2025-08-18 10:05:00'),
(3, 3, 4, '交通运输学导论.ppt', 'http://example.com/files/3.ppt', 'ppt', 50, '2025-08-18 10:10:00'),
(4, 4, 1, '航海技术概论.pdf', 'http://example.com/files/4.pdf', 'pdf', 20, '2025-08-18 10:15:00'),
(5, 5, 7, '合同法讲义.pdf', 'http://example.com/files/5.pdf', 'pdf', 75, '2025-08-18 10:20:00'),
(6, 6, 17, '电路基础习题.pdf', 'http://example.com/files/6.pdf', 'pdf', 64, '2025-08-18 10:25:00'),
(7, 7, 23, '高级英语.doc', 'http://example.com/files/7.doc', 'doc', 88, '2025-08-18 10:30:00'),
(8, 8, 28, '高等数学复习.pdf', 'http://example.com/files/8.pdf', 'pdf', 110, '2025-08-18 10:35:00'),
(9, 9, 13, '机械原理.pdf', 'http://example.com/files/9.pdf', 'pdf', 45, '2025-08-18 10:40:00'),
(10, 10, 16, '软件工程导论.ppt', 'http://example.com/files/10.ppt', 'ppt', 32, '2025-08-18 10:45:00');