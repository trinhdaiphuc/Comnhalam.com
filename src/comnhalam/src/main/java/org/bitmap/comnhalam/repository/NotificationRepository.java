package org.bitmap.comnhalam.repository;

import org.bitmap.comnhalam.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
