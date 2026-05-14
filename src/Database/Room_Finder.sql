-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th5 14, 2026 lúc 12:55 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `Room_Finder`
--
CREATE DATABASE IF NOT EXISTS Room_Finder;
USE Room_Finder;
-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `accounts`
--

CREATE TABLE `accounts` (
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `accounts`
--

INSERT INTO `accounts` (`username`, `password`) VALUES
('admin', '123'),
('anpham', '123456'),
('vunguyen', '123456');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `amenities`
--

CREATE TABLE `amenities` (
  `amenity_id` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `amenities`
--

INSERT INTO `amenities` (`amenity_id`, `name`) VALUES
('1598b07b-808e-4a4a-aa18-d8c5a16256a4', 'Điều hòa'),
('44d49938-8a1b-4e7b-bec5-fdeb64bfb31d', 'Chỗ để xe an ninh'),
('845a35e5-6a01-48e6-bb8a-0805702892dc', 'Tủ lạnh'),
('b1c89707-c9fa-4d41-84c5-f40f681be1d4', 'Máy giặt'),
('d6ec6f9e-239c-4b05-955d-0cb0e8299cef', 'Bếp điện'),
('d99aec45-ff81-4091-b06a-63edda98ce78', 'Giường nệm'),
('e5f2f336-4551-4973-9230-5d71e3aeae10', 'Wifi miễn phí');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `reviews`
--

CREATE TABLE `reviews` (
  `review_id` varchar(50) NOT NULL,
  `room_id` varchar(50) NOT NULL,
  `tenant_id` varchar(50) NOT NULL,
  `rating` int(11) DEFAULT NULL CHECK (`rating` >= 1 and `rating` <= 5),
  `comment` text DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `reviews`
--

INSERT INTO `reviews` (`review_id`, `room_id`, `tenant_id`, `rating`, `comment`, `created_at`) VALUES
('2faef67b-2181-4ade-a859-4941426896b7', '5a56c60c-7079-4d60-9d30-42b867307c67', '23e9e68d-8f42-446d-a35a-7ed2cf758e41', 5, 'Nhà đẹp', '2026-05-14 16:44:47');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `rooms`
--

CREATE TABLE `rooms` (
  `room_id` varchar(50) NOT NULL,
  `landlord_id` varchar(50) NOT NULL,
  `title` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `area` int(11) NOT NULL,
  `price` double NOT NULL,
  `status` enum('PENDING','APPROVED','DECLINED') DEFAULT 'PENDING',
  `availability` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `rooms`
--

INSERT INTO `rooms` (`room_id`, `landlord_id`, `title`, `address`, `description`, `area`, `price`, `status`, `availability`, `created_at`) VALUES
('5a56c60c-7079-4d60-9d30-42b867307c67', 'e37fa297-54fd-4f29-9faa-f9dff4461777', '69 Nguyễn Lương Bằng', '69 Nguyễn Lương Bằng, Liên Chiểu, Đà Nẵng', 'Phòng trọ rộng rãi, thoáng mát, an ninh tốt.\nĐầy đủ tiện nghi cơ bản, gần chợ và các trường đại học, thuận tiện đi lại.', 15, 1500000, 'APPROVED', 1, '2026-05-14 16:42:44'),
('6a442de1-029a-477b-8664-e6cddcdae914', 'e37fa297-54fd-4f29-9faa-f9dff4461777', '300 Nguyễn Lương Bằng', '300 Nguyễn Lương Bằng, Liên Chiểu, Đà Nẵng', 'Không gian sống yên tĩnh, sạch sẽ, có chỗ để xe rộng rãi.\nPhòng mới xây, thiết kế hiện đại, phù hợp cho sinh viên và người đi làm.', 43, 1230000, 'APPROVED', 1, '2026-05-14 17:50:29'),
('7e186d11-be57-457d-8fa1-a54df786d245', 'e37fa297-54fd-4f29-9faa-f9dff4461777', '12 Âu Cơ', '12 Âu Cơ, Liên Chiểu, Đà Nẵng', 'Phòng trọ giá cả hợp lý, khu dân cư văn minh.\nGần nhiều cửa hàng tiện lợi, trang bị sẵn giường nệm và quạt mát.', 34, 3230000, 'APPROVED', 1, '2026-05-14 17:51:44'),
('e87d79ad-90fe-4d5a-abac-a083b45290c2', 'e37fa297-54fd-4f29-9faa-f9dff4461777', '76 Ngô Sĩ Liên', '76 Ngô Sĩ Liên, Liên Chiểu, Đà Nẵng', 'Môi trường sống tiện nghi, có bếp nấu ăn riêng biệt.\nHệ thống phòng cháy chữa cháy an toàn, ban công thoáng gió.', 12, 4560000, 'APPROVED', 1, '2026-05-14 17:51:16');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `room_amenities`
--

CREATE TABLE `room_amenities` (
  `room_id` varchar(50) NOT NULL,
  `amenity_id` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `room_amenities`
--

INSERT INTO `room_amenities` (`room_id`, `amenity_id`) VALUES
('5a56c60c-7079-4d60-9d30-42b867307c67', '1598b07b-808e-4a4a-aa18-d8c5a16256a4'),
('5a56c60c-7079-4d60-9d30-42b867307c67', 'b1c89707-c9fa-4d41-84c5-f40f681be1d4'),
('6a442de1-029a-477b-8664-e6cddcdae914', '845a35e5-6a01-48e6-bb8a-0805702892dc'),
('6a442de1-029a-477b-8664-e6cddcdae914', 'b1c89707-c9fa-4d41-84c5-f40f681be1d4'),
('7e186d11-be57-457d-8fa1-a54df786d245', '1598b07b-808e-4a4a-aa18-d8c5a16256a4'),
('7e186d11-be57-457d-8fa1-a54df786d245', 'b1c89707-c9fa-4d41-84c5-f40f681be1d4'),
('e87d79ad-90fe-4d5a-abac-a083b45290c2', '845a35e5-6a01-48e6-bb8a-0805702892dc'),
('e87d79ad-90fe-4d5a-abac-a083b45290c2', 'b1c89707-c9fa-4d41-84c5-f40f681be1d4');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `room_images`
--

CREATE TABLE `room_images` (
  `image_id` int(11) NOT NULL,
  `room_id` varchar(50) NOT NULL,
  `image_path` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `room_images`
--

INSERT INTO `room_images` (`image_id`, `room_id`, `image_path`) VALUES
(1, '5a56c60c-7079-4d60-9d30-42b867307c67', 'src/Images/room1_image.png'),
(2, '5a56c60c-7079-4d60-9d30-42b867307c67', 'src/Images/room2_image.png'),
(3, '5a56c60c-7079-4d60-9d30-42b867307c67', 'src/Images/room3_image.png'),
(4, '6a442de1-029a-477b-8664-e6cddcdae914', 'src/Images/room4_image.png'),
(5, '6a442de1-029a-477b-8664-e6cddcdae914', 'src/Images/room5_image.png'),
(6, '6a442de1-029a-477b-8664-e6cddcdae914', 'src/Images/room6_image.png'),
(7, 'e87d79ad-90fe-4d5a-abac-a083b45290c2', 'src/Images/room7_image.png'),
(8, '7e186d11-be57-457d-8fa1-a54df786d245', 'src/Images/room8_image.png');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `user_id` varchar(50) NOT NULL,
  `username` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `phone_number` varchar(15) DEFAULT NULL,
  `role` enum('ADMIN','LANDLORD','TENANT') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`user_id`, `username`, `name`, `phone_number`, `role`) VALUES
('23e9e68d-8f42-446d-a35a-7ed2cf758e41', 'vunguyen', 'Nguyễn Vũ', '0823345342', 'TENANT'),
('ADMIN01', 'admin', 'Nguyễn Quản Trị', '0889298557', 'ADMIN'),
('e37fa297-54fd-4f29-9faa-f9dff4461777', 'anpham', 'Phạm An', '0982543536', 'LANDLORD');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`username`);

--
-- Chỉ mục cho bảng `amenities`
--
ALTER TABLE `amenities`
  ADD PRIMARY KEY (`amenity_id`);

--
-- Chỉ mục cho bảng `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`review_id`),
  ADD KEY `room_id` (`room_id`),
  ADD KEY `tenant_id` (`tenant_id`);

--
-- Chỉ mục cho bảng `rooms`
--
ALTER TABLE `rooms`
  ADD PRIMARY KEY (`room_id`),
  ADD KEY `landlord_id` (`landlord_id`);

--
-- Chỉ mục cho bảng `room_amenities`
--
ALTER TABLE `room_amenities`
  ADD PRIMARY KEY (`room_id`,`amenity_id`),
  ADD KEY `amenity_id` (`amenity_id`);

--
-- Chỉ mục cho bảng `room_images`
--
ALTER TABLE `room_images`
  ADD PRIMARY KEY (`image_id`),
  ADD KEY `room_id` (`room_id`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `room_images`
--
ALTER TABLE `room_images`
  MODIFY `image_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`tenant_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `rooms`
--
ALTER TABLE `rooms`
  ADD CONSTRAINT `rooms_ibfk_1` FOREIGN KEY (`landlord_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `room_amenities`
--
ALTER TABLE `room_amenities`
  ADD CONSTRAINT `room_amenities_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `room_amenities_ibfk_2` FOREIGN KEY (`amenity_id`) REFERENCES `amenities` (`amenity_id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `room_images`
--
ALTER TABLE `room_images`
  ADD CONSTRAINT `room_images_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`username`) REFERENCES `accounts` (`username`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
