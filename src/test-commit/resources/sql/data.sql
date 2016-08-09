CREATE TABLE `gps_tracks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `latitude` double NOT NULL,
  `longtitude` double NOT NULL,
  `time` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `device_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
INSERT INTO `gps_tracks` (`id`, `latitude`, `longtitude`, `time`, `user_id`, `device_id`) VALUES
(139, 53.143374, 45.034526, 1445963700000, 3, NULL);