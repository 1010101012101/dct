CREATE TABLE `vwb_attach` (
  `siteId` int(11) default NULL,
  `resourceId` int(11) NOT NULL default '0',
  `clbId` int(11) NOT NULL default '0',
  `filename` varchar(100) NOT NULL default '',
  `change_time` datetime default NULL,
  `length` int(11) default NULL,
  `change_by` varchar(255) default NULL,
  `version` int(11) NOT NULL default '0',
  PRIMARY KEY  (`clbId`,`version`),
  KEY `change_time` (`change_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_banner` (
  `id` int(11) NOT NULL auto_increment,
  `siteId` int(11) default NULL,
  `dirName` varchar(100) character set utf8 collate utf8_bin default NULL,
  `name` varchar(100) character set utf8 collate utf8_bin default NULL,
  `status` tinyint(1) NOT NULL default '0',
  `type` int(11) NOT NULL,
  `creator` varchar(100) character set utf8 collate utf8_bin default NULL,
  `createdTime` datetime default NULL,
  `pageId` int(11) NOT NULL,
  `ownedtype` varchar(100) character set utf8 collate utf8_bin default 'system',
  `bannerProfile` text,
  `leftPictureClbId` int(11) default NULL,
  `rightPictureClbId` int(11) default NULL,
  `middlePictureClbId` int(11) default NULL,
  `cssClbId` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_dpage_content_info` (
  `id` int(11) NOT NULL auto_increment,
  `siteId` int(11) default NULL,
  `title` varchar(255) default NULL,
  `resourceid` int(11) NOT NULL,
  `version` int(11) NOT NULL default '1',
  `change_time` datetime default NULL,
  `content` mediumtext NOT NULL,
  `change_by` varchar(255) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `siteid` (`siteId`,`resourceid`,`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_email_notify` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `siteId` int(11) default NULL,
  `resourceId` int(11) default NULL,
  `subscriber` varchar(45) NOT NULL,
  `receiver` varchar(45) NOT NULL,
  `rec_time` varchar(45) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `siteId` (`siteId`,`resourceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_myspace` (
  `id` int(11) NOT NULL auto_increment,
  `eid` varchar(50) character set utf8 collate utf8_bin NOT NULL,
  `siteId` int(11) default NULL,
  `resourceId` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_policy` (
  `id` int(11) NOT NULL auto_increment,
  `siteId` int(11) default NULL,
  `policy` text,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_portal_page` (
  `siteId` int(11) default NULL,
  `resourceId` int(11) NOT NULL,
  `title` varchar(255) default NULL,
  `uri` varchar(255) default NULL,
  KEY `siteId` (`siteId`,`resourceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_portal_portlets` (
  `id` int(11) NOT NULL auto_increment,
  `siteId` int(11) default NULL,
  `resourceId` int(11) default NULL,
  `context` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_properties` (
  `id` int(11) NOT NULL auto_increment,
  `strName` varchar(255) default NULL,
  `strValue` varchar(255) default NULL,
  `iSiteNum` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_resource_acl` (
  `id` int(11) NOT NULL auto_increment,
  `siteId` int(11) default NULL,
  `resourceId` int(11) NOT NULL,
  `type` varchar(10) default NULL,
  `eid` varchar(50) NOT NULL,
  `action` varchar(255) NOT NULL,
  `resourceType` varchar(10) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `resourceId` (`resourceId`,`siteId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_resource_info` (
  `id` int(11) NOT NULL auto_increment,
  `siteId` int(11) NOT NULL default '0',
  `type` varchar(64) default NULL,
  `title` varchar(255) default NULL,
  `trail` int(11) default '0',
  `parent` int(11) default '0',
  `left_menu` int(11) default '0',
  `top_menu` int(11) default '0',
  `footer` int(11) default '0',
  `banner` int(11) default '0',
  `create_time` datetime default NULL,
  `creator` varchar(255) default NULL,
  `acl` int(11) default '0',
  PRIMARY KEY  (`id`,`siteId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_share_acl` (
  `id` int(11) unsigned NOT NULL auto_increment,
  `hash` varchar(45) NOT NULL,
  `URL` varchar(256) NOT NULL,
  `accessTime` bigint(20) unsigned NOT NULL,
  `shareTime` bigint(20) unsigned NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_site` (
  `id` int(11) NOT NULL auto_increment,
  `state` varchar(255) default NULL,
  `prefix` varchar(255) default NULL,
  `create_time` datetime default NULL,
  `published` varchar(255) default 'published',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `vwb_site` (`id`,`state`,`prefix`,`create_time`) VALUES  (1,'work','vwb',NOW());

CREATE TABLE `vwb_skin` (
  `id` int(11) NOT NULL auto_increment,
  `siteId` int(11) NOT NULL default '0',
  `name` varchar(255) NOT NULL default '',
  `clbId` int(11) NOT NULL default '0',
  `space` varchar(255) default NULL,
  `forTemplate` varchar(45) default 'conference',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`siteId`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_temp_page` (
  `Id` int(11) NOT NULL auto_increment,
  `siteId` int(11) unsigned default NULL,
  `resourceId` int(11) unsigned default NULL,
  `author` varchar(255) default NULL,
  `content` text,
  `last_modify` datetime default NULL,
  PRIMARY KEY  (`Id`),
  UNIQUE KEY `siteId` (`siteId`,`resourceId`,`author`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_template` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `clbId` int(11) default NULL,
  `type` varchar(64) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_key_generate` (
  `id` int(11) NOT NULL auto_increment,
  `siteId` int(11) NOT NULL default '0',
  PRIMARY KEY  (`siteId`,`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `vwb_ddl_space` (
  `siteId` int(11) NOT NULL DEFAULT '0',
  `code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`siteId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


delimiter //
CREATE  PROCEDURE `findacl`(siteid int(11), vid int(11))
begin
declare done int;
declare result int(11);
declare generation int;
declare aclflag int(11);
declare recursive int;
declare ovid int(11);
set done=0;
set result=vid;
set recursive=64;
set generation=-1;
set ovid=vid;
set @sql=CONCAT('select parent,acl from vwb_resource_info where siteid=? and id=? into @result, @aclflag');
PREPARE stmt FROM @sql;
while  done=0 do
set @ovidP = ovid;
set @siteid=siteid;
EXECUTE stmt USING @siteid,@ovidP;
set result=@result;
set aclflag=@aclflag;
if (aclflag !=0 or result=vid) then
set done=1;
if(result=vid)then
set generation = -1;
end if;
else
if (result!=0) then
set ovid=result;
else
set done=1;
end if;
end if;
set generation = generation+1;
if(recursive=0) then
set ovid=vid;
set generation = 0;
set done=1;
else
set recursive=recursive-1;
end if;
end while;
select ovid, generation;
end; //


CREATE  PROCEDURE `findbanner`(siteid int(11), vid int(11))
begin
declare done int;
declare result int(11);
declare generation int;
declare bannermenu int(11);
declare recursive int;
declare ovid int(11);
set done=0;
set result=vid;
set recursive=64;
set generation=-1;
set ovid=vid;
set @sql=CONCAT('select parent,banner from vwb_resource_info where siteid=? and id=? into @result, @bannermenu');
PREPARE stmt FROM @sql;
while  done=0 do
set @ovidP = ovid;
set @siteid=siteid;
EXECUTE stmt USING @siteid, @ovidP;
set result=@result;
set bannermenu=@bannermenu;
if (bannermenu !=0 or result=vid) then
set done=1;
if(result=vid)then
set generation = -1;
end if;
else
if (result!=0) then
set ovid=result;
else
set done=1;
end if;
end if;
set generation = generation+1;
if(recursive=0) then
set ovid=vid;
set generation = 0;
set done=1;
else
set recursive=recursive-1;
end if;
end while;
select ovid, generation;
end; //


CREATE  PROCEDURE `findfooter`(siteid int(11), vid int(11))
begin
declare done int;
declare result int(11);
declare generation int;
declare footermenu int(11);
declare recursive int;
declare ovid int(11);
set done=0;
set result=vid;
set recursive=64;
set generation=-1;
set ovid=vid;
set @sql=CONCAT('select parent,footer from vwb_resource_info where siteid=? and id=? into @result, @footermenu');
PREPARE stmt FROM @sql;
while  done=0 do
set @ovidP = ovid;
set @siteid=siteid;
EXECUTE stmt USING @siteid, @ovidP;
set result=@result;
set footermenu=@footermenu;
if (footermenu !=0 or result=vid) then
set done=1;
if(result=vid)then
set generation = -1;
end if;
else
if (result!=0) then
set ovid=result;
else
set done=1;
end if;
end if;
set generation = generation+1;
if(recursive=0) then
set ovid=vid;
set generation = 0;
set done=1;
else
set recursive=recursive-1;
end if;
end while;
select ovid, generation;
end; //


CREATE  PROCEDURE `findleftmenu`(siteid int(11), vid int(11))
begin
declare done int;
declare result int(11);
declare generation int;
declare leftmenu int(11);
declare recursive int;
declare ovid int(11);
set done=0;
set result=vid;
set recursive=64;
set generation=-1;
set ovid=vid;
set @sql=CONCAT('select parent,left_menu from vwb_resource_info where siteid=? and id=? into @result, @leftmenu');
PREPARE stmt FROM @sql;
while  done=0 do
set @ovidP = ovid;
set @siteid=siteid;
EXECUTE stmt USING @siteid, @ovidP;
set result=@result;
set leftmenu=@leftmenu;
if (leftmenu !=0 or result=vid) then
set done=1;
if(result=vid)then
set generation = -1;
end if;
else
if (result!=0) then
set ovid=result;
else
set done=1;
end if;
end if;
set generation = generation+1;
if(recursive=0) then
set ovid=vid;
set generation = 0;
set done=1;
else
set recursive=recursive-1;
end if;
end while;
select ovid, generation;
end; //


CREATE  PROCEDURE `findtopmenu`(siteid int(11), vid int(11))
begin
declare done int;
declare result int(11);
declare generation int;
declare topmenu int(11);
declare recursive int;
declare ovid int(11);
set done=0;
set result=vid;
set recursive=64;
set generation=-1;
set ovid=vid;
set @sql=CONCAT('select parent,top_menu from vwb_resource_info where siteid=? and id=? into @result, @topmenu');
PREPARE stmt FROM @sql;
while  done=0 do
set @ovidP = ovid;
set @siteid=siteid;
EXECUTE stmt USING @siteid, @ovidP;
set result=@result;
set topmenu=@topmenu;
if (topmenu !=0 or result=vid) then
set done=1;
if(result=vid)then
set generation = -1;
end if;
else
if (result!=0) then
set ovid=result;
else
set done=1;
end if;
end if;
set generation = generation+1;
if(recursive=0) then
set ovid=vid;
set generation = 0;
set done=1;
else
set recursive=recursive-1;
end if;
end while;
select ovid, generation;
end; //


CREATE PROCEDURE `findtrail`(siteid int(11), vid int(11))
begin
declare done int;
declare result int(11);
declare generation int;
declare trailflag int(11);
declare recursive int;
declare ovid int(11);
set done=0;
set result=vid;
set recursive=64;
set generation=-1;
set ovid=vid;
set @sql=CONCAT('select parent,trail from vwb_resource_info where siteid=? and id=? into @result, @trailflag');
PREPARE stmt FROM @sql;
while  done=0 do
set @ovidP = ovid;
set @siteid=siteid;
EXECUTE stmt USING @siteid, @ovidP;
set result=@result;
set trailflag=@trailflag;
if (trailflag !=0 or result=vid) then
set done=1;
if(result=vid)then
set generation = -1;
end if;
else
if (result!=0) then
set ovid=result;
else
set done=1;
end if;
end if;
set generation = generation+1;
if(recursive=0) then
set ovid=vid;
set generation = 0;
set done=1;
else
set recursive=recursive-1;
end if;
end while;
select ovid, generation;
end; //
delimiter ;