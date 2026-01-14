package com.bar.gestiondesfichier.location.seed;

import com.bar.gestiondesfichier.location.model.Country;
import com.bar.gestiondesfichier.location.repository.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(1) // Run early in startup
public class CountrySeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CountrySeeder.class);

    // Flag to track if seeder has already run in this session
    private static boolean hasRun = false;

    private final CountryRepository countryRepository;

    public CountrySeeder(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void run(String... args) {
        // Prevent multiple runs in same session
        if (hasRun) {
            logger.info("🔄 Country seeder has already run in this session - skipping");
            return;
        }

        try {
            logger.info("🌍 Starting COMPLETE country seeding process...");
            seedAllCountriesSafely();
            hasRun = true;
            logger.info("✅ Complete country seeding process finished!");
        } catch (Exception e) {
            // CATCH-ALL: Prevent application crash
            logger.error("❌ Unexpected error in country seeding. Application will continue normally.", e);
            // IMPORTANT: Don't rethrow - let application continue
        }
    }

    /**
     * Safe seeding method for ALL countries
     */
    @Transactional
    protected void seedAllCountriesSafely() {
        List<CountryData> allCountries = getAllCountries();
        AtomicInteger addedCount = new AtomicInteger(0);
        AtomicInteger skippedCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        logger.info("🔄 Seeding {} countries from all continents", allCountries.size());

        // First, check how many countries already exist
        long existingCount = countryRepository.countByActiveTrue();
        logger.info("📊 Found {} existing countries in database", existingCount);

        // If all countries already exist, skip the process
        if (existingCount >= allCountries.size()) {
            logger.info("✅ All countries already exist in database. Skipping seeding.");
            return;
        }

        for (CountryData data : allCountries) {
            try {
                // Check if country already exists
                if (countryAlreadyExists(data.isoCode, data.name)) {
                    skippedCount.incrementAndGet();
                    continue;
                }

                // Validate data before creating
                validateCountryData(data);

                // Create and save country
                Country country = createCountryEntity(data);
                saveCountrySafely(country, data, addedCount, errorCount);

            } catch (DataIntegrityViolationException e) {
                skippedCount.incrementAndGet();
            } catch (Exception e) {
                errorCount.incrementAndGet();
                logger.warn("⚠️ Error processing country {} ({}): {}",
                        data.name, data.isoCode, e.getMessage());
            }
        }

        // Log summary
        logSeedingSummary(allCountries.size(), addedCount.get(), skippedCount.get(), errorCount.get());
    }

    /**
     * Check if country exists
     */
    private boolean countryAlreadyExists(String isoCode, String name) {
        try {
            return countryRepository.findByIsoCodeAndActiveTrue(isoCode).isPresent() ||
                    countryRepository.findByNameIgnoreCaseAndActiveTrue(name).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate country data
     */
    private void validateCountryData(CountryData data) {
        if (data.isoCode == null || data.isoCode.trim().isEmpty()) {
            throw new IllegalArgumentException("ISO code is required");
        }
        if (data.name == null || data.name.trim().isEmpty()) {
            throw new IllegalArgumentException("Country name is required");
        }
    }

    /**
     * Create Country entity
     */
    private Country createCountryEntity(CountryData data) {
        Country country = new Country(
                data.name.trim(),
                data.description != null ? data.description.trim() : "",
                data.isoCode.trim().toUpperCase(),
                data.phoneCode != null ? data.phoneCode.trim() : "",
                data.flagUrl != null ? data.flagUrl.trim() : ""
        );
        country.setActive(true);
        return country;
    }

    /**
     * Safe save with error handling
     */
    private void saveCountrySafely(Country country, CountryData data,
                                   AtomicInteger addedCount, AtomicInteger errorCount) {
        try {
            countryRepository.save(country);
            addedCount.incrementAndGet();
        } catch (DataIntegrityViolationException e) {
            errorCount.incrementAndGet();
        } catch (Exception e) {
            errorCount.incrementAndGet();
        }
    }

    /**
     * Log summary
     */
    private void logSeedingSummary(int total, int added, int skipped, int errors) {
        try {
            long finalCount = countryRepository.countByActiveTrue();
            logger.info("=".repeat(60));
            logger.info("🌍 COMPLETE COUNTRY SEEDING SUMMARY");
            logger.info("=".repeat(60));
            logger.info("📊 Total countries in seed file: {}", total);
            logger.info("✅ Newly added: {}", added);
            logger.info("⏭️ Already existed: {}", skipped);
            logger.info("❌ Errors: {}", errors);
            logger.info("🏁 Total in database: {}", finalCount);
            logger.info("📈 Coverage: {}/{} countries", finalCount, total);
            logger.info("=".repeat(60));
        } catch (Exception e) {
            logger.error("Failed to log seeding summary: {}", e.getMessage());
        }
    }

    /**
     * COMPLETE LIST OF ALL 195 COUNTRIES
     */
    private List<CountryData> getAllCountries() {
        List<CountryData> allCountries = new ArrayList<>();

        // =================== AFRICA (54 countries) ===================
        allCountries.addAll(Arrays.asList(
                new CountryData("Algeria", "People's Democratic Republic of Algeria", "DZA", "+213", "https://flagcdn.com/w320/dz.png"),
                new CountryData("Angola", "Republic of Angola", "AGO", "+244", "https://flagcdn.com/w320/ao.png"),
                new CountryData("Benin", "Republic of Benin", "BEN", "+229", "https://flagcdn.com/w320/bj.png"),
                new CountryData("Botswana", "Republic of Botswana", "BWA", "+267", "https://flagcdn.com/w320/bw.png"),
                new CountryData("Burkina Faso", "Burkina Faso", "BFA", "+226", "https://flagcdn.com/w320/bf.png"),
                new CountryData("Burundi", "Republic of Burundi", "BDI", "+257", "https://flagcdn.com/w320/bi.png"),
                new CountryData("Cabo Verde", "Republic of Cabo Verde", "CPV", "+238", "https://flagcdn.com/w320/cv.png"),
                new CountryData("Cameroon", "Republic of Cameroon", "CMR", "+237", "https://flagcdn.com/w320/cm.png"),
                new CountryData("Central African Republic", "Central African Republic", "CAF", "+236", "https://flagcdn.com/w320/cf.png"),
                new CountryData("Chad", "Republic of Chad", "TCD", "+235", "https://flagcdn.com/w320/td.png"),
                new CountryData("Comoros", "Union of the Comoros", "COM", "+269", "https://flagcdn.com/w320/km.png"),
                new CountryData("Congo", "Republic of the Congo", "COG", "+242", "https://flagcdn.com/w320/cg.png"),
                new CountryData("DR Congo", "Democratic Republic of the Congo", "COD", "+243", "https://flagcdn.com/w320/cd.png"),
                new CountryData("Djibouti", "Republic of Djibouti", "DJI", "+253", "https://flagcdn.com/w320/dj.png"),
                new CountryData("Egypt", "Arab Republic of Egypt", "EGY", "+20", "https://flagcdn.com/w320/eg.png"),
                new CountryData("Equatorial Guinea", "Republic of Equatorial Guinea", "GNQ", "+240", "https://flagcdn.com/w320/gq.png"),
                new CountryData("Eritrea", "State of Eritrea", "ERI", "+291", "https://flagcdn.com/w320/er.png"),
                new CountryData("Eswatini", "Kingdom of Eswatini", "SWZ", "+268", "https://flagcdn.com/w320/sz.png"),
                new CountryData("Ethiopia", "Federal Democratic Republic of Ethiopia", "ETH", "+251", "https://flagcdn.com/w320/et.png"),
                new CountryData("Gabon", "Gabonese Republic", "GAB", "+241", "https://flagcdn.com/w320/ga.png"),
                new CountryData("Gambia", "Republic of the Gambia", "GMB", "+220", "https://flagcdn.com/w320/gm.png"),
                new CountryData("Ghana", "Republic of Ghana", "GHA", "+233", "https://flagcdn.com/w320/gh.png"),
                new CountryData("Guinea", "Republic of Guinea", "GIN", "+224", "https://flagcdn.com/w320/gn.png"),
                new CountryData("Guinea-Bissau", "Republic of Guinea-Bissau", "GNB", "+245", "https://flagcdn.com/w320/gw.png"),
                new CountryData("Ivory Coast", "Republic of Côte d'Ivoire", "CIV", "+225", "https://flagcdn.com/w320/ci.png"),
                new CountryData("Kenya", "Republic of Kenya", "KEN", "+254", "https://flagcdn.com/w320/ke.png"),
                new CountryData("Lesotho", "Kingdom of Lesotho", "LSO", "+266", "https://flagcdn.com/w320/ls.png"),
                new CountryData("Liberia", "Republic of Liberia", "LBR", "+231", "https://flagcdn.com/w320/lr.png"),
                new CountryData("Libya", "State of Libya", "LBY", "+218", "https://flagcdn.com/w320/ly.png"),
                new CountryData("Madagascar", "Republic of Madagascar", "MDG", "+261", "https://flagcdn.com/w320/mg.png"),
                new CountryData("Malawi", "Republic of Malawi", "MWI", "+265", "https://flagcdn.com/w320/mw.png"),
                new CountryData("Mali", "Republic of Mali", "MLI", "+223", "https://flagcdn.com/w320/ml.png"),
                new CountryData("Mauritania", "Islamic Republic of Mauritania", "MRT", "+222", "https://flagcdn.com/w320/mr.png"),
                new CountryData("Mauritius", "Republic of Mauritius", "MUS", "+230", "https://flagcdn.com/w320/mu.png"),
                new CountryData("Morocco", "Kingdom of Morocco", "MAR", "+212", "https://flagcdn.com/w320/ma.png"),
                new CountryData("Mozambique", "Republic of Mozambique", "MOZ", "+258", "https://flagcdn.com/w320/mz.png"),
                new CountryData("Namibia", "Republic of Namibia", "NAM", "+264", "https://flagcdn.com/w320/na.png"),
                new CountryData("Niger", "Republic of the Niger", "NER", "+227", "https://flagcdn.com/w320/ne.png"),
                new CountryData("Nigeria", "Federal Republic of Nigeria", "NGA", "+234", "https://flagcdn.com/w320/ng.png"),
                new CountryData("Rwanda", "Republic of Rwanda", "RWA", "+250", "https://flagcdn.com/w320/rw.png"),
                new CountryData("São Tomé and Príncipe", "Democratic Republic of São Tomé and Príncipe", "STP", "+239", "https://flagcdn.com/w320/st.png"),
                new CountryData("Senegal", "Republic of Senegal", "SEN", "+221", "https://flagcdn.com/w320/sn.png"),
                new CountryData("Seychelles", "Republic of Seychelles", "SYC", "+248", "https://flagcdn.com/w320/sc.png"),
                new CountryData("Sierra Leone", "Republic of Sierra Leone", "SLE", "+232", "https://flagcdn.com/w320/sl.png"),
                new CountryData("Somalia", "Federal Republic of Somalia", "SOM", "+252", "https://flagcdn.com/w320/so.png"),
                new CountryData("South Africa", "Republic of South Africa", "ZAF", "+27", "https://flagcdn.com/w320/za.png"),
                new CountryData("South Sudan", "Republic of South Sudan", "SSD", "+211", "https://flagcdn.com/w320/ss.png"),
                new CountryData("Sudan", "Republic of the Sudan", "SDN", "+249", "https://flagcdn.com/w320/sd.png"),
                new CountryData("Tanzania", "United Republic of Tanzania", "TZA", "+255", "https://flagcdn.com/w320/tz.png"),
                new CountryData("Togo", "Togolese Republic", "TGO", "+228", "https://flagcdn.com/w320/tg.png"),
                new CountryData("Tunisia", "Tunisian Republic", "TUN", "+216", "https://flagcdn.com/w320/tn.png"),
                new CountryData("Uganda", "Republic of Uganda", "UGA", "+256", "https://flagcdn.com/w320/ug.png"),
                new CountryData("Zambia", "Republic of Zambia", "ZMB", "+260", "https://flagcdn.com/w320/zm.png"),
                new CountryData("Zimbabwe", "Republic of Zimbabwe", "ZWE", "+263", "https://flagcdn.com/w320/zw.png")
        ));

        // =================== ASIA (48 countries) ===================
        allCountries.addAll(Arrays.asList(
                new CountryData("Afghanistan", "Islamic Republic of Afghanistan", "AFG", "+93", "https://flagcdn.com/w320/af.png"),
                new CountryData("Armenia", "Republic of Armenia", "ARM", "+374", "https://flagcdn.com/w320/am.png"),
                new CountryData("Azerbaijan", "Republic of Azerbaijan", "AZE", "+994", "https://flagcdn.com/w320/az.png"),
                new CountryData("Bahrain", "Kingdom of Bahrain", "BHR", "+973", "https://flagcdn.com/w320/bh.png"),
                new CountryData("Bangladesh", "People's Republic of Bangladesh", "BGD", "+880", "https://flagcdn.com/w320/bd.png"),
                new CountryData("Bhutan", "Kingdom of Bhutan", "BTN", "+975", "https://flagcdn.com/w320/bt.png"),
                new CountryData("Brunei", "Nation of Brunei, Abode of Peace", "BRN", "+673", "https://flagcdn.com/w320/bn.png"),
                new CountryData("Cambodia", "Kingdom of Cambodia", "KHM", "+855", "https://flagcdn.com/w320/kh.png"),
                new CountryData("China", "People's Republic of China", "CHN", "+86", "https://flagcdn.com/w320/cn.png"),
                new CountryData("Cyprus", "Republic of Cyprus", "CYP", "+357", "https://flagcdn.com/w320/cy.png"),
                new CountryData("Georgia", "Georgia", "GEO", "+995", "https://flagcdn.com/w320/ge.png"),
                new CountryData("India", "Republic of India", "IND", "+91", "https://flagcdn.com/w320/in.png"),
                new CountryData("Indonesia", "Republic of Indonesia", "IDN", "+62", "https://flagcdn.com/w320/id.png"),
                new CountryData("Iran", "Islamic Republic of Iran", "IRN", "+98", "https://flagcdn.com/w320/ir.png"),
                new CountryData("Iraq", "Republic of Iraq", "IRQ", "+964", "https://flagcdn.com/w320/iq.png"),
                new CountryData("Israel", "State of Israel", "ISR", "+972", "https://flagcdn.com/w320/il.png"),
                new CountryData("Japan", "Japan", "JPN", "+81", "https://flagcdn.com/w320/jp.png"),
                new CountryData("Jordan", "Hashemite Kingdom of Jordan", "JOR", "+962", "https://flagcdn.com/w320/jo.png"),
                new CountryData("Kazakhstan", "Republic of Kazakhstan", "KAZ", "+7", "https://flagcdn.com/w320/kz.png"),
                new CountryData("Kuwait", "State of Kuwait", "KWT", "+965", "https://flagcdn.com/w320/kw.png"),
                new CountryData("Kyrgyzstan", "Kyrgyz Republic", "KGZ", "+996", "https://flagcdn.com/w320/kg.png"),
                new CountryData("Laos", "Lao People's Democratic Republic", "LAO", "+856", "https://flagcdn.com/w320/la.png"),
                new CountryData("Lebanon", "Lebanese Republic", "LBN", "+961", "https://flagcdn.com/w320/lb.png"),
                new CountryData("Malaysia", "Malaysia", "MYS", "+60", "https://flagcdn.com/w320/my.png"),
                new CountryData("Maldives", "Republic of Maldives", "MDV", "+960", "https://flagcdn.com/w320/mv.png"),
                new CountryData("Mongolia", "Mongolia", "MNG", "+976", "https://flagcdn.com/w320/mn.png"),
                new CountryData("Myanmar", "Republic of the Union of Myanmar", "MMR", "+95", "https://flagcdn.com/w320/mm.png"),
                new CountryData("Nepal", "Federal Democratic Republic of Nepal", "NPL", "+977", "https://flagcdn.com/w320/np.png"),
                new CountryData("North Korea", "Democratic People's Republic of Korea", "PRK", "+850", "https://flagcdn.com/w320/kp.png"),
                new CountryData("Oman", "Sultanate of Oman", "OMN", "+968", "https://flagcdn.com/w320/om.png"),
                new CountryData("Pakistan", "Islamic Republic of Pakistan", "PAK", "+92", "https://flagcdn.com/w320/pk.png"),
                new CountryData("Palestine", "State of Palestine", "PSE", "+970", "https://flagcdn.com/w320/ps.png"),
                new CountryData("Philippines", "Republic of the Philippines", "PHL", "+63", "https://flagcdn.com/w320/ph.png"),
                new CountryData("Qatar", "State of Qatar", "QAT", "+974", "https://flagcdn.com/w320/qa.png"),
                new CountryData("Russia", "Russian Federation", "RUS", "+7", "https://flagcdn.com/w320/ru.png"),
                new CountryData("Saudi Arabia", "Kingdom of Saudi Arabia", "SAU", "+966", "https://flagcdn.com/w320/sa.png"),
                new CountryData("Singapore", "Republic of Singapore", "SGP", "+65", "https://flagcdn.com/w320/sg.png"),
                new CountryData("South Korea", "Republic of Korea", "KOR", "+82", "https://flagcdn.com/w320/kr.png"),
                new CountryData("Sri Lanka", "Democratic Socialist Republic of Sri Lanka", "LKA", "+94", "https://flagcdn.com/w320/lk.png"),
                new CountryData("Syria", "Syrian Arab Republic", "SYR", "+963", "https://flagcdn.com/w320/sy.png"),
                new CountryData("Taiwan", "Republic of China (Taiwan)", "TWN", "+886", "https://flagcdn.com/w320/tw.png"),
                new CountryData("Tajikistan", "Republic of Tajikistan", "TJK", "+992", "https://flagcdn.com/w320/tj.png"),
                new CountryData("Thailand", "Kingdom of Thailand", "THA", "+66", "https://flagcdn.com/w320/th.png"),
                new CountryData("Timor-Leste", "Democratic Republic of Timor-Leste", "TLS", "+670", "https://flagcdn.com/w320/tl.png"),
                new CountryData("Turkey", "Republic of Turkey", "TUR", "+90", "https://flagcdn.com/w320/tr.png"),
                new CountryData("Turkmenistan", "Turkmenistan", "TKM", "+993", "https://flagcdn.com/w320/tm.png"),
                new CountryData("United Arab Emirates", "United Arab Emirates", "ARE", "+971", "https://flagcdn.com/w320/ae.png"),
                new CountryData("Uzbekistan", "Republic of Uzbekistan", "UZB", "+998", "https://flagcdn.com/w320/uz.png"),
                new CountryData("Vietnam", "Socialist Republic of Vietnam", "VNM", "+84", "https://flagcdn.com/w320/vn.png"),
                new CountryData("Yemen", "Republic of Yemen", "YEM", "+967", "https://flagcdn.com/w320/ye.png")
        ));

        // =================== EUROPE (44 countries) ===================
        allCountries.addAll(Arrays.asList(
                new CountryData("Albania", "Republic of Albania", "ALB", "+355", "https://flagcdn.com/w320/al.png"),
                new CountryData("Andorra", "Principality of Andorra", "AND", "+376", "https://flagcdn.com/w320/ad.png"),
                new CountryData("Austria", "Republic of Austria", "AUT", "+43", "https://flagcdn.com/w320/at.png"),
                new CountryData("Belarus", "Republic of Belarus", "BLR", "+375", "https://flagcdn.com/w320/by.png"),
                new CountryData("Belgium", "Kingdom of Belgium", "BEL", "+32", "https://flagcdn.com/w320/be.png"),
                new CountryData("Bosnia and Herzegovina", "Bosnia and Herzegovina", "BIH", "+387", "https://flagcdn.com/w320/ba.png"),
                new CountryData("Bulgaria", "Republic of Bulgaria", "BGR", "+359", "https://flagcdn.com/w320/bg.png"),
                new CountryData("Croatia", "Republic of Croatia", "HRV", "+385", "https://flagcdn.com/w320/hr.png"),
                new CountryData("Czech Republic", "Czech Republic", "CZE", "+420", "https://flagcdn.com/w320/cz.png"),
                new CountryData("Denmark", "Kingdom of Denmark", "DNK", "+45", "https://flagcdn.com/w320/dk.png"),
                new CountryData("Estonia", "Republic of Estonia", "EST", "+372", "https://flagcdn.com/w320/ee.png"),
                new CountryData("Finland", "Republic of Finland", "FIN", "+358", "https://flagcdn.com/w320/fi.png"),
                new CountryData("France", "French Republic", "FRA", "+33", "https://flagcdn.com/w320/fr.png"),
                new CountryData("Germany", "Federal Republic of Germany", "DEU", "+49", "https://flagcdn.com/w320/de.png"),
                new CountryData("Greece", "Hellenic Republic", "GRC", "+30", "https://flagcdn.com/w320/gr.png"),
                new CountryData("Hungary", "Hungary", "HUN", "+36", "https://flagcdn.com/w320/hu.png"),
                new CountryData("Iceland", "Iceland", "ISL", "+354", "https://flagcdn.com/w320/is.png"),
                new CountryData("Ireland", "Republic of Ireland", "IRL", "+353", "https://flagcdn.com/w320/ie.png"),
                new CountryData("Italy", "Italian Republic", "ITA", "+39", "https://flagcdn.com/w320/it.png"),
                new CountryData("Kosovo", "Republic of Kosovo", "XKX", "+383", "https://flagcdn.com/w320/xk.png"),
                new CountryData("Latvia", "Republic of Latvia", "LVA", "+371", "https://flagcdn.com/w320/lv.png"),
                new CountryData("Liechtenstein", "Principality of Liechtenstein", "LIE", "+423", "https://flagcdn.com/w320/li.png"),
                new CountryData("Lithuania", "Republic of Lithuania", "LTU", "+370", "https://flagcdn.com/w320/lt.png"),
                new CountryData("Luxembourg", "Grand Duchy of Luxembourg", "LUX", "+352", "https://flagcdn.com/w320/lu.png"),
                new CountryData("Malta", "Republic of Malta", "MLT", "+356", "https://flagcdn.com/w320/mt.png"),
                new CountryData("Moldova", "Republic of Moldova", "MDA", "+373", "https://flagcdn.com/w320/md.png"),
                new CountryData("Monaco", "Principality of Monaco", "MCO", "+377", "https://flagcdn.com/w320/mc.png"),
                new CountryData("Montenegro", "Montenegro", "MNE", "+382", "https://flagcdn.com/w320/me.png"),
                new CountryData("Netherlands", "Kingdom of the Netherlands", "NLD", "+31", "https://flagcdn.com/w320/nl.png"),
                new CountryData("North Macedonia", "Republic of North Macedonia", "MKD", "+389", "https://flagcdn.com/w320/mk.png"),
                new CountryData("Norway", "Kingdom of Norway", "NOR", "+47", "https://flagcdn.com/w320/no.png"),
                new CountryData("Poland", "Republic of Poland", "POL", "+48", "https://flagcdn.com/w320/pl.png"),
                new CountryData("Portugal", "Portuguese Republic", "PRT", "+351", "https://flagcdn.com/w320/pt.png"),
                new CountryData("Romania", "Romania", "ROU", "+40", "https://flagcdn.com/w320/ro.png"),
                new CountryData("San Marino", "Republic of San Marino", "SMR", "+378", "https://flagcdn.com/w320/sm.png"),
                new CountryData("Serbia", "Republic of Serbia", "SRB", "+381", "https://flagcdn.com/w320/rs.png"),
                new CountryData("Slovakia", "Slovak Republic", "SVK", "+421", "https://flagcdn.com/w320/sk.png"),
                new CountryData("Slovenia", "Republic of Slovenia", "SVN", "+386", "https://flagcdn.com/w320/si.png"),
                new CountryData("Spain", "Kingdom of Spain", "ESP", "+34", "https://flagcdn.com/w320/es.png"),
                new CountryData("Sweden", "Kingdom of Sweden", "SWE", "+46", "https://flagcdn.com/w320/se.png"),
                new CountryData("Switzerland", "Swiss Confederation", "CHE", "+41", "https://flagcdn.com/w320/ch.png"),
                new CountryData("Ukraine", "Ukraine", "UKR", "+380", "https://flagcdn.com/w320/ua.png"),
                new CountryData("United Kingdom", "United Kingdom of Great Britain and Northern Ireland", "GBR", "+44", "https://flagcdn.com/w320/gb.png"),
                new CountryData("Vatican City", "Vatican City State", "VAT", "+379", "https://flagcdn.com/w320/va.png")
        ));

        // =================== NORTH AMERICA (23 countries) ===================
        allCountries.addAll(Arrays.asList(
                new CountryData("Antigua and Barbuda", "Antigua and Barbuda", "ATG", "+1-268", "https://flagcdn.com/w320/ag.png"),
                new CountryData("Bahamas", "Commonwealth of The Bahamas", "BHS", "+1-242", "https://flagcdn.com/w320/bs.png"),
                new CountryData("Barbados", "Barbados", "BRB", "+1-246", "https://flagcdn.com/w320/bb.png"),
                new CountryData("Belize", "Belize", "BLZ", "+501", "https://flagcdn.com/w320/bz.png"),
                new CountryData("Canada", "Canada", "CAN", "+1", "https://flagcdn.com/w320/ca.png"),
                new CountryData("Costa Rica", "Republic of Costa Rica", "CRI", "+506", "https://flagcdn.com/w320/cr.png"),
                new CountryData("Cuba", "Republic of Cuba", "CUB", "+53", "https://flagcdn.com/w320/cu.png"),
                new CountryData("Dominica", "Commonwealth of Dominica", "DMA", "+1-767", "https://flagcdn.com/w320/dm.png"),
                new CountryData("Dominican Republic", "Dominican Republic", "DOM", "+1-809", "https://flagcdn.com/w320/do.png"),
                new CountryData("El Salvador", "Republic of El Salvador", "SLV", "+503", "https://flagcdn.com/w320/sv.png"),
                new CountryData("Grenada", "Grenada", "GRD", "+1-473", "https://flagcdn.com/w320/gd.png"),
                new CountryData("Guatemala", "Republic of Guatemala", "GTM", "+502", "https://flagcdn.com/w320/gt.png"),
                new CountryData("Haiti", "Republic of Haiti", "HTI", "+509", "https://flagcdn.com/w320/ht.png"),
                new CountryData("Honduras", "Republic of Honduras", "HND", "+504", "https://flagcdn.com/w320/hn.png"),
                new CountryData("Jamaica", "Jamaica", "JAM", "+1-876", "https://flagcdn.com/w320/jm.png"),
                new CountryData("Mexico", "United Mexican States", "MEX", "+52", "https://flagcdn.com/w320/mx.png"),
                new CountryData("Nicaragua", "Republic of Nicaragua", "NIC", "+505", "https://flagcdn.com/w320/ni.png"),
                new CountryData("Panama", "Republic of Panama", "PAN", "+507", "https://flagcdn.com/w320/pa.png"),
                new CountryData("Saint Kitts and Nevis", "Federation of Saint Christopher and Nevis", "KNA", "+1-869", "https://flagcdn.com/w320/kn.png"),
                new CountryData("Saint Lucia", "Saint Lucia", "LCA", "+1-758", "https://flagcdn.com/w320/lc.png"),
                new CountryData("Saint Vincent and the Grenadines", "Saint Vincent and the Grenadines", "VCT", "+1-784", "https://flagcdn.com/w320/vc.png"),
                new CountryData("Trinidad and Tobago", "Republic of Trinidad and Tobago", "TTO", "+1-868", "https://flagcdn.com/w320/tt.png"),
                new CountryData("United States", "United States of America", "USA", "+1", "https://flagcdn.com/w320/us.png")
        ));

        // =================== SOUTH AMERICA (12 countries) ===================
        allCountries.addAll(Arrays.asList(
                new CountryData("Argentina", "Argentine Republic", "ARG", "+54", "https://flagcdn.com/w320/ar.png"),
                new CountryData("Bolivia", "Plurinational State of Bolivia", "BOL", "+591", "https://flagcdn.com/w320/bo.png"),
                new CountryData("Brazil", "Federative Republic of Brazil", "BRA", "+55", "https://flagcdn.com/w320/br.png"),
                new CountryData("Chile", "Republic of Chile", "CHL", "+56", "https://flagcdn.com/w320/cl.png"),
                new CountryData("Colombia", "Republic of Colombia", "COL", "+57", "https://flagcdn.com/w320/co.png"),
                new CountryData("Ecuador", "Republic of Ecuador", "ECU", "+593", "https://flagcdn.com/w320/ec.png"),
                new CountryData("Guyana", "Co-operative Republic of Guyana", "GUY", "+592", "https://flagcdn.com/w320/gy.png"),
                new CountryData("Paraguay", "Republic of Paraguay", "PRY", "+595", "https://flagcdn.com/w320/py.png"),
                new CountryData("Peru", "Republic of Peru", "PER", "+51", "https://flagcdn.com/w320/pe.png"),
                new CountryData("Suriname", "Republic of Suriname", "SUR", "+597", "https://flagcdn.com/w320/sr.png"),
                new CountryData("Uruguay", "Oriental Republic of Uruguay", "URY", "+598", "https://flagcdn.com/w320/uy.png"),
                new CountryData("Venezuela", "Bolivarian Republic of Venezuela", "VEN", "+58", "https://flagcdn.com/w320/ve.png")
        ));

        // =================== OCEANIA (14 countries) ===================
        allCountries.addAll(Arrays.asList(
                new CountryData("Australia", "Commonwealth of Australia", "AUS", "+61", "https://flagcdn.com/w320/au.png"),
                new CountryData("Fiji", "Republic of Fiji", "FJI", "+679", "https://flagcdn.com/w320/fj.png"),
                new CountryData("Kiribati", "Republic of Kiribati", "KIR", "+686", "https://flagcdn.com/w320/ki.png"),
                new CountryData("Marshall Islands", "Republic of the Marshall Islands", "MHL", "+692", "https://flagcdn.com/w320/mh.png"),
                new CountryData("Micronesia", "Federated States of Micronesia", "FSM", "+691", "https://flagcdn.com/w320/fm.png"),
                new CountryData("Nauru", "Republic of Nauru", "NRU", "+674", "https://flagcdn.com/w320/nr.png"),
                new CountryData("New Zealand", "New Zealand", "NZL", "+64", "https://flagcdn.com/w320/nz.png"),
                new CountryData("Palau", "Republic of Palau", "PLW", "+680", "https://flagcdn.com/w320/pw.png"),
                new CountryData("Papua New Guinea", "Independent State of Papua New Guinea", "PNG", "+675", "https://flagcdn.com/w320/pg.png"),
                new CountryData("Samoa", "Independent State of Samoa", "WSM", "+685", "https://flagcdn.com/w320/ws.png"),
                new CountryData("Solomon Islands", "Solomon Islands", "SLB", "+677", "https://flagcdn.com/w320/sb.png"),
                new CountryData("Tonga", "Kingdom of Tonga", "TON", "+676", "https://flagcdn.com/w320/to.png"),
                new CountryData("Tuvalu", "Tuvalu", "TUV", "+688", "https://flagcdn.com/w320/tv.png"),
                new CountryData("Vanuatu", "Republic of Vanuatu", "VUT", "+678", "https://flagcdn.com/w320/vu.png")
        ));

        logger.info("📋 Loaded {} countries total", allCountries.size());
        return allCountries;
    }

    /**
     * Data holder class for country information
     */
    private static class CountryData {
        String name;
        String description;
        String isoCode;
        String phoneCode;
        String flagUrl;

        CountryData(String name, String description, String isoCode, String phoneCode, String flagUrl) {
            this.name = name;
            this.description = description;
            this.isoCode = isoCode;
            this.phoneCode = phoneCode;
            this.flagUrl = flagUrl;
        }
    }
}