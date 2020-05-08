CREATE TABLE `success_academy_learnings`.`cxpp_lookup` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cxpp_key` VARCHAR(250) NOT NULL,
  `cxpp_value` VARCHAR(250) NULL,
  `description` MEDIUMTEXT NULL,
  `created_dt_time` DATETIME(6) NOT NULL,
  `updated_dt_time` DATETIME(6) NOT NULL,
  `created_by` VARCHAR(250) NOT NULL,
  `updated_by` VARCHAR(250) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `cxpp_key_UNIQUE` (`cxpp_key` ASC) VISIBLE)


insert into cxpp_lookup(cxpp_key,cxpp_value,description,created_dt_time,updated_dt_time,created_by,updated_by) values('CXPP_UI_TAB_MODEL','1','key to store CXPP UI tab location for Model', '2020-01-01 10:10:10','2020-01-01 10:10:10','vimod','vimod')

insert into cxpp_lookup(cxpp_key,cxpp_value,description,created_dt_time,updated_dt_time,created_by,updated_by) values('CXPP_UI_TAB_ROLE','2','key to store CXPP UI tab location for Role', '2020-01-01 10:10:10','2020-01-01 10:10:10','vimod','vimod')

insert into cxpp_lookup(cxpp_key,cxpp_value,description,created_dt_time,updated_dt_time,created_by,updated_by) values('CXPP_UI_TAB_PRODUCT','3','key to store CXPP UI tab location for Product', '2020-01-01 10:10:10','2020-01-01 10:10:10','vimod','vimod')

insert into cxpp_lookup(cxpp_key,cxpp_value,description,created_dt_time,updated_dt_time,created_by,updated_by) values('CXPP_UI_TAB_TECHNOLOGY','4','key to store CXPP UI tab location for Technology', '2020-01-01 10:10:10','2020-01-01 10:10:10','vimod','vimod')