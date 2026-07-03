package com.example.backend.privacy;

import com.example.backend.auditoria.logAcoes.LogAcoesRepository;
import com.example.backend.auditoria.logErros.LogErrosRepository;
import com.example.backend.master.platform.provisioningLogs.ProvisioningLogsRepository;
import com.example.backend.portal.dispositivos.DispositivosRepository;
import com.example.backend.portal.sessoes.SessoesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataRetentionJob {

    private static final Logger log = LoggerFactory.getLogger(DataRetentionJob.class);

    private final DataRetentionProperties properties;
    private final LogAcoesRepository logAcoesRepository;
    private final LogErrosRepository logErrosRepository;
    private final ProvisioningLogsRepository provisioningLogsRepository;
    private final SessoesRepository sessoesRepository;
    private final DispositivosRepository dispositivosRepository;

    public DataRetentionJob(
            DataRetentionProperties properties,
            LogAcoesRepository logAcoesRepository,
            LogErrosRepository logErrosRepository,
            ProvisioningLogsRepository provisioningLogsRepository,
            SessoesRepository sessoesRepository,
            DispositivosRepository dispositivosRepository
    ) {
        this.properties = properties;
        this.logAcoesRepository = logAcoesRepository;
        this.logErrosRepository = logErrosRepository;
        this.provisioningLogsRepository = provisioningLogsRepository;
        this.sessoesRepository = sessoesRepository;
        this.dispositivosRepository = dispositivosRepository;
    }

    @Scheduled(cron = "${app.privacy.retention.cron:0 30 2 * * *}")
    public void purgeExpiredData() {
        long deletedActionLogs = logAcoesRepository.deleteByCreatedAtBefore(
                LocalDateTime.now().minusDays(properties.getActionLogsDays())
        );
        long deletedErrorLogs = logErrosRepository.deleteByCreatedAtBefore(
                LocalDateTime.now().minusDays(properties.getErrorLogsDays())
        );
        long deletedProvisioningLogs = provisioningLogsRepository.deleteByCreatedAtBefore(
                LocalDateTime.now().minusDays(properties.getProvisioningLogsDays())
        );
        long deletedLoggedOutSessions = sessoesRepository.deleteByDataLogoutBefore(
                LocalDateTime.now().minusDays(properties.getSessionsDays())
        );
        long deletedExpiredSessions = sessoesRepository.deleteByExpiracaoBefore(
                LocalDateTime.now().minusDays(properties.getSessionsDays())
        );
        long deletedInactiveDevices = dispositivosRepository.deleteByAtivoFalseAndCreatedAtBefore(
                LocalDateTime.now().minusDays(properties.getInactiveDevicesDays())
        );

        log.info(
                "Retencao LGPD executada: actionLogs={}, errorLogs={}, provisioningLogs={}, loggedOutSessions={}, expiredSessions={}, inactiveDevices={}",
                deletedActionLogs,
                deletedErrorLogs,
                deletedProvisioningLogs,
                deletedLoggedOutSessions,
                deletedExpiredSessions,
                deletedInactiveDevices
        );
    }
}
