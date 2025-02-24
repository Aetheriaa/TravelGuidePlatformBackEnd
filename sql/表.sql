-- 1. 用户表 (users)
CREATE TABLE `users` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `nickname` VARCHAR(255) DEFAULT NULL,
  `avatar` VARCHAR(255) DEFAULT NULL,
  `gender` ENUM('M','F','U') DEFAULT NULL,
  `birthday` DATE DEFAULT NULL,
  `phone_number` VARCHAR(20) DEFAULT NULL,
  `bio` TEXT DEFAULT NULL,
  `registration_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login_time` TIMESTAMP NULL DEFAULT NULL,
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '用户状态，1正常，0封禁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 攻略表 (guides)
CREATE TABLE `guides` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `content` MEDIUMTEXT NOT NULL,
  `cover_image` VARCHAR(255) DEFAULT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `view_count` INT UNSIGNED NOT NULL DEFAULT 0,
  `like_count` INT UNSIGNED NOT NULL DEFAULT 0,
  `comment_count` INT UNSIGNED NOT NULL DEFAULT 0,
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1: 发布, 0: 草稿, -1: 删除',
   `tags` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '攻略标签，用逗号隔开',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_title` (`title`),
  FULLTEXT KEY `ft_content` (`content`),
  CONSTRAINT `fk_guides_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 评论表 (comments)
CREATE TABLE `comments` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `guide_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `content` TEXT NOT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `parent_comment_id` BIGINT UNSIGNED DEFAULT NULL,
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '评论状态（1:正常，0：删除）',
  PRIMARY KEY (`id`),
  KEY `idx_guide_id` (`guide_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_comment_id` (`parent_comment_id`),
  CONSTRAINT `fk_comments_guides` FOREIGN KEY (`guide_id`) REFERENCES `guides` (`id`),
  CONSTRAINT `fk_comments_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_comments_comments` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 点赞表 (likes)
CREATE TABLE `likes` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `guide_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_guide_user` (`guide_id`, `user_id`),
  KEY `idx_user_guide` (`user_id`, `guide_id`),
  CONSTRAINT `fk_likes_guides` FOREIGN KEY (`guide_id`) REFERENCES `guides` (`id`),
  CONSTRAINT `fk_likes_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. 收藏表 (favorites)
CREATE TABLE `favorites` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `guide_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_guide_user` (`guide_id`, `user_id`),
  KEY `idx_user_guide` (`user_id`, `guide_id`),
  CONSTRAINT `fk_favorites_guides` FOREIGN KEY (`guide_id`) REFERENCES `guides` (`id`),
  CONSTRAINT `fk_favorites_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. 关注表 (follows)
CREATE TABLE `follows` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `follower_id` BIGINT UNSIGNED NOT NULL,
  `following_id` BIGINT UNSIGNED NOT NULL,
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_following` (`follower_id`, `following_id`),
  KEY `idx_following_follower` (`following_id`, `follower_id`),
  CONSTRAINT `fk_follows_users_follower` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_follows_users_following` FOREIGN KEY (`following_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. 行程表 (itineraries)
CREATE TABLE `itineraries` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  `description` TEXT,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_itineraries_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. 行程详情表 (itinerary_details)
CREATE TABLE `itinerary_details` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `itinerary_id` BIGINT UNSIGNED NOT NULL,
  `type` ENUM('attraction', 'hotel', 'transport', 'restaurant') NOT NULL,
  `item_id` BIGINT UNSIGNED NOT NULL,
  `day` INT NOT NULL,
  `start_time` TIME DEFAULT NULL,
  `end_time` TIME DEFAULT NULL,
  `notes` TEXT,
  PRIMARY KEY (`id`),
  KEY `idx_itinerary_id` (`itinerary_id`),
  CONSTRAINT `fk_itinerary_details_itineraries` FOREIGN KEY (`itinerary_id`) REFERENCES `itineraries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. 旅游产品表 (products)
CREATE TABLE `products` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `type` ENUM('flight', 'hotel', 'ticket', 'package') NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `price` DECIMAL(10, 2) NOT NULL,
  `supplier` VARCHAR(255) DEFAULT NULL,
  `stock` INT UNSIGNED NOT NULL,
    `image` VARCHAR(255) DEFAULT NULL,
      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
      `status` TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. 订单表 (orders)
CREATE TABLE `orders` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `product_id` BIGINT UNSIGNED NOT NULL,
  `order_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `quantity` INT UNSIGNED NOT NULL,
  `total_price` DECIMAL(10, 2) NOT NULL,
  `status` TINYINT NOT NULL,
  `payment_method` VARCHAR(50) DEFAULT NULL,
    `payment_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  CONSTRAINT `fk_orders_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_orders_products` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE `user_tags` (
                             `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                             `user_id` BIGINT UNSIGNED NOT NULL,
                             `tag` VARCHAR(255) NOT NULL,
                             PRIMARY KEY (`id`),
                             KEY `idx_user_id` (`user_id`),
                             FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tags` (
                        `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                        `name` VARCHAR(255) NOT NULL UNIQUE,  -- 标签名称 (唯一)
                        `popularity` INT UNSIGNED NOT NULL DEFAULT 0, -- 标签热度 (出现的次数)
                        `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 攻略-标签关联表 (多对多)
CREATE TABLE `guide_tags` (
                              `guide_id` BIGINT UNSIGNED NOT NULL,
                              `tag_id` INT UNSIGNED NOT NULL,
                              PRIMARY KEY (`guide_id`, `tag_id`),
                              FOREIGN KEY (`guide_id`) REFERENCES `guides` (`id`) ON DELETE CASCADE,  -- 级联删除
                              FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE     -- 级联删除
) ENGINE=InnoDB;