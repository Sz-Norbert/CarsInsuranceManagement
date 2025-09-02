package com.example.carins.service;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyExpiryLoggerService {

    private final InsurancePolicyRepository policyRepository;

    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void logExpiredPolicies() {
        LocalDate currentDate = LocalDate.now();
        List<InsurancePolicy> expiredPolicies = policyRepository.findExpiredUnloggedPolicies(currentDate);
        
        for (InsurancePolicy policy : expiredPolicies) {
            logExpiredPolicy(policy);
            policyRepository.markPolicyAsLogged(policy.getId());
        }
    }

    private void logExpiredPolicy(InsurancePolicy policy) {
        log.info("Policy {} for car {} expired on {}", 
                policy.getId(), 
                policy.getCar().getId(), 
                policy.getEndDate());
    }
}