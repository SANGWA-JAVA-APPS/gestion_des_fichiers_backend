package com.bar.gestiondesfichier.seeders;


import com.bar.gestiondesfichier.document.model.*;
import com.bar.gestiondesfichier.document.repository.*;
import com.bar.gestiondesfichier.entity.Account;
import com.bar.gestiondesfichier.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final DocStatusRepository docStatusRepository;
    private final DocumentRepository documentRepository;
    private final NormeLoiRepository normeLoiRepository;

    @Value("${app.seed.norme-loi.enabled:true}")
    private boolean seedEnabled;

    @Value("${app.seed.norme-loi.count:0}")
    private int normeLoiCount;

    @Override
    public void run(String... args) {

        if (!seedEnabled || normeLoiCount <= 0) {
            return;
        }

        if (normeLoiRepository.count() > 100) {
            return;
        }

        Account admin = seedAccount();
        DocStatus validated = seedStatus();
        List<Document> documents = seedDocuments(admin);

        seedNormeLois(admin, validated, documents, normeLoiCount);
    }

    private Account seedAccount() {
        return accountRepository.findAll().stream().findFirst()
                .orElseGet(() -> {
                    Account account = new Account();
                    account.setUsername("admin");
                    account.setActive(true);
                    return accountRepository.save(account);
                });
    }

    private DocStatus seedStatus() {
        return docStatusRepository.findAll().stream().findFirst()
                .orElseGet(() -> {
                    DocStatus status = new DocStatus();
                    status.setName("VALIDATED");
                    return docStatusRepository.save(status);
                });
    }

    private List<Document> seedDocuments(Account owner) {

        if (documentRepository.count() >= 10) {
            return documentRepository.findAll();
        }

        List<Document> documents = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Document doc = new Document(
                    "norme_doc_" + i + ".pdf",
                    "NORME_ORIGINAL_" + i + ".pdf",
                    "application/pdf",
                    120_000L + (i * 10_000),
                    "/uploads/normes/norme_" + i + ".pdf",
                    owner,
                    LocalDateTime.now().plusYears(5)
            );
            documents.add(doc);
        }

        return documentRepository.saveAll(documents);
    }

    private void seedNormeLois(
            Account admin,
            DocStatus status,
            List<Document> documents,
            int total
    ) {

        List<String> domains = List.of(
                "Health",
                "Industry",
                "IT & Security",
                "Environment",
                "Education",
                "Laboratory",
                "Finance"
        );

        List<NormeLoi> batch = new ArrayList<>();

        for (int i = 1; i <= total; i++) {

            Document document = documents.get(i % documents.size());

            NormeLoi loi = new NormeLoi(
                    admin,
                    document,
                    status,
                    String.format("ISO-%05d", i)
            );

            loi.setDescription("Generated norme loi number " + i);
            loi.setDomaineApplication(domains.get(i % domains.size()));
            loi.setDateVigueur(
                    LocalDateTime.now().minusDays((long) (Math.random() * 2000))
            );
            loi.setActive(i % 6 != 0);

            batch.add(loi);

            if (batch.size() == 50) {
                normeLoiRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            normeLoiRepository.saveAll(batch);
        }
    }
}
