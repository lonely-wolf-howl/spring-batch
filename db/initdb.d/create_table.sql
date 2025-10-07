CREATE TABLE `package`
(
    `package_seq`  int         NOT NULL AUTO_INCREMENT,
    `package_name` varchar(50) NOT NULL,
    `count`        int                  DEFAULT NULL,
    `period`       int                  DEFAULT NULL,
    `created_at`   timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_at`  timestamp            DEFAULT NULL,
    PRIMARY KEY (`package_seq`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `pass`
(
    `pass_seq`        int         NOT NULL AUTO_INCREMENT,
    `package_seq`     int         NOT NULL,
    `user_id`         varchar(20) NOT NULL,
    `status`          varchar(10) NOT NULL,
    `remaining_count` int                  DEFAULT NULL,
    `started_at`      timestamp   NOT NULL,
    `ended_at`        timestamp            DEFAULT NULL,
    `expired_at`      timestamp            DEFAULT NULL,
    `created_at`      timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_at`     timestamp            DEFAULT NULL,
    PRIMARY KEY (`pass_seq`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `bulk_pass`
(
    `bulk_pass_seq` int         NOT NULL AUTO_INCREMENT,
    `package_seq`   int         NOT NULL,
    `user_group_id` varchar(20) NOT NULL,
    `status`        varchar(10) NOT NULL,
    `count`         int                  DEFAULT NULL,
    `started_at`    timestamp   NOT NULL,
    `ended_at`      timestamp            DEFAULT NULL,
    `created_at`    timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_at`   timestamp            DEFAULT NULL,
    PRIMARY KEY (`bulk_pass_seq`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `booking`
(
    `booking_seq`  int         NOT NULL AUTO_INCREMENT,
    `pass_seq`     int         NOT NULL,
    `user_id`      varchar(20) NOT NULL,
    `status`       varchar(10) NOT NULL,
    `used_pass`    tinyint(1)  NOT NULL DEFAULT '0',
    `attended`     tinyint(1)  NOT NULL DEFAULT '0',
    `started_at`   timestamp   NOT NULL,
    `ended_at`     timestamp   NOT NULL,
    `cancelled_at` timestamp            DEFAULT NULL,
    `created_at`   timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_at`  timestamp            DEFAULT NULL,
    PRIMARY KEY (`booking_seq`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `user`
(
    `user_id`     varchar(20) NOT NULL,
    `user_name`   varchar(50) NOT NULL,
    `status`      varchar(10) NOT NULL,
    `phone`       varchar(50)          DEFAULT NULL,
    `meta`        TEXT                 DEFAULT NULL,
    `created_at`  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_at` timestamp            DEFAULT NULL,
    PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `user_group_mapping`
(
    `user_group_id`   varchar(20) NOT NULL,
    `user_id`         varchar(20) NOT NULL,
    `user_group_name` varchar(50) NOT NULL,
    `description`     varchar(50) NOT NULL,
    `created_at`      timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_at`     timestamp            DEFAULT NULL,
    PRIMARY KEY (`user_group_id`, `user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `notification`
(
    `notification_seq` int           NOT NULL AUTO_INCREMENT,
    `uuid`             varchar(20)   NOT NULL,
    `event`            varchar(10)   NOT NULL,
    `text`             varchar(1000) NOT NULL,
    `sent`             tinyint(1)    NOT NULL DEFAULT '0',
    `sent_at`          timestamp              DEFAULT NULL,
    `created_at`       timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_at`      timestamp              DEFAULT NULL,
    PRIMARY KEY (`notification_seq`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE `statistics`
(
    `statistics_seq`  int       NOT NULL AUTO_INCREMENT,
    `statistics_at`   timestamp NOT NULL,
    `all_count`       int       NOT NULL DEFAULT 0,
    `attended_count`  int       NOT NULL DEFAULT 0,
    `cancelled_count` int       NOT NULL DEFAULT 0,
    PRIMARY KEY (`statistics_seq`),
    INDEX idx_statistics_at (`statistics_at`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;