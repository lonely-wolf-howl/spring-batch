CREATE TABLE `package`
(
    `package_seq`  int         NOT NULL AUTO_INCREMENT COMMENT 'Package sequence number',
    `package_name` varchar(50) NOT NULL COMMENT 'Package name',
    `count`        int                  DEFAULT NULL COMMENT 'Number of available passes, NULL means unlimited',
    `period`       int                  DEFAULT NULL COMMENT 'Valid period (days), NULL means unlimited',
    `created_at`   timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    `modified_at`  timestamp            DEFAULT NULL COMMENT 'Modification timestamp',
    PRIMARY KEY (`package_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Package information';

CREATE TABLE `pass`
(
    `pass_seq`        int         NOT NULL AUTO_INCREMENT COMMENT 'Pass sequence number',
    `package_seq`     int         NOT NULL COMMENT 'Package sequence number',
    `user_id`         varchar(20) NOT NULL COMMENT 'User ID',
    `status`          varchar(10) NOT NULL COMMENT 'Status',
    `remaining_count` int                  DEFAULT NULL COMMENT 'Remaining pass count, NULL means unlimited',
    `started_at`      timestamp   NOT NULL COMMENT 'Start timestamp',
    `ended_at`        timestamp            DEFAULT NULL COMMENT 'End timestamp, NULL means unlimited',
    `expired_at`      timestamp            DEFAULT NULL COMMENT 'Expiration timestamp',
    `created_at`      timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    `modified_at`     timestamp            DEFAULT NULL COMMENT 'Modification timestamp',
    PRIMARY KEY (`pass_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Pass information';

CREATE TABLE `bulk_pass`
(
    `bulk_pass_seq`   int         NOT NULL AUTO_INCREMENT COMMENT 'Bulk pass sequence number',
    `package_seq`     int         NOT NULL COMMENT 'Package sequence number',
    `user_group_id`   varchar(20) NOT NULL COMMENT 'User group ID',
    `status`          varchar(10) NOT NULL COMMENT 'Status',
    `count`           int                  DEFAULT NULL COMMENT 'Number of passes, NULL means unlimited',
    `started_at`      timestamp   NOT NULL COMMENT 'Start timestamp',
    `ended_at`        timestamp            DEFAULT NULL COMMENT 'End timestamp, NULL means unlimited',
    `created_at`      timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    `modified_at`     timestamp            DEFAULT NULL COMMENT 'Modification timestamp',
    PRIMARY KEY (`bulk_pass_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Bulk pass for issuing passes to multiple users';

CREATE TABLE `booking`
(
    `booking_seq`  int         NOT NULL AUTO_INCREMENT COMMENT 'Booking sequence number',
    `pass_seq`     int         NOT NULL COMMENT 'Pass sequence number',
    `user_id`      varchar(20) NOT NULL COMMENT 'User ID',
    `status`       varchar(10) NOT NULL COMMENT 'Status',
    `used_pass`    tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Whether the pass was used',
    `attended`     tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Attendance flag',
    `started_at`   timestamp   NOT NULL COMMENT 'Start timestamp',
    `ended_at`     timestamp   NOT NULL COMMENT 'End timestamp',
    `cancelled_at` timestamp            DEFAULT NULL COMMENT 'Cancellation timestamp',
    `created_at`   timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    `modified_at`  timestamp            DEFAULT NULL COMMENT 'Modification timestamp',
    PRIMARY KEY (`booking_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Booking information';

CREATE TABLE `user`
(
    `user_id`     varchar(20) NOT NULL COMMENT 'User ID',
    `user_name`   varchar(50) NOT NULL COMMENT 'User name',
    `status`      varchar(10) NOT NULL COMMENT 'Status',
    `phone`       varchar(50)          DEFAULT NULL COMMENT 'Phone number',
    `meta`        TEXT                 DEFAULT NULL COMMENT 'Meta information (JSON)',
    `created_at`  timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    `modified_at` timestamp            DEFAULT NULL COMMENT 'Modification timestamp',
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User information';

CREATE TABLE `user_group_mapping`
(
    `user_group_id`   varchar(20) NOT NULL COMMENT 'User group ID',
    `user_id`         varchar(20) NOT NULL COMMENT 'User ID',
    `user_group_name` varchar(50) NOT NULL COMMENT 'User group name',
    `description`     varchar(50) NOT NULL COMMENT 'Description',
    `created_at`      timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    `modified_at`     timestamp            DEFAULT NULL COMMENT 'Modification timestamp',
    PRIMARY KEY (`user_group_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Mapping between user groups and users';

CREATE TABLE `notification`
(
    `notification_seq` int           NOT NULL AUTO_INCREMENT COMMENT 'Notification sequence number',
    `uuid`             varchar(20)   NOT NULL COMMENT 'User UUID (e.g., KakaoTalk)',
    `event`            varchar(10)   NOT NULL COMMENT 'Event type',
    `text`             varchar(1000) NOT NULL COMMENT 'Notification content',
    `sent`             tinyint(1)    NOT NULL DEFAULT '0' COMMENT 'Whether the notification was sent',
    `sent_at`          timestamp              DEFAULT NULL COMMENT 'Sent timestamp',
    `created_at`       timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    `modified_at`      timestamp              DEFAULT NULL COMMENT 'Modification timestamp',
    PRIMARY KEY (`notification_seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Notification log table';

CREATE TABLE `statistics`
(
    `statistics_seq`      int       NOT NULL AUTO_INCREMENT COMMENT 'Statistics sequence number',
    `statistics_at`       timestamp NOT NULL COMMENT 'Statistics timestamp',
    `all_count`           int       NOT NULL DEFAULT 0 COMMENT 'Total count',
    `attended_count`      int       NOT NULL DEFAULT 0 COMMENT 'Attendance count',
    `cancelled_count`     int       NOT NULL DEFAULT 0 COMMENT 'Cancellation count',
    PRIMARY KEY (`statistics_seq`),
    INDEX idx_statistics_at (`statistics_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Daily/periodic statistics data';