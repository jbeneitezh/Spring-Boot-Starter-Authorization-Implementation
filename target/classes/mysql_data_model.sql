
CREATE TABLE `refresh_token` (
  `id` bigint NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `expiration_date` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

CREATE TABLE `roles` (
  `id` bigint NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

CREATE TABLE `users` (
  `id` bigint NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `origin` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

CREATE TABLE `user_roles` (
  `id` bigint NOT NULL,
  `USER_ID` bigint NOT NULL,
  `ROLE_ID` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE `refresh_token`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_r4k4edos30bx9neoq81mdvwph` (`token`),
  ADD KEY `FKjtx87i0jvq2svedphegvdwcuy` (`user_id`);


ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`);


ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);


ALTER TABLE `user_roles`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`ROLE_ID`),
  ADD KEY `FKhfh9dx7w3ubf1co1vdev94g3f` (`USER_ID`);



ALTER TABLE `refresh_token`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;


ALTER TABLE `roles`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

ALTER TABLE `users`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;


ALTER TABLE `user_roles`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;


ALTER TABLE `refresh_token`
  ADD CONSTRAINT `FKjtx87i0jvq2svedphegvdwcuy` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);


ALTER TABLE `user_roles`
  ADD CONSTRAINT `FK_USER_ROLES_ROLE_ID` FOREIGN KEY (`ROLE_ID`) REFERENCES `ROLES` (`id`),
  ADD CONSTRAINT `FK_USER_ROLES_USER_ID` FOREIGN KEY (`USER_ID`) REFERENCES `USERS` (`id`),
  ADD CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`ROLE_ID`) REFERENCES `roles` (`id`),
  ADD CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`USER_ID`) REFERENCES `users` (`id`);
  
INSERT INTO roles (description) VALUES ('ROLE_admin');
COMMIT;

