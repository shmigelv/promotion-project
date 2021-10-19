package util;

import com.shmigel.promotionproject.Application;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Transactional
@AutoConfigureMockMvc
@AutoConfigureEmbeddedDatabase
@SpringBootTest(classes = {Application.class, TestUtil.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultTestConfiguration {
}
