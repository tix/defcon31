CREATE DATABASE `zoodb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

##系统
INSERT INTO `t_admin_permissions` (`identification`, `createTime`, `parent_id`, `permissions_level`, `permissions_title`, `permissions_type`, `permissions_url`, `sort_index`)
VALUES
	('fc0dd753-5c08-4627-85c9-fb3f1bf59dc3', '2017-10-23 02:24:40', '', 0, '动物园', 'SYSTEM', '', 1);

//菜单
INSERT INTO `t_admin_permissions` (`identification`, `createTime`, `parent_id`, `permissions_level`, `permissions_title`, `permissions_type`, `permissions_url`, `sort_index`)
VALUES
	('19b1d8e7-09a0-4583-8a1f-39453658f934', '2017-10-23 02:52:00', 'fc0dd753-5c08-4627-85c9-fb3f1bf59dc3', 2, '应用管理', 'CATALOG', 'application', 1),
	('19b1d8e7-09a0-4583-8a1f-39453658f935', '2017-10-23 02:52:00', 'fc0dd753-5c08-4627-85c9-fb3f1bf59dc3', 2, '流量管理', 'CATALOG', 'task', 2),
	('19b1d8e7-09a0-4583-8a1f-39453658f936', '2017-10-23 02:52:00', 'fc0dd753-5c08-4627-85c9-fb3f1bf59dc3', 2, '统计', 'CATALOG', 'report', 3);

##菜单权限
INSERT INTO `t_admin_role_permissions` (`identification`, `createTime`, `permissions_id`, `role_id`)
VALUES
	('02b9ab9e-c3fd-40f0-a991-3b55509ab1cf', '2018-09-17 05:10:12', '68edaa46-7150-4db3-a3bb-76a9ba92160a', 'f87e0d1d-51cb-4f0b-9642-6d6dc9a1ea88'),
	('04157ddb-5cf3-4f3e-9962-56e6573e50fa', '2018-09-17 05:10:12', '7a2d62ed-2eb2-46ed-af8c-3967034d35e7', 'f87e0d1d-51cb-4f0b-9642-6d6dc9a1ea88'),
	('02b9ab9e-c3fd-40f0-a991-3b55509ab1c1', '2018-09-17 05:10:12', '68edaa46-7150-4db3-a3bb-76a9ba92160a', 'f87e0d1d-51cb-4f0b-9642-6d6dc9a1ea88');

##目录
INSERT INTO `t_admin_permissions` (`identification`, `createTime`, `parent_id`, `permissions_level`, `permissions_title`, `permissions_type`, `permissions_url`, `sort_index`)
VALUES
	('19b1d8e7-09a0-4583-8a1f-39453658r934', '2017-10-23 02:52:00', '19b1d8e7-09a0-4583-8a1f-39453658f934', 1, 'test', 'MENU', 'test', 1);

##目录权限
INSERT INTO `t_admin_role_permissions` (`identification`, `createTime`, `permissions_id`, `role_id`)
VALUES
	('02b9ab9e-c3fd-40f1-a991-3b55509ab2ef', '2018-09-17 05:10:12', '19b1d8e7-09a0-4583-8a1f-39453658r934', 'f87e0d1d-51cb-4f0b-9642-6d6dc9a1ea88');