package com.hfut.cat_adoption_system;

import com.hfut.cat_adoption_system.service.CatAdoptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:cat_adoption_system;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always"
})
class CatAdoptionSystemApplicationTests {

    @Autowired
    private CatAdoptionService service;

    @Test
    void contextLoads() {
        assertThat(service.dashboard().catCount()).isEqualTo(8);
        assertThat(service.listCats(null, "蛋黄")).hasSize(1);
        assertThat(service.listCats(null, "德园")).hasSize(3);
        assertThat(service.listCatPhotos("CAT260501001")).hasSize(3);
        assertThat(service.locationStats()).isNotEmpty();
        assertThat(service.listProducts()).hasSize(4);
        assertThat(service.getDonationChannel().qrUrl()).isEqualTo("/uploads/catalog/alipay-qr.jpg");
        assertThat(service.listApplicationDetails(null)).hasSize(1);
        assertThat(service.toggleFavorite("U2026050703", "CAT260501001")).isTrue();
        assertThat(service.listFavoriteCats("U2026050703")).hasSize(1);
    }
}
