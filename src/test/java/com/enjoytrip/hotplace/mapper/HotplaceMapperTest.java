package com.enjoytrip.hotplace.mapper;

import com.enjoytrip.hotplace.dto.Hotplace;
import com.enjoytrip.support.AbstractMySqlContainerTest;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HotplaceMapperTest extends AbstractMySqlContainerTest {

    @Autowired
    HotplaceMapper hotplaceMapper;

    @Test
    void insertSelectAllUpdateTitleAndDeleteHotplace() {
        Hotplace hotplace = new Hotplace(
                "ssafy",
                "Mapper hotplace",
                "2026-06-02",
                "cafe",
                "Mapper description",
                37.5665,
                126.9780,
                "/images/mapper.jpg"
        );

        hotplaceMapper.insertHotplace(hotplace);

        assertThat(hotplace.getHotplaceId()).isPositive();
        assertThat(hotplaceMapper.selectAllHotplaces())
                .extracting(Hotplace::getHotplaceId)
                .contains(hotplace.getHotplaceId());

        hotplace.setTitle("Mapper hotplace updated");
        hotplaceMapper.updateHotplace(hotplace);

        Hotplace updated = hotplaceMapper.selectHotplaceById(hotplace.getHotplaceId());
        assertThat(updated.getTitle()).isEqualTo("Mapper hotplace updated");

        hotplaceMapper.deleteHotplace(hotplace.getHotplaceId());

        assertThat(hotplaceMapper.selectHotplaceById(hotplace.getHotplaceId())).isNull();
    }
}
