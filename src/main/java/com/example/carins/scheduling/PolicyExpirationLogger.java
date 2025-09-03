package com.example.carins.scheduling;

import com.example.carins.repo.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class PolicyExpirationLogger {

    private static final Logger logger = LoggerFactory.getLogger(PolicyExpirationLogger.class);

    private final InsurancePolicyRepository insurancePolicyRepository;

    // Keep track of already-logged policies
    private final Set<Long> loggedPolicies = new HashSet<>();

    public PolicyExpirationLogger(InsurancePolicyRepository insurancePolicyRepository) {
        this.insurancePolicyRepository = insurancePolicyRepository;
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void logExpiredPolicies() {
        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);

        // Fetch policies that ended yesterday or today
        insurancePolicyRepository.findAll().stream()
                .filter(policy -> !loggedPolicies.contains(policy.getId())) // not logged yet
                .filter(policy -> !policy.getEndDate().isAfter(now))        // expired
                .filter(policy -> !policy.getEndDate().isBefore(yesterday)) // expired within last 1 day
                .forEach(policy -> {
                    logger.info("Policy {} for car {} expired on {}",
                            policy.getId(),
                            policy.getCar().getId(),
                            policy.getEndDate());
                    loggedPolicies.add(policy.getId()); // mark as logged
                });
    }
}