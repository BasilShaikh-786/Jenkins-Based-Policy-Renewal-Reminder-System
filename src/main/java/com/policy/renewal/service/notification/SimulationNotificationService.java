package com.policy.renewal.service.notification;

import com.policy.renewal.model.Policy;
import com.policy.renewal.model.ReminderRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * SimulationNotificationService — safe, non-destructive notification service.
 *
 * <p>Implements Requirement C: Safe notification behaviour. Operates in dry-run/simulation
 * mode without requiring external SMTP configuration or sending real emails to recipients.
 * Logs structured simulation messages and returns a SIMULATED result status.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Service
public class SimulationNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(SimulationNotificationService.class);

    @Value("${notification.simulation-mode:true}")
    private boolean simulationMode = true;

    public SimulationNotificationService() {
    }

    public SimulationNotificationService(boolean simulationMode) {
        this.simulationMode = simulationMode;
    }

    @Override
    public NotificationResult sendRenewalReminder(Policy policy, int reminderWindowDays) {
        if (policy == null) {
            return NotificationResult.failure(
                    ReminderRecord.CHANNEL_SIMULATION,
                    "Policy cannot be null"
            );
        }

        String recipient = policy.getCustomerEmail();
        if (recipient == null || recipient.isBlank()) {
            String errorMsg = "Customer email is not configured for policy " + policy.getPolicyNumber();
            log.warn("[NOTIFICATION-FAILED] {}", errorMsg);
            return NotificationResult.failure(
                    simulationMode ? ReminderRecord.CHANNEL_SIMULATION : ReminderRecord.CHANNEL_EMAIL,
                    errorMsg
            );
        }

        if (simulationMode) {
            String simulatedMessage = String.format(
                    "[SIMULATION] Renewal Reminder Dispatched -> Policy: %s | Customer: %s <%s> | Type: %s | Expiry: %s | Window: %d days | Premium: ₹ %.2f",
                    policy.getPolicyNumber(),
                    policy.getCustomerName(),
                    recipient,
                    policy.getPolicyType(),
                    policy.getExpiryDate(),
                    reminderWindowDays,
                    policy.getPremiumAmount()
            );

            log.info("{}", simulatedMessage);
            return NotificationResult.simulated(simulatedMessage);
        } else {
            // Placeholder for future real SMTP integration when SMTP credentials are configured
            log.warn("[REAL-MAIL-UNAVAILABLE] Real email dispatch is disabled; falling back to simulation for {}", policy.getPolicyNumber());
            return NotificationResult.simulated("[SIMULATION-FALLBACK] Real mail disabled: simulated reminder for " + policy.getPolicyNumber());
        }
    }

    @Override
    public boolean isSimulationMode() {
        return simulationMode;
    }

    public void setSimulationMode(boolean simulationMode) {
        this.simulationMode = simulationMode;
    }
}
