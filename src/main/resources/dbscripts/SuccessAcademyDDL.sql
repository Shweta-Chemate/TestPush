CREATE TABLE success_academy_learnings(
  'row_id' VARCHAR(100) NOT NULL,
  'title' VARCHAR(250) NOT NULL,
  'asset_model' VARCHAR(250) NOT NULL,
  'asset_facet' VARCHAR(250) NOT NULL,
  'asset_group' VARCHAR(250) NOT NULL,
  'supported_formats' VARCHAR(250) NULL,
  'post_date' VARCHAR(45) NULL,
  'description' MEDIUMTEXT NULL,
  'learning_link' VARCHAR(250) NOT NULL,
  'last_modified_dt_time' TIMESTAMP(6) NOT NULL,
  PRIMARY KEY ('row_id'),
  UNIQUE INDEX 'row_id_UNIQUE' ('row_id' ASC) VISIBLE) ENGINE=InnoDB DEFAULT CHARSET=latin1