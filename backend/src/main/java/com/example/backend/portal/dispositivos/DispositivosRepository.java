package com.example.backend.portal.dispositivos;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DispositivosRepository extends JpaRepository<Dispositivos, Integer> {
    boolean existsByDeviceId(String deviceId);
    boolean existsByDeviceIdAndIdNot(String deviceId, Integer id);
    boolean existsByPushTokenAndAtivoTrue(String pushToken);
    boolean existsByPushTokenAndAtivoTrueAndIdNot(String pushToken, Integer id);
}
