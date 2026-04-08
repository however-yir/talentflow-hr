package io.liuzhuoran.talentflow.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class AuthAndMenuIntegrationTest {

    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("talentflow_hr")
            .withUsername("talentflow")
            .withPassword("talentflow");

    @Container
    private static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", AuthAndMenuIntegrationTest::jdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.redis.host", REDIS::getHost);
        registry.add("spring.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.task.scheduling.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("update hr set password = ? where username = ?", passwordEncoder.encode("123"), "admin");
        stringRedisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    void doLoginShouldSucceedWithMysqlBackedUser() throws Exception {
        mockMvc.perform(post("/doLogin")
                        .session(verifyCodeSession())
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"123\",\"code\":\"abcd\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("登录成功!"))
                .andExpect(jsonPath("$.obj.username").value("admin"));
    }

    @Test
    void systemMenuEndpointShouldReturnMenuAfterLoginAndPopulateRedisCache() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/doLogin")
                        .session(verifyCodeSession())
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"123\",\"code\":\"abcd\"}"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession authenticatedSession = (MockHttpSession) loginResult.getRequest().getSession(false);

        mockMvc.perform(get("/system/config/menu").session(authenticatedSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists());

        assertThat(jdbcTemplate.queryForObject("select count(*) from hr", Integer.class)).isGreaterThan(0);
        assertThat(stringRedisTemplate.getConnectionFactory().getConnection().ping()).isEqualTo("PONG");
        Set<String> cacheKeys = stringRedisTemplate.keys("menus_cache*");
        assertThat(cacheKeys).isNotNull();
        assertThat(cacheKeys).isNotEmpty();
    }

    private MockHttpSession verifyCodeSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("verify_code", "ABCD");
        return session;
    }

    private static String jdbcUrl() {
        String url = MYSQL.getJdbcUrl();
        if (url.contains("?")) {
            return url + "&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        }
        return url + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
    }
}
